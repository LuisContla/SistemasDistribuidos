package com.mycompany.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.concurrent.Executors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;
import java.security.Key;

public class WebServer {
    
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String ACTUATOR_ENDPOINT = "/actuator/metrics/system.cpu.usage";
    private final int port;
    private HttpServer server;
    private final ObjectMapper objectMapper;
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //Base de datos
    private static final String DB_URL = "jdbc:mysql://banco-distribuido-db.c70mis2eismq.us-east-2.rds.amazonaws.com/banco_db"; 
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password123"; 

    public WebServer(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
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
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(ACTUATOR_ENDPOINT).setHandler(this::handleCpuRequest);
            server.createContext(STATUS_ENDPOINT).setHandler(this::handleStatusCheckRequest);
            server.createContext(LOGIN_ENDPOINT).setHandler(this::handleLoginRequest);
            server.createContext(REGISTER_ENDPOINT).setHandler(this::handleRegisterRequest);
            server.setExecutor(Executors.newFixedThreadPool(8));
            server.start();
            System.out.println("AuthService (Con Roles) iniciado en puerto: " + port);
        } catch (IOException e) { e.printStackTrace(); }
    }

    //Login
    private void handleLoginRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.close(); return; }

        try {
            LoginRequest request = objectMapper.readValue(exchange.getRequestBody(), LoginRequest.class);
            
            // Validar usuario y OBTENER ROL
            String rolDetectado = validarUsuarioYObtenerRol(request.getCurp(), request.getPassword());
            
            if (rolDetectado != null) {
                String token = generarToken(request.getCurp(), rolDetectado);
                
                // Enviamos el rol al frontend
                LoginResponse responseObj = new LoginResponse(token, "Login Exitoso", rolDetectado);
                
                byte[] responseBytes = objectMapper.writeValueAsBytes(responseObj);
                sendResponse(responseBytes, exchange, 200);
            } else {
                sendResponse("{\"mensaje\": \"Credenciales incorrectas\"}".getBytes(), exchange, 401);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(("{\"mensaje\": \"Error: " + e.getMessage() + "\"}").getBytes(), exchange, 500);
        }
    }

    //Registro
    private void handleRegisterRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return;
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.close(); return; }

        try {
            RegisterRequest req = objectMapper.readValue(exchange.getRequestBody(), RegisterRequest.class);
            String passwordHash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

            // Guardamos con rol 'user' por defecto
            if (registrarUsuarioEnBD(req.getCurp(), req.getName(), passwordHash, "user")) {
                sendResponse("{\"mensaje\": \"Registrado\"}".getBytes(), exchange, 200);
                crearCuentaBancaria(req.getCurp());
            } else {
                sendResponse("{\"mensaje\": \"Error al registrar\"}".getBytes(), exchange, 400);
            }
        } catch (Exception e) {
            sendResponse(("{\"mensaje\": \"Error: " + e.getMessage() + "\"}").getBytes(), exchange, 500);
        }
    }

    //Métodos para la base de datos
    private String validarUsuarioYObtenerRol(String curp, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT password_hash, rol FROM users WHERE curp = ?")) {
            stmt.setString(1, curp);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                String rol = rs.getString("rol");
                if (BCrypt.checkpw(password, hash)) {
                    return rol;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private boolean registrarUsuarioEnBD(String curp, String nombre, String hash, String rol) {
        String sql = "INSERT INTO users (curp, nombre, password_hash, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curp);
            stmt.setString(2, nombre);
            stmt.setString(3, hash);
            stmt.setString(4, rol);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    private void crearCuentaBancaria(String curp) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO accounts (curp, saldo) VALUES (?, 0.0)")) {
            stmt.setString(1, curp);
            stmt.executeUpdate();
        } catch (Exception e) {}
    }
    private String generarToken(String curp, String rol) {
        return Jwts.builder()
                .setSubject(curp)
                .claim("role", rol) // Guardamos el rol dentro del token también
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(KEY)
                .compact();
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        sendResponse("AuthService OK".getBytes(), exchange, 200);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
    
    public static class LoginRequest {
        private String curp; private String password;
        public String getCurp() { return curp; } public void setCurp(String curp) { this.curp = curp; }
        public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    }
    public static class RegisterRequest {
        private String curp; private String name; private String password;
        public String getCurp() { return curp; } public void setCurp(String curp) { this.curp = curp; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    }
    public static class LoginResponse {
        public String token;
        public String message;
        public String role; // <--- Nuevo campo para el frontend
        public LoginResponse(String t, String m, String r) { this.token=t; this.message=m; this.role=r; }
    }
    // Método para obtener CPU real sin Spring Boot
    private String obtenerCpuJson() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            // El valor viene de 0.0 a 1.0
            double cpu = osBean.getSystemCpuLoad(); 
            
            // Si devuelve NaN (a veces pasa al inicio), mandamos 0
            if (Double.isNaN(cpu)) cpu = 0.0;
            
            // Formato JSON idéntico al que espera el monitor
            // {"measurements":[{"statistic":"VALUE","value":0.45}]}
            return "{\"measurements\":[{\"statistic\":\"VALUE\",\"value\":" + cpu + "}]}";
        } catch (Exception e) {
            return "{\"measurements\":[{\"statistic\":\"VALUE\",\"value\":0.0}]}";
        }
    }
    private void handleCpuRequest(HttpExchange exchange) throws IOException {
        if (manejarCORS(exchange)) return; 
        String jsonResponse = obtenerCpuJson();
        // Usamos .getBytes() para convertir el String a bytes, igual que en tus otros métodos
        sendResponse(jsonResponse.getBytes(), exchange, 200);
    }
}