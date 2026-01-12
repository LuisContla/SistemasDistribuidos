package com.ipn.distribuidos;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorLanterna {

    static List<ServicioInfo> servicios = Arrays.asList(
        new ServicioInfo("Auth Service", "18.217.241.241", 3000), 
        new ServicioInfo("Account Svc 1", "18.223.120.59", 3000),
        new ServicioInfo("Account Svc 2", "18.225.33.183", 3000),
        new ServicioInfo("Transaccion 1", "13.59.209.109", 3000),
        new ServicioInfo("Transaccion 2", "3.144.74.161", 3000),
        new ServicioInfo("Auditor",       "18.118.145.18", 3000),
        new ServicioInfo("FrontEnd",      "3.137.141.189", 5173)
    );

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public static void main(String[] args) throws IOException {
        // Configuración Lanterna
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = new TerminalScreen(terminalFactory.createTerminal());
        screen.startScreen();

        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        BasicWindow window = new BasicWindow("Monitor de Sistema Financiero (IPN)");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentPanel.addComponent(new Label("Monitor de CPU Real"));
        contentPanel.addComponent(new EmptySpace());

        // Tabla
        Panel tablePanel = new Panel();
        tablePanel.setLayoutManager(new GridLayout(4)); 

        tablePanel.addComponent(new Label("SERVICIO").setForegroundColor(TextColor.ANSI.CYAN));
        tablePanel.addComponent(new Label("IP").setForegroundColor(TextColor.ANSI.CYAN));
        tablePanel.addComponent(new Label("PUERTO").setForegroundColor(TextColor.ANSI.CYAN));
        tablePanel.addComponent(new Label("USO CPU").setForegroundColor(TextColor.ANSI.CYAN));

        Label[] statusLabels = new Label[servicios.size()];

        for (int i = 0; i < servicios.size(); i++) {
            ServicioInfo s = servicios.get(i);
            tablePanel.addComponent(new Label(s.nombre));
            tablePanel.addComponent(new Label(s.ip));
            tablePanel.addComponent(new Label(String.valueOf(s.puerto)));
            
            statusLabels[i] = new Label("Esperando datos...");
            tablePanel.addComponent(statusLabels[i]);
        }

        contentPanel.addComponent(tablePanel);
        contentPanel.addComponent(new Button("Salir", window::close));
        window.setComponent(contentPanel);

        // Loop de actualización
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < servicios.size(); i++) {
                    double cpu = obtenerCpuReal(servicios.get(i));
                    actualizarLabel(statusLabels[i], cpu, servicios.get(i).nombre);
                }
            }
        }, 0, 1000L);

        textGUI.addWindowAndWait(window);
        screen.stopScreen();
        System.exit(0);
    }

    private static void actualizarLabel(Label label, double cpu, String nombre) {
        if (cpu == -2.0) {
            // Caso especial Frontend
            label.setText("ONLINE (WEB)");
            label.setForegroundColor(TextColor.ANSI.GREEN);
        } else if (cpu < 0) {
            // Error de conexión o falta Actuator
            label.setText("SIN CONEXIÓN / 404");
            label.setForegroundColor(TextColor.ANSI.RED);
        } else {
            // Dato Real
            label.setText(String.format("%.2f %%", cpu));
            if (cpu > 80.0) label.setForegroundColor(TextColor.ANSI.RED);
            else if (cpu > 40.0) label.setForegroundColor(TextColor.ANSI.YELLOW);
            else label.setForegroundColor(TextColor.ANSI.GREEN);
        }
    }

    private static double obtenerCpuReal(ServicioInfo servicio) {
        if (servicio.nombre.equals("FrontEnd")) {
             return -2.0;
        }

        try {
            String url = "http://" + servicio.ip + ":" + servicio.puerto + "/actuator/metrics/system.cpu.usage";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(1500))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parseamos el JSON real de Spring Boot
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                double valor = json.getAsJsonArray("measurements")
                                   .get(0).getAsJsonObject()
                                   .get("value").getAsDouble();
                return valor * 100.0;
            } else {
                return -1.0;
            }
        } catch (Exception e) {
            return -1.0; 
        }
    }

    static class ServicioInfo {
        String nombre;
        String ip;
        int puerto;
        public ServicioInfo(String n, String i, int p) { 
            nombre = n; ip = i; puerto = p;
        }
    }
}