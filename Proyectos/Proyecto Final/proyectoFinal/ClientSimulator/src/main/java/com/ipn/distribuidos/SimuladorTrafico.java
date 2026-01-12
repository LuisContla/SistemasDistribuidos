package com.ipn.distribuidos;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimuladorTrafico {

    private static final String AUTH_URL = "http://3.137.141.189/api/auth";
    private static final String ACCOUNT_URL = "http://3.137.141.189/api/account";
    
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Random random = new Random();
    
    // Lista para guardar las CURPs y los Tokens de prueba
    private static final List<String> curps = new ArrayList<>();
    private static final List<String> tokens = new ArrayList<>();

    public static void main(String[] args) {
        // Validación de argumentos
        if (args.length < 4) {
            System.err.println("Uso: java SimuladorTrafico <n_clientes> <h_hilos> <p_monto> <t_transacciones>");
            System.exit(1);
        }

        int n = Integer.parseInt(args[0]); // n clientes
        int h = Integer.parseInt(args[1]); // h hilos
        double p = Double.parseDouble(args[2]); // p monto inicial
        int t = Integer.parseInt(args[3]); // t transacciones por minuto

        if (h <= 1 || h >= 9) System.err.println("⚠️ Advertencia: El PDF pide 1 < h < 9.");
        if (t <= 1 || t >= 60) System.err.println("⚠️ Advertencia: El PDF pide 1 < t < 60.");

        System.out.println("=== SISTEMA DE SIMULACIÓN BANCARIA ===");
        System.out.printf("Clientes: %d | Hilos: %d | Monto Inicial: $%.2f | Tx/Min: %d%n", n, h, p, t);

        crearEscenario(n, p);
        ExecutorService executor = Executors.newFixedThreadPool(h); 

        System.out.println("\n>>> Iniciando bombardeo de transacciones (Infinito) <<<");
        

        for (int i = 0; i < n; i++) {
            final int indexCliente = i; 
            executor.submit(() -> ejecutarCicloCliente(indexCliente, n, t));
        }
    }

    private static void crearEscenario(int n, double monto) {
        System.out.println("--- Preparando " + n + " usuarios simulados ---");
        
        for (int i = 1; i <= n; i++) {
            String curp = "USER_v2" + i; // CAMBIAR CÓMO SE VA A GENERAR CUPR PARA QUE NO HAYA DUPLICADO
            String pass = "password123";
            
            try {
                JsonObject jsonReg = new JsonObject();
                jsonReg.addProperty("curp", curp);
                jsonReg.addProperty("password", pass);
                jsonReg.addProperty("name", "Robot " + i);
                sendPost(AUTH_URL + "/register", jsonReg, null); 

                JsonObject jsonLogin = new JsonObject();
                jsonLogin.addProperty("curp", curp);
                jsonLogin.addProperty("password", pass);
                
                String responseBody = sendPost(AUTH_URL + "/login", jsonLogin, null);
                
                JsonObject resJson = JsonParser.parseString(responseBody).getAsJsonObject();
                if(resJson.has("token")) {
                    String token = resJson.get("token").getAsString();
                    
                    curps.add(curp);
                    tokens.add(token);

                    JsonObject jsonDep = new JsonObject();
                    jsonDep.addProperty("curp", curp);
                    jsonDep.addProperty("monto", monto);
                    sendPost(ACCOUNT_URL + "/deposit", jsonDep, token);
                    
                    System.out.print("."); // Indicador de progreso
                } else {
                    System.err.println("\nError: No se recibió token para " + curp);
                }
                
            } catch (Exception e) {
                if (!e.getMessage().contains("400") && !e.getMessage().contains("409")) {
                     System.err.println("Error preparando cliente " + i + ": " + e.getMessage());
                }
            }
        }
        System.out.println("\nUsuarios listos y fondeados.");
    }

    private static void ejecutarCicloCliente(int miIndice, int totalClientes, int txPorMinuto) {
        long esperaPromedioMs = 60000 / txPorMinuto; 
        
        String miCurp = curps.get(miIndice);
        String miToken = tokens.get(miIndice);

        while (true) { 
            try {
                int indiceDestino = random.nextInt(totalClientes);
                while (indiceDestino == miIndice) {
                    indiceDestino = random.nextInt(totalClientes);
                }
                String curpDestino = curps.get(indiceDestino);

                double monto = 10 + (100 * random.nextDouble()); // Entre 10 y 110 pesos

                JsonObject jsonTx = new JsonObject();
                jsonTx.addProperty("curp", miCurp);      // Origen
                jsonTx.addProperty("destino", curpDestino); // Destino (Random)
                jsonTx.addProperty("monto", monto);
                
                sendPost(ACCOUNT_URL + "/transfer", jsonTx, miToken);
                
                System.out.printf("[%s] %s -> %s : $%.2f%n", 
                        Thread.currentThread().getName(), miCurp, curpDestino, monto);

                double variacion = 0.6 + (0.8 * random.nextDouble());
                long esperaReal = (long) (esperaPromedioMs * variacion);
                
                Thread.sleep(esperaReal);

            } catch (Exception e) {
                System.err.println("Error en transacción: " + e.getMessage());
                try { Thread.sleep(1000); } catch (Exception ex) {} // Pausa si hay error
            }
        }
    }

    // Método auxiliar para enviar POST con o sin Token
    private static String sendPost(String url, JsonObject json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .timeout(Duration.ofSeconds(10)); // Timeout generoso para AWS
        
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP Error " + response.statusCode() + " Body: " + response.body());
        }
        return response.body();
    }
}