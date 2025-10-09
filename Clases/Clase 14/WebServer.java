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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";

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

    public WebServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0); // socket HTTP
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);

        server.setExecutor(Executors.newFixedThreadPool(8)); // 8 hilos
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

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
            if (vals == null || vals.isEmpty()) {
                System.out.println("Header: " + k + " = <sin valor>");
            } else {
                for (String v : vals) {
                    System.out.println("Header: " + k + " = " + v);
                }
            }
        });
        // ================================================================================

        // Leer cuerpo y registrarlo
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        System.out.println("Bytes recibidos en el cuerpo: " + requestBytes.length);
        System.out.println("Contenido del cuerpo: " + new String(requestBytes, StandardCharsets.UTF_8));

        // DEBUG ON?
        boolean isDebugMode = headers.containsKey("X-Debug")
                && headers.get("X-Debug").get(0).equalsIgnoreCase("true");

        long startTime = System.nanoTime();

        // ---- Trabajo simulado (activa si quieres ver tiempos mayores) ----
        // try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        // ------------------------------------------------------------------

        // Calcula respuesta
        byte[] responseBytes = calculateResponse(requestBytes);

        long finishTime = System.nanoTime();
        long elapsedNs = finishTime - startTime;

        // ====== Ejercicio 4: header con ns + segundos y milisegundos ======
        if (isDebugMode) {
            long seconds = elapsedNs / 1_000_000_000L;
            long milliseconds = (elapsedNs % 1_000_000_000L) / 1_000_000L;
            String debugMessage = String.format(
                    "La operacion tomo %d nanosegundos = %d segundos con %d milisegundos.",
                    elapsedNs, seconds, milliseconds
            );
            exchange.getResponseHeaders().add("X-Debug-Info", debugMessage);
        }
        // =================================================================

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

        return String.format("El resultado de la multiplicación es %s%n", result)
                .getBytes(StandardCharsets.UTF_8);
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(StandardCharsets.UTF_8), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
}
