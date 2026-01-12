package com.mycompany.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import com.sun.net.httpserver.HttpExchange; // Importante para el handler
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import com.sun.net.httpserver.HttpServer;
import java.util.List;

public class WebServer {

    private static final int PORT = 3002;

    // --- CONFIGURACIÓN DE BASE DE DATOS ---
    // (Asegúrate de que esta URL sea la correcta de tu RDS)
    private static final String DB_URL = "jdbc:mysql://banco-distribuido-db.c70mis2eismq.us-east-2.rds.amazonaws.com:3306/banco_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password123";
    private static final String ACTUATOR_ENDPOINT = "/actuator/metrics/system.cpu.usage";
    // --- CONFIGURACIÓN SQS (PON TU URL AQUÍ) ---
    private static final String QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/376784885737/transacciones-queue";
    
    // --- CREDENCIALES AWS (PON TUS CREDENCIALES AQUÍ) ---
    private static final String AWS_ACCESS_KEY = "AKIAVPORBC7U7M3A3VH5";
    private static final String AWS_SECRET_KEY = "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";

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public WebServer(int port) {
        this.objectMapper = new ObjectMapper();
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_2) // Verifica que sea tu región (Ohio)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY)
                ))
                .build();

        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (Exception e) { e.printStackTrace(); }
    }

    public void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext(ACTUATOR_ENDPOINT).setHandler(this::handleCpuRequest);
            server.createContext("/status").setHandler(exchange -> {
                String response = "TransactionService Operativo";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.start();
            System.out.println("TransactionService monitor iniciado en puerto: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(this::escucharColaSQS).start();
    }

    private void escucharColaSQS() {
        System.out.println("Iniciando escucha de SQS...");
        while (true) {
            try {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(20)
                        .build();

                List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

                for (Message message : messages) {
                    System.out.println(">>> Procesando mensaje: " + message.body());
                    procesarTransaccion(message.body());
                    deleteMessage(message);
                }
            } catch (Exception e) {
                System.err.println("Error SQS: " + e.getMessage());
            }
        }
    }

    private void procesarTransaccion(String jsonBody) {
        Connection conn = null;
        try {
            JsonNode json = objectMapper.readTree(jsonBody);

            if (json.has("Message") && json.has("Type") && "Notification".equals(json.get("Type").asText())) {
                json = objectMapper.readTree(json.get("Message").asText());
            }
            
            String origen = json.get("origen").asText();
            String destino = json.get("destino").asText();
            double monto = json.get("monto").asDouble();

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.setAutoCommit(false); // Inicia transacción SQL

            // 1. Restar al origen (Tabla accounts)
            try (PreparedStatement stmtResta = conn.prepareStatement("UPDATE accounts SET saldo = saldo - ? WHERE curp = ?")) {
                stmtResta.setDouble(1, monto);
                stmtResta.setString(2, origen);
                stmtResta.executeUpdate();
            }

            // 2. Sumar al destino (Tabla accounts)
            try (PreparedStatement stmtSuma = conn.prepareStatement("UPDATE accounts SET saldo = saldo + ? WHERE curp = ?")) {
                stmtSuma.setDouble(1, monto);
                stmtSuma.setString(2, destino);
                stmtSuma.executeUpdate();
            }

            // 3. Registrar el movimiento (Tabla transactions)
            // --- AQUÍ ESTABA EL ERROR, YA CORREGIDO ---
            String sqlInsert = "INSERT INTO transactions (origen_curp, destino_curp, monto, status) VALUES (?, ?, ?, 'EXITO')";
            
            try (PreparedStatement stmtHist = conn.prepareStatement(sqlInsert)) {
                stmtHist.setString(1, origen);
                stmtHist.setString(2, destino);
                stmtHist.setDouble(3, monto);
                stmtHist.executeUpdate();
            }

            conn.commit(); // Confirmar cambios
            System.out.println("--- Transacción Completada con éxito ---");

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { 
                    conn.rollback(); 
                    System.out.println("!!! Rollback: Se devolvió el dinero por error !!!");
                } catch (Exception ex) {}
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }

    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .receiptHandle(message.receiptHandle())
                .build());
    }
    // ---------------------------------------------------
    // MÉTODOS PARA EL MONITOR (CPU)
    // ---------------------------------------------------
    private void handleCpuRequest(HttpExchange exchange) throws IOException {
        String jsonResponse = obtenerCpuJson();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResponse.getBytes());
        os.close();
    }

    private String obtenerCpuJson() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double cpu = osBean.getSystemCpuLoad(); 
            if (Double.isNaN(cpu)) cpu = 0.0;
            return "{\"measurements\":[{\"statistic\":\"VALUE\",\"value\":" + cpu + "}]}";
        } catch (Exception e) {
            return "{\"measurements\":[{\"statistic\":\"VALUE\",\"value\":0.0}]}";
        }
    }
}