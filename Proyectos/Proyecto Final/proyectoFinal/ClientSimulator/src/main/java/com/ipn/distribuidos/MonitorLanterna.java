package com.ipn.distribuidos;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class MonitorLanterna {

    // Lista de servicios a monitorear (Nombre e IP/URL) 
    static List<ServicioInfo> servicios = Arrays.asList(
        new ServicioInfo("Auth Service", "10.0.0.1"),
        new ServicioInfo("Account Service 1", "10.0.0.2"),
        new ServicioInfo("Account Service 2", "10.0.0.3"),
        new ServicioInfo("Transaction Svc 1", "10.0.0.4"),
        new ServicioInfo("Transaction Svc 2", "10.0.0.5")
    );

    public static void main(String[] args) throws IOException {
        int segundos = 2; // Default
        if (args.length > 0) {
            segundos = Integer.parseInt(args[0]);
        }

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = new TerminalScreen(terminalFactory.createTerminal());
        screen.startScreen();

        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        BasicWindow window = new BasicWindow("Sistema Financiero - Monitor de Recursos");

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        
        contentPanel.addComponent(new Label("Monitoreando cada " + segundos + " segundos. [Presiona Ctrl+C para salir]"));
        contentPanel.addComponent(new EmptySpace());

        Panel tablePanel = new Panel();
        tablePanel.setLayoutManager(new GridLayout(3));

        tablePanel.addComponent(new Label("SERVICIO").setForegroundColor(TextColor.ANSI.CYAN));
        tablePanel.addComponent(new Label("IP INSTANCIA").setForegroundColor(TextColor.ANSI.CYAN));
        tablePanel.addComponent(new Label("USO CPU").setForegroundColor(TextColor.ANSI.CYAN));

        Label[] cpuLabels = new Label[servicios.size()];

        for (int i = 0; i < servicios.size(); i++) {
            ServicioInfo s = servicios.get(i);
            
            // Columna 1: Nombre
            tablePanel.addComponent(new Label(s.nombre));
            
            // Columna 2: IP 
            tablePanel.addComponent(new Label(s.ip));
            
            // Columna 3: CPU
            cpuLabels[i] = new Label("Calculando...");
            tablePanel.addComponent(cpuLabels[i]);
        }

        contentPanel.addComponent(tablePanel);
        contentPanel.addComponent(new EmptySpace());
        contentPanel.addComponent(new Button("Cerrar Monitor", window::close));

        window.setComponent(contentPanel);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < servicios.size(); i++) {
                    double cpu = obtenerCpuRemoto(servicios.get(i).ip);
                    actualizarLabel(cpuLabels[i], cpu);
                }
            }
        }, 0, segundos * 1000L);

        textGUI.addWindowAndWait(window);
        screen.stopScreen();
    }

    private static void actualizarLabel(Label label, double cpu) {
        String texto = String.format("%.1f %%", cpu);
        label.setText(texto);

        if (cpu > 80.0) {
            label.setForegroundColor(TextColor.ANSI.RED);
        } else if (cpu > 50.0) {
            label.setForegroundColor(TextColor.ANSI.YELLOW);
        } else {
            label.setForegroundColor(TextColor.ANSI.GREEN);
        }
    }

    //  aque va a ir la llamada HTTP real al endpoint /actuator/metrics/system.cpu.usage 
    private static double obtenerCpuRemoto(String ip) {
        return new Random().nextDouble() * 100;
    }

    static class ServicioInfo {
        String nombre;
        String ip;
        public ServicioInfo(String n, String i) { nombre = n; ip = i; }
    }
}