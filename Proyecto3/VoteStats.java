
/*
 * Proyecto: Sistemas Distribuidos - Simulador de Votaciones (Programa 2)
 * Archivo : VoteStats.java
 * Autor   : [TU NOMBRE] - [TU GRUPO]
 * Descripción:
 *   Lee en tiempo real el archivo VOTOS.dat y muestra una tabla de porcentajes
 *   y barras horizontales en colores por partido que se actualizan en vivo.
 *   Acepta consultas por consola:
 *     - "sexo"    : totales por sexo
 *     - "estado"  : totales por estado
 *     - "edad X"  : votos de edad X, desglosados por sexo
 *
 * Requisitos:
 *   - Java 11+
 *   - lanterna-3.x.jar (para color en terminal con buen soporte multiplataforma)
 *     Descarga: https://github.com/mabe02/lanterna
 *
 * Uso:
 *   Compilar (Linux/macOS): javac -cp lanterna.jar Curp.java CurpParser.java VoteStats.java
 *   Compilar (Windows)    : javac -cp lanterna.jar; Curp.java CurpParser.java VoteStats.java
 *   Ejecutar (Linux/macOS): java -cp .:lanterna.jar VoteStats VOTOS.dat
 *   Ejecutar (Windows)    : java -cp .;lanterna.jar VoteStats VOTOS.dat
 */
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.SGR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoteStats {

    private static final List<String> PARTIDOS = Arrays.asList(
        "MC","MORENA","PAN","PRD","PRI","PT","PVEM"
    );

    // Colores aproximados por partido
    private static final Map<String, TextColor> COLOR = Map.of(
        "MC",     new TextColor.RGB(255,128,0),
        "MORENA", new TextColor.RGB(140,25,25),
        "PAN",    new TextColor.RGB(0,102,204),
        "PRD",    new TextColor.RGB(240,208,0),
        "PRI",    new TextColor.RGB(0,153,76),
        "PT",     new TextColor.RGB(204,0,0),
        "PVEM",   new TextColor.RGB(102,204,0)
    );

    private final File archivo;
    private final Map<String, Long> conteoPartido = new ConcurrentHashMap<>();
    private final Map<Character, Long> conteoSexo = new ConcurrentHashMap<>();
    private final Map<String, Long> conteoEstado = new ConcurrentHashMap<>();
    private final Map<Integer, long[]> conteoEdadSexo = new ConcurrentHashMap<>(); // edad -> [H, M]
    private volatile long total = 0;

    public VoteStats(File archivo) {
        this.archivo = archivo;
        for (String p: PARTIDOS) conteoPartido.put(p, 0L);
        conteoSexo.put('H', 0L); conteoSexo.put('M', 0L);
    }

    public void start() throws Exception {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        terminal.setCursorVisible(false);

        Thread lector = new Thread(this::tail);
        lector.setDaemon(true);
        lector.start();

        Thread entrada = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    if (line.equals("sexo")) {
                        printSexo(terminal);
                    } else if (line.equals("estado")) {
                        printEstado(terminal);
                    } else if (line.startsWith("edad ")) {
                        try {
                            int x = Integer.parseInt(line.substring(5).trim());
                            printEdad(terminal, x);
                        } catch (NumberFormatException nfe) {
                            // ignorar
                        }
                    }
                }
            } catch (Exception e) { /* nada */ }
        });
        entrada.setDaemon(true);
        entrada.start();

        // Bucle de dibujo
        while (true) {
            draw(terminal);
            Thread.sleep(100); // ~10 FPS
        }
    }

    private void tail() {
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            long pos = raf.length(); // empezar al final si ya existe
            while (true) {
                long len = raf.length();
                if (len < pos) { // archivo truncado
                    pos = len;
                } else if (len > pos) {
                    raf.seek(pos);
                    String line;
                    while ((line = raf.readLine()) != null) {
                        processLine(new String(line.getBytes("ISO-8859-1"), StandardCharsets.UTF_8));
                    }
                    pos = raf.getFilePointer();
                }
                Thread.sleep(50);
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private void processLine(String line) {
        String[] parts = line.split(",", 2);
        if (parts.length != 2) return;
        String curp = parts[0].trim();
        String partido = parts[1].trim().toUpperCase();

        if (!conteoPartido.containsKey(partido)) return;

        conteoPartido.merge(partido, 1L, Long::sum);
        total++;

        char sx = Character.toUpperCase(CurpParser.sexo(curp));
        if (sx == 'H' || sx == 'M') conteoSexo.merge(sx, 1L, Long::sum);

        String est = CurpParser.estado(curp);
        conteoEstado.merge(est, 1L, Long::sum);

        int edad = CurpParser.edad(curp, LocalDate.now());
        if (edad >= 0 && edad <= 120) {
            conteoEdadSexo.computeIfAbsent(edad, k -> new long[2]);
            if (sx == 'H') conteoEdadSexo.get(edad)[0]++;
            if (sx == 'M') conteoEdadSexo.get(edad)[1]++;
        }
    }

    private void draw(Terminal t) throws Exception {
        TerminalSize size = t.getTerminalSize();
        int width = size.getColumns();
        int height = size.getRows();
        t.clearScreen();

        // Título
        put(t, 0, 0, "Simulador de Votaciones - Estadísticas en Vivo (archivo: " + archivo.getName() + ")");
        put(t, 0, 1, "Total de votos: " + total + " | Escribe: sexo | estado | edad X  (Enter)");

        // Barras por partido
        int y = 3;
        long max = Math.max(1, total);
        int barMax = Math.max(10, width - 25); // espacio para etiqueta + %
        for (String p: PARTIDOS) {
            long c = conteoPartido.getOrDefault(p, 0L);
            int pct = (int)Math.round((c*100.0)/Math.max(1,total));
            int barLen = (int)Math.round((c*1.0*barMax)/max);

            // Etiqueta
            put(t, 2, y, String.format("%-7s %6d (%3d%%)", p, c, pct));

            // Barra
            TextColor color = COLOR.getOrDefault(p, TextColor.ANSI.WHITE);
            t.setForegroundColor(color);
            for (int i = 0; i < barLen; i++) {
                put(t, 22+i, y, "█");
            }
            t.resetColorAndSGR();
            y++;
        }
        t.flush();
    }

    private void printSexo(Terminal t) throws Exception {
        t.bell();
        put(t, 0, 12, String.format("Consulta SEXO: H=%d  M=%d", 
            conteoSexo.getOrDefault('H', 0L),
            conteoSexo.getOrDefault('M', 0L)));
        t.flush();
    }

    private void printEstado(Terminal t) throws Exception {
        t.bell();
        put(t, 0, 13, "Consulta ESTADO (top 10):");
        // ordenar por valor desc
        var list = new java.util.ArrayList<Map.Entry<String,Long>>(conteoEstado.entrySet());
        list.sort((a,b) -> Long.compare(b.getValue(), a.getValue()));
        int y = 14;
        for (int i=0; i<Math.min(10, list.size()); i++) {
            var e = list.get(i);
            put(t, 2, y++, String.format("%-20s %6d", e.getKey(), e.getValue()));
        }
        t.flush();
    }

    private void printEdad(Terminal t, int x) throws Exception {
        long[] hm = conteoEdadSexo.getOrDefault(x, new long[]{0,0});
        t.bell();
        put(t, 0, 25, String.format("Consulta EDAD %d: H=%d  M=%d", x, hm[0], hm[1]));
        t.flush();
    }

    private void put(Terminal t, int x, int y, String s) throws Exception {
        t.setCursorPosition(x, y);
        for (char ch: s.toCharArray()) {
            t.putCharacter(ch);
        }
    }

    public static void main(String[] args) throws Exception {
        File archivo = new File(args.length > 0 ? args[0] : "VOTOS.dat");
        if (!archivo.exists()) {
            System.out.println("Aviso: " + archivo.getAbsolutePath() + " no existe todavía. Esperando datos...");
            // Crear archivo vacío para que el tail funcione bien
            archivo.createNewFile();
        }
        new VoteStats(archivo).start();
    }
}
