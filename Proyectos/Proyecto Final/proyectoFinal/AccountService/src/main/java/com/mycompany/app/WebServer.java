package com.mycompany.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;

public class WebServer {
    
    private static final int PORT = 3001; 

    //Base de datos
    private static final String DB_URL = "jdbc:mysql://banco-distribuido-db.c70mis2eismq.us-east-2.rds.amazonaws.com:3306/banco_db"; 
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password123";
    private static final String ACTUATOR_ENDPOINT = "/actuator/metrics/system.cpu.usage";
    //SNS
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-2:376784885737:transacciones-topic"; 
    private static final String AWS_ACCESS_KEY = "AKIAVPORBC7U7M3A3VH5"; 
    private static final String AWS_SECRET_KEY = "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";  

    private HttpServer server;
    private final ObjectMapper objectMapper;
    private final SnsClient snsClient;

    public WebServer(int port) {
        this.objectMapper = new ObjectMapper();
        // Formato de fecha para que se vea bien en JSON
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        
        this.snsClient = SnsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
                .build();
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (Exception e) {}
    }

    private boolean manejarCORS(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    public void startServer() {
        
        try {
            this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            //endpoint cliente
            server.createContext(ACTUATOR_ENDPOINT).setHandler(this::handleCpuRequest);
            server.createContext("/balance").setHandler(this::handleBalanceRequest);
            server.createContext("/deposit").setHandler(this::handleDepositRequest);
            server.createContext("/withdraw").setHandler(this::handleWithdrawRequest);
            server.createContext("/transfer").setHandler(this::handleTransferRequest);
            server.createContext("/transactions").setHandler(this::handleGetTransactions);
            server.createContext("/status").setHandler(exchange -> sendResponse("AccountService OK".getBytes(), exchange, 200));
            
            //endpoint  admin
            server.createContext("/admin/users").setHandler(this::handleAdminUsers);
            server.createContext("/admin/total-balance").setHandler(this::handleAdminTotal);
            server.createContext("/admin/stats").setHandler(this::handleAdminStats);
            server.createContext("/admin/transactions").setHandler(this::handleAdminTransactions);
            
            server.setExecutor(Executors.newFixedThreadPool(8));
            server.start();
            System.out.println("AccountService (Admin Full) iniciado en puerto: " + PORT);
        } catch (IOException e) { e.printStackTrace(); }
    }

    
    private void handleAdminTransactions(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        
        // Query: Trae TODAS las transacciones
        String sql = "SELECT * FROM transactions ORDER BY fecha DESC";
        List<Map<String, Object>> logs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("id", String.valueOf(rs.getInt("id")));
                
                Timestamp ts = rs.getTimestamp("fecha");
                log.put("timestamp", ts != null ? ts.toString() : ""); 

                log.put("origen", rs.getString("origen_curp"));
                log.put("destino", rs.getString("destino_curp"));
                log.put("monto", rs.getDouble("monto"));
                log.put("status", rs.getString("status")); 
                
                logs.add(log);
            }
            sendResponse(objectMapper.writeValueAsBytes(logs), exchange, 200);

        } catch (Exception e) { 
            e.printStackTrace();
            sendError("Error logs: " + e.getMessage(), exchange, 500); 
        }
    }

    //Admin
    private void handleAdminUsers(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        String sql = "SELECT u.curp, u.nombre, IFNULL(a.saldo, 0) as saldo FROM users u LEFT JOIN accounts a ON u.curp = a.curp";
        List<Map<String, Object>> usuarios = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("curp", rs.getString("curp"));
                user.put("name", rs.getString("nombre"));
                user.put("balance", rs.getDouble("saldo"));
                usuarios.add(user);
            }
            sendResponse(objectMapper.writeValueAsBytes(usuarios), exchange, 200);
        } catch (Exception e) { sendError(e.getMessage(), exchange, 500); }
    }

    private void handleAdminTotal(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT SUM(saldo) as total FROM accounts");
             ResultSet rs = stmt.executeQuery()) {
            double total = 0;
            if (rs.next()) total = rs.getDouble("total");
            sendResponse(("{\"total\": " + total + "}").getBytes(), exchange, 200);
        } catch (Exception e) { sendError(e.getMessage(), exchange, 500); }
    }

    //Cliente
    private void handleGetTransactions(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.close(); return; }

        try {
            AccountRequest req = objectMapper.readValue(exchange.getRequestBody(), AccountRequest.class);
            String miCurp = req.getCurp();
            String sql = "SELECT * FROM transactions WHERE origen_curp = ? OR destino_curp = ? ORDER BY fecha DESC";
            
            List<Map<String, Object>> historial = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, miCurp);
                stmt.setString(2, miCurp);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> tx = new HashMap<>();
                    tx.put("id", String.valueOf(rs.getInt("id")));
                    Timestamp ts = rs.getTimestamp("fecha");
                    tx.put("timestamp", ts != null ? ts.toString() : ""); 
                    double monto = rs.getDouble("monto");
                    String origen = rs.getString("origen_curp");
                    String destino = rs.getString("destino_curp");

                    if (miCurp.equals(origen)) {
                        tx.put("type", "Envío"); tx.put("amount", -monto); tx.put("counterparty", destino);
                    } else {
                        tx.put("type", "Depósito"); tx.put("amount", monto); tx.put("counterparty", origen);
                    }
                    historial.add(tx);
                }
            }
            sendResponse(objectMapper.writeValueAsBytes(historial), exchange, 200);
        } catch (Exception e) { sendError(e.getMessage(), exchange, 500); }
    }

    private void handleTransferRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.close(); return; }
        try {
            AccountRequest req = objectMapper.readValue(exchange.getRequestBody(), AccountRequest.class);
            String jsonMensaje = String.format("{\"origen\":\"%s\", \"destino\":\"%s\", \"monto\":%.2f}", req.getCurp(), req.getDestino(), req.getMonto());
            PublishRequest publishRequest = PublishRequest.builder().topicArn(TOPIC_ARN).message(jsonMensaje).build();
            snsClient.publish(publishRequest);
            sendResponse("{\"mensaje\": \"Transferencia en proceso\", \"status\": \"PENDIENTE\"}".getBytes(), exchange, 200);
        } catch (Exception e) { sendError(e.getMessage(), exchange, 500); }
    }

    private void handleBalanceRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        try {
            AccountRequest req = objectMapper.readValue(exchange.getRequestBody(), AccountRequest.class);
            double saldo = getSaldo(req.getCurp());
            if(saldo == -1) saldo = 0;
            sendResponse(("{\"mensaje\":\"OK\", \"saldoActual\":" + saldo + "}").getBytes(), exchange, 200);
        } catch (Exception e) { sendError(e.getMessage(), exchange, 500); }
    }

    private void handleDepositRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        try {
            AccountRequest req = objectMapper.readValue(exchange.getRequestBody(), AccountRequest.class);
            if(actualizarSaldo(req.getCurp(), req.getMonto(), true)) sendResponse("{\"mensaje\":\"OK\"}".getBytes(), exchange, 200);
            else sendError("Error BD", exchange, 400);
        } catch(Exception e){ sendError(e.getMessage(), exchange, 500); }
    }

    private void handleWithdrawRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        try {
            AccountRequest req = objectMapper.readValue(exchange.getRequestBody(), AccountRequest.class);
            if (getSaldo(req.getCurp()) >= req.getMonto()) {
                if (actualizarSaldo(req.getCurp(), req.getMonto(), false)) sendResponse("{\"mensaje\":\"OK\"}".getBytes(), exchange, 200);
                else sendError("Error BD", exchange, 500);
            } else sendError("Fondos insuficientes", exchange, 400);
        } catch (Exception e) { sendError(e.getMessage(), exchange, 500); }
    }


    private double getSaldo(String curp) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT saldo FROM accounts WHERE curp = ?")) {
            stmt.setString(1, curp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("saldo");
        } catch (Exception e) {}
        return -1;
    }

    private boolean actualizarSaldo(String curp, double monto, boolean esDeposito) {
        String sql = esDeposito ? "UPDATE accounts SET saldo = saldo + ? WHERE curp = ?" : "UPDATE accounts SET saldo = saldo - ? WHERE curp = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, monto);
            stmt.setString(2, curp);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
    private void sendError(String msj, HttpExchange ex, int code) throws IOException {
        sendResponse(("{\"error\": \"" + msj + "\"}").getBytes(), ex, code);
    }

    public static class AccountRequest {
        private String curp, destino; private double monto;
        public String getCurp() { return curp; } public void setCurp(String c) { this.curp = c; }
        public String getDestino() { return destino; } public void setDestino(String d) { this.destino = d; }
        public double getMonto() { return monto; } public void setMonto(double m) { this.monto = m; }
    }

    //Estadísticas y gráficas
    private void handleAdminStats(com.sun.net.httpserver.HttpExchange exchange) throws java.io.IOException {
        
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String sql = "SELECT DATE(fecha) as dia, COUNT(*) as cantidad, SUM(monto) as total " +
                     "FROM transactions GROUP BY DATE(fecha) ORDER BY dia ASC LIMIT 30";
        
        java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
        java.util.List<java.util.Map<String, Object>> seriesCount = new java.util.ArrayList<>();
        java.util.List<java.util.Map<String, Object>> seriesAmount = new java.util.ArrayList<>();

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String dia = rs.getString("dia");
                
                // Gráfica 1 Cantidad
                java.util.Map<String, Object> p1 = new java.util.HashMap<>();
                p1.put("bucket", dia);
                p1.put("value", rs.getInt("cantidad"));
                seriesCount.add(p1);

                // Gráfica 2 Dinero
                java.util.Map<String, Object> p2 = new java.util.HashMap<>();
                p2.put("bucket", dia);
                p2.put("value", rs.getDouble("total"));
                seriesAmount.add(p2);
            }
            
            respuesta.put("seriesCount", seriesCount);
            respuesta.put("seriesAmount", seriesAmount);
            
            // Enviar JSON
            byte[] bytes = objectMapper.writeValueAsBytes(respuesta);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();

        } catch (Exception e) { 
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
    // ---------------------------------------------------
    // MÉTODOS PARA EL MONITOR (CPU)
    // ---------------------------------------------------
    private void handleCpuRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        String jsonResponse = obtenerCpuJson();
        sendResponse(jsonResponse.getBytes(), exchange, 200);
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