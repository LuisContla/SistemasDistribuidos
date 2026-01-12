package com.mycompany.app;

import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import com.sun.net.httpserver.HttpExchange;
public class WebServer {

    private static final int PORT = 3003;

    //Base de datos
    private static final String DB_URL = "jdbc:mysql://banco-distribuido-db.c70mis2eismq.us-east-2.rds.amazonaws.com:3306/banco_db"; // <<<< TU DB
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password123";
    private static final String ACTUATOR_ENDPOINT = "/actuator/metrics/system.cpu.usage";
    //SQS transacciones-queue
    private static final String QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/376784885737/Auditoria-queue"; 
    
    //Credenciales del IAM
    private static final String AWS_ACCESS_KEY = "AKIAVPORBC7U7M3A3VH5";  
    private static final String AWS_SECRET_KEY = "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";  
    
    private final SqsClient sqsClient;
    
    public WebServer(int port) {
        // Configuración Cliente SQS
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_2)
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
                String response = "AuditService Operativo (Puerto " + PORT + ")";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.start();
            System.out.println("AuditService monitor iniciado en puerto: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(this::escucharColaSQS).start();
    }

    private void escucharColaSQS() {
        System.out.println("Auditor escuchando en: " + QUEUE_URL);
        
        while (true) {
            try {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .maxNumberOfMessages(5) 
                        .waitTimeSeconds(20) 
                        .build();

                List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

                for (Message message : messages) {
                    System.out.println(">>> [AUDIT] Mensaje capturado: " + message.body());
                    
                    // Guardamos el JSON crudo en la tabla de auditoría
                    guardarAuditoria(message.body());

                    deleteMessage(message);
                }
            } catch (Exception e) {
                System.err.println("Error Audit SQS: " + e.getMessage());
                try { Thread.sleep(5000); } catch (InterruptedException ie) {} 
            }
        }
    }

    private void guardarAuditoria(String jsonBody) {
        String sql = "INSERT INTO audit_logs (evento_json, origen) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            
            String mensajeLimpio = jsonBody;

            try {
                com.fasterxml.jackson.databind.JsonNode rootNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(jsonBody);

                if (rootNode.has("Message") && rootNode.get("Message").isTextual()) {
                    mensajeLimpio = rootNode.get("Message").asText(); 
                    // Esto convierte: 
                    // {"Type":"Notification", "Message":"{\"origen\":\"A\"...}"} 
                    // en solo: 
                    // {"origen":"A", "destino":"B", "monto":50}
                }
            } catch (Exception e) {
                System.err.println("No se pudo limpiar el JSON (se guardará crudo): " + e.getMessage());
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, mensajeLimpio); // <--- Guardamos el limpio
                stmt.setString(2, "AuditService");
                stmt.executeUpdate();
                System.out.println("--- [AUDIT] Evidencia guardada LIMPIA en BD ---");
            }
            
        } catch (Exception e) {
            System.err.println("Error SQL Audit: " + e.getMessage());
        }
    }

    private void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(QUEUE_URL)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteRequest);
        } catch (Exception e) {
            System.err.println("Error borrar SQS: " + e.getMessage());
        }
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