/*
 *  MIT License
 *  Copyright (c) 2019 Michael Pogrebinsky
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
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String QUOTES_ENDPOINT = "/quotes";
    private static final String POSTS_ENDPOINT = "/posts"; // Item 2
    private static final String TRANSLATE_ENDPOINT = "/translate"; // Item 3

    private final int port;
    private HttpServer server;

    public static void main(String[] args) {
        int serverPort = 8080; // puerto del servidor
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]); // puerto por línea de comandos
        }

        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public WebServer(int port) { this.port = port; }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0); // socket HTTP
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext quotesContext = server.createContext(QUOTES_ENDPOINT);
        HttpContext postsContext = server.createContext(POSTS_ENDPOINT); // Item 2
        HttpContext translateContext = server.createContext(TRANSLATE_ENDPOINT); // Item 3

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        quotesContext.setHandler(this::handleQuotesRequest);
        postsContext.setHandler(this::handlePostsRequest); // Item 2
        translateContext.setHandler(this::handleTranslateRequest); // Item 3

        server.setExecutor(Executors.newFixedThreadPool(8)); // 8 hilos
        server.start();
    }

    // ===================== /quotes =====================
    private void handleQuotesRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
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
            byte[] bytes = "{\"error\":\"Interrupted while calling upstream\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }
    // ==================================================

    // ===================== /posts (Item 2) =====================
    private void handlePostsRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        int userId = 1; // según enunciado
        String group = "GRUPO-EQUIPO"; // default
        String name = "Nombre del alumno"; // default
        String jsonPayload;

        if (method.equals("GET")) {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            group = params.getOrDefault("group", group);
            name = params.getOrDefault("name", name);
            jsonPayload = String.format("{\"userId\":%d,\"title\":\"%s\",\"body\":\"%s\"}",
                    userId, escapeJson(group), escapeJson(name));
        } else if (method.equals("POST")) {
            byte[] reqBytes = exchange.getRequestBody().readAllBytes();
            String body = new String(reqBytes, StandardCharsets.UTF_8).trim();
            if (!body.isEmpty()) {
                jsonPayload = body; // ya viene JSON formado por el cliente
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
            byte[] bytes = "{\"error\":\"Interrupted while calling upstream\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }
    // =============================================================

    // ===================== /translate (Item 3) =====================
    private void handleTranslateRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.sendResponseHeaders(405, -1); exchange.close(); return;
        }
        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());

        // Texto a traducir (default = frase de la práctica)
        String text = params.getOrDefault("text",
                "People have the right to disagree with your opinions and to dissent.");
        // Idioma destino (default: español)
        String target = params.getOrDefault("target", "es");

        // API key: por seguridad tomamos primero variable de entorno; si no, aceptamos ?key=...
        String apiKey = System.getenv("GCP_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = params.getOrDefault("key", "");
        }
        if (apiKey == null || apiKey.isBlank()) {
            byte[] msg = "{\"error\":\"Falta API key. Define GCP_API_KEY en el entorno o pásala como ?key=...\"}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, msg.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(msg); }
            return;
        }

        // Construimos URL con parámetros (Importante: NO encerrar q entre comillas en Java)
        String encodedQ = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = String.format(
                "https://translation.googleapis.com/language/translate/v2?target=%s&key=%s&q=%s",
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
            byte[] bytes = "{\"error\":\"Interrupted while calling upstream\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }
    // =============================================================

    // ===================== Utilidades =====================
    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx == -1) { map.put(urlDecode(pair), ""); }
            else {
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

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) { exchange.close(); return; }

        // ====== Ejercicio 3: imprimir cantidad de headers y todos los pares key-value ======
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

        // Leer cuerpo y registrarlo
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        System.out.println("Bytes recibidos en el cuerpo: " + requestBytes.length);
        System.out.println("Contenido del cuerpo: " + new String(requestBytes, StandardCharsets.UTF_8));

        // DEBUG ON?
        boolean isDebugMode = headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true");
        long startTime = System.nanoTime();

        // ---- Trabajo simulado ----
        // try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }

        // Calcula respuesta
        byte[] responseBytes = calculateResponse(requestBytes);
        long finishTime = System.nanoTime();
        long elapsedNs = finishTime - startTime;

        // ====== Ejercicio 4: header con ns + segundos y milisegundos ======
        if (isDebugMode) {
            long seconds = elapsedNs / 1_000_000_000L;
            long milliseconds = (elapsedNs % 1_000_000_000L) / 1_000_000L;
            String debugMessage = String.format("La operacion tomo %d nanosegundos = %d segundos con %d milisegundos.",
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
            BigInteger bigInteger = new BigInteger(number.trim());
            result = result.multiply(bigInteger);
        }
        return String.format("El resultado de la multiplicación es %s%n", result).getBytes(StandardCharsets.UTF_8);
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) { exchange.close(); return; }
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
