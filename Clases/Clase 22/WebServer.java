/*
 *  MIT License
 *  Copyright (c) 2019 Michael Pogrebinsky
 */

/*
 * Ejercicio Clase 22
 * Autor: Contla Mota Luis Andrés 
 * Grupo: 7CV3 
 */

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String QUOTES_ENDPOINT = "/quotes";
    private static final String POSTS_ENDPOINT = "/posts";       // Item 2
    private static final String TRANSLATE_ENDPOINT = "/translate"; // Item 3 (Translate)
    private static final String GCS_ENDPOINT = "/gcs";             // Item 3 (GCS JSON API)

    private final int port;
    private HttpServer server;

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }
        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();
        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public WebServer(int port) { this.port = port; }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext quotesContext = server.createContext(QUOTES_ENDPOINT);
        HttpContext postsContext = server.createContext(POSTS_ENDPOINT);
        HttpContext translateContext = server.createContext(TRANSLATE_ENDPOINT);
        HttpContext gcsContext = server.createContext(GCS_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        quotesContext.setHandler(this::handleQuotesRequest);
        postsContext.setHandler(this::handlePostsRequest);
        translateContext.setHandler(this::handleTranslateRequest);
        gcsContext.setHandler(this::handleGcsRequest);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    // ===================== /quotes =====================
    private void handleQuotesRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1); exchange.close(); return;
        }

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.breakingbadquotes.xyz/v1/quotes"))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET().build();
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("=== Breaking Bad API ===");
            System.out.println("Status upstream: " + resp.statusCode());
            System.out.println("-- Headers upstream --");
            resp.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));
            System.out.println("-- Body upstream --\n" + resp.body());

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("X-Upstream-Status", String.valueOf(resp.statusCode()));
            sendResponse(resp.body().getBytes(StandardCharsets.UTF_8), exchange);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            byte[] bytes = jsonError("Interrupted while calling upstream");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    // ===================== /posts (Item 2) =====================
    private void handlePostsRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        int userId = 1;
        String group = "GRUPO-EQUIPO";
        String name = "Nombre del alumno";
        String jsonPayload;

        if ("GET".equals(method)) {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            group = params.getOrDefault("group", group);
            name = params.getOrDefault("name", name);
            jsonPayload = String.format("{\"userId\":%d,\"title\":\"%s\",\"body\":\"%s\"}",
                    userId, escapeJson(group), escapeJson(name));
        } else if ("POST".equals(method)) {
            byte[] reqBytes = exchange.getRequestBody().readAllBytes();
            String body = new String(reqBytes, StandardCharsets.UTF_8).trim();
            if (!isBlank(body)) {
                jsonPayload = body;
            } else {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
                group = params.getOrDefault("group", group);
                name = params.getOrDefault("name", name);
                jsonPayload = String.format("{\"userId\":%d,\"title\":\"%s\",\"body\":\"%s\"}",
                        userId, escapeJson(group), escapeJson(name));
            }
        } else { exchange.sendResponseHeaders(405, -1); exchange.close(); return; }

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        HttpRequest upstream = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        try {
            HttpResponse<String> resp = client.send(upstream, HttpResponse.BodyHandlers.ofString());
            System.out.println("=== JSONPlaceholder /posts ===");
            System.out.println("Payload enviado: " + jsonPayload);
            System.out.println("Status upstream: " + resp.statusCode());
            System.out.println("-- Headers upstream --");
            resp.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));
            System.out.println("-- Body upstream --\n" + resp.body());

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("X-Upstream-Status", String.valueOf(resp.statusCode()));
            sendResponse(resp.body().getBytes(StandardCharsets.UTF_8), exchange);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            byte[] bytes = jsonError("Interrupted while calling upstream");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    // ===================== /translate (Item 3) =====================
    private void handleTranslateRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1); exchange.close(); return;
        }
        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
        String text = params.getOrDefault("text", "People have the right to disagree with your opinions and to dissent.");
        String target = params.getOrDefault("target", "es");

        String apiKey = System.getenv("GCP_API_KEY");
        if (isBlank(apiKey)) apiKey = params.getOrDefault("key", "");
        if (isBlank(apiKey)) {
            byte[] msg = jsonError("Missing API key. Set GCP_API_KEY or pass ?key=");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, msg.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(msg); }
            return;
        }

        String encodedQ = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = String.format("https://translation.googleapis.com/language/translate/v2?target=%s&key=%s&q=%s",
                target, apiKey, encodedQ);

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET().build();
        try {
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("=== Cloud Translation v2 ===");
            System.out.println("URL: " + url.replace(apiKey, "***API_KEY***"));
            System.out.println("Status upstream: " + resp.statusCode());
            System.out.println("-- Headers upstream --");
            resp.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));
            System.out.println("-- Body upstream --\n" + resp.body());

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("X-Upstream-Status", String.valueOf(resp.statusCode()));
            sendResponse(resp.body().getBytes(StandardCharsets.UTF_8), exchange);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            byte[] bytes = jsonError("Interrupted while calling upstream");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    // ===================== /gcs (Item 3: GCS JSON API) =====================
    private void handleGcsRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1); exchange.close(); return;
        }
        Map<String, String> p = parseQuery(exchange.getRequestURI().getQuery());
        String bucket = p.getOrDefault("bucket", "");
        String object = p.getOrDefault("object", "");
        String alt = p.getOrDefault("alt", ""); // "media" para bytes; vacío -> metadatos JSON

        if (isBlank(bucket) || isBlank(object)) {
            byte[] msg = jsonError("Missing bucket and/or object");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(400, msg.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(msg); }
            return;
        }

        String token = System.getenv("GCS_ACCESS_TOKEN");
        if (isBlank(token)) token = p.getOrDefault("token", "");
        if (isBlank(token)) {
            byte[] msg = jsonError("Missing token. Set GCS_ACCESS_TOKEN or pass ?token=");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, msg.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(msg); }
            return;
        }

        // https://storage.googleapis.com/storage/v1/b/<bucket>/o/<object>[?alt=media]
        String encodedObject = URLEncoder.encode(object, StandardCharsets.UTF_8).replace("+", "%20");
        StringBuilder url = new StringBuilder("https://storage.googleapis.com/storage/v1/b/")
                .append(bucket).append("/o/").append(encodedObject);
        if (!isBlank(alt)) url.append("?alt=").append(URLEncoder.encode(alt, StandardCharsets.UTF_8));

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + token)
                .GET().build();
        try {
            HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            System.out.println("=== GCS JSON API ===");
            System.out.println("URL: " + url);
            System.out.println("Status upstream: " + resp.statusCode());
            System.out.println("-- Headers upstream --");
            resp.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));

            String contentType = resp.headers().firstValue("content-type").orElse(
                    (!isBlank(alt) && "media".equalsIgnoreCase(alt)) ? "application/octet-stream" : "application/json; charset=utf-8"
            );
            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.getResponseHeaders().add("X-Upstream-Status", String.valueOf(resp.statusCode()));
            sendResponse(resp.body(), exchange);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            byte[] bytes = jsonError("Interrupted while calling upstream");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    // ===================== Utilidades =====================
    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                map.put(urlDecode(pair), "");
            } else {
                String key = urlDecode(pair.substring(0, idx));
                String val = urlDecode(pair.substring(idx + 1));
                map.put(key, val);
            }
        }
        return map;
    }

    private String urlDecode(String s) { return URLDecoder.decode(s, StandardCharsets.UTF_8); }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static byte[] jsonError(String msg) {
        String safe = escapeJson(msg);
        String json = "{\"error\":\"" + safe + "\"}";
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.close();
            return;
        }

        Headers headers = exchange.getRequestHeaders();
        int headerNames = headers.size();
        int pairCount = 0;
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            List<String> vals = e.getValue();
            pairCount += (vals == null ? 0 : vals.size());
        }
        System.out.println("Total de headers (nombres únicos): " + headerNames);
        System.out.println("Total de pares key-value: " + pairCount);
        headers.forEach((k, vals) -> {
            if (vals == null || vals.isEmpty()) System.out.println("Header: " + k + " = <sin valor>");
            else for (String v : vals) System.out.println("Header: " + k + " = " + v);
        });

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        System.out.println("Bytes recibidos en el cuerpo: " + requestBytes.length);
        System.out.println("Contenido del cuerpo: " + new String(requestBytes, StandardCharsets.UTF_8));

        long startTime = System.nanoTime();
        byte[] responseBytes = calculateResponse(requestBytes);
        long elapsedNs = System.nanoTime() - startTime;

        boolean isDebugMode = headers.containsKey("X-Debug") && !headers.get("X-Debug").isEmpty()
                && "true".equalsIgnoreCase(headers.get("X-Debug").get(0));
        if (isDebugMode) {
            long seconds = elapsedNs / 1_000_000_000L;
            long milliseconds = (elapsedNs % 1_000_000_000L) / 1_000_000L;
            String debugMessage = String.format("La operacion tomo %d ns = %d s con %d ms.",
                    elapsedNs, seconds, milliseconds);
            exchange.getResponseHeaders().add("X-Debug-Info", debugMessage);
        }

        sendResponse(responseBytes, exchange);
    }

    private byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes, StandardCharsets.UTF_8);
        String[] stringNumbers = bodyString.split(",");
        BigInteger result = BigInteger.ONE;
        for (String number : stringNumbers) {
            if (isBlank(number)) continue;
            BigInteger bigInteger = new BigInteger(number.trim());
            result = result.multiply(bigInteger);
        }
        return String.format("El resultado de la multiplicación es %s%n", result)
                .getBytes(StandardCharsets.UTF_8);
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) { exchange.close(); return; }
        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(StandardCharsets.UTF_8), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
            outputStream.flush();
        }
        exchange.close();
    }
}
