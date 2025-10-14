/*
 * PROYECTO 3 — Simulación de votaciones
 * Autor: Contla Mota Luis Andrés 
 * Grupo: 7CV3 
 */
// ----------------------------------------------------------------------------------
// Este archivo forma parte de la simulación de votaciones en tiempo real.
// Traté de mantener el código simple y bien comentado para facilitar su lectura.
// ----------------------------------------------------------------------------------

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.TerminalSize;

import java.io.File;
import java.util.*;
import java.awt.Font;
import java.util.Map.Entry;

public class StatsApp {
    private static final String FILE = "VOTOS.dat";

    private static final Map<String, TextColor> PARTY_COLOR = new HashMap<String, TextColor>();
    static {
        PARTY_COLOR.put("MORENA", TextColor.ANSI.RED_BRIGHT);
        PARTY_COLOR.put("PAN",    TextColor.ANSI.BLUE_BRIGHT);
        PARTY_COLOR.put("PRI",    TextColor.ANSI.GREEN_BRIGHT);
        PARTY_COLOR.put("PRD",    TextColor.ANSI.YELLOW);
        PARTY_COLOR.put("PT",     TextColor.ANSI.RED);
        PARTY_COLOR.put("PVEM",   TextColor.ANSI.GREEN);
        PARTY_COLOR.put("MC",     TextColor.ANSI.CYAN_BRIGHT);
    }

    public static void main(String[] args) throws Exception {
        StatsModel model = new StatsModel();

        // Hilo lector: observa el archivo VOTOS.dat y va alimentando el modelo
        Thread reader = new Thread(new VotoReader(FILE, model), "voto-reader");
        reader.setDaemon(true);
        reader.start();

        Font font = new Font("Consolas", Font.PLAIN, 18);
        SwingTerminalFontConfiguration fontCfg = SwingTerminalFontConfiguration.newInstance(font);

        DefaultTerminalFactory factory = new DefaultTerminalFactory()
                .setPreferTerminalEmulator(true)
                .setTerminalEmulatorTitle("SIMULACIÓN DE VOTACIONES")
                .setInitialTerminalSize(new TerminalSize(140, 42))
                .setTerminalEmulatorFontConfiguration(fontCfg);

        Screen screen = new TerminalScreen(factory.createTerminal());
        screen.startScreen();
        screen.setCursorPosition(null);

        int option = 0; // vista actual (0=Resumen, 1=Sexo, 2=Estados, 3=Edad)

        try {
            while (true) {
                // Bucle principal de la UI: refresca pantalla, dibuja vista y lee teclado
                screen.clear();
                TextGraphics g = screen.newTextGraphics();

                int cols = screen.getTerminalSize().getColumns();
                int rows = screen.getTerminalSize().getRows();

                g.enableModifiers(SGR.BOLD);
                g.putString(2, 1, "SIMULACIÓN DE VOTACIONES — Tiempo Real");
                g.disableModifiers(SGR.BOLD);
                g.putString(2, 2, "Archivo: " + new File(FILE).getAbsolutePath());
                g.putString(2, 3, "Total de votos: " + model.getTotal());

                g.putString(2, 5, "Presione el número en su teclado para visualizar la información");
                g.putString(2, 6, "[0] Resumen | [1] Votos por sexo | [2] Votos por estado | [3] Votos por edad | [Q] Salir");

                int chartTop = 8;
                int chartWidth = Math.max(20, cols - 6 - 2);

                switch (option) {
                    case 0: drawResumen(g, model, chartTop, chartWidth, rows, cols); break;
                    case 1: drawPorSexo(g, model, chartTop, chartWidth); break;
                    case 2: drawPorEstado(g, model, chartTop, chartWidth); break;
                    case 3: drawPorEdadYSexoBucket(g, model, chartTop, chartWidth, rows); break;
                    default: break;
                }

                screen.refresh();

                KeyStroke key = screen.pollInput();
                if (key != null) {
                    if (key.getKeyType() == KeyType.Character) {
                        char ch = Character.toLowerCase(key.getCharacter());
                        if (ch == 'q') break;
                        if (ch == '0') option = 0;
                        if (ch == '1') option = 1;
                        if (ch == '2') option = 2;
                        if (ch == '3') option = 3;
                    } else if (key.getKeyType() == KeyType.EOF) {
                        break;
                    }
                }

                Thread.sleep(120);
            }
        } finally {
            screen.stopScreen();
        }
    }

    // ===== Resumen: barras de porcentaje en una sola columna =====
    // Dibuja barras por partido en una sola columna (solo porcentaje del total)
    private static void drawResumen(TextGraphics g, StatsModel model, int top, int width, int rows, int cols) {
        g.putString(2, top, "Votos por partido (porcentaje del total):");
        Map<String, Long> data = model.snapshotPartidos();
        long total = Math.max(1, model.getTotal());

        List<String> partidos = new ArrayList<String>();
        for (Partido p : Partido.values()) partidos.add(p.name());

        int line = top + 2;
        int labelWidth = 6;
        int pctWidth = 6;
        int barWidth = Math.max(20, Math.min(80, width - (labelWidth + 1 + pctWidth + 3)));

        long maxVal = 1;
        for (String p : partidos) {
            Long v = data.get(p);
            if (v != null && v > maxVal) maxVal = v;
        }

        for (String p : partidos) {
            long val = data.get(p) == null ? 0L : data.get(p);
            double pct = (val * 100.0) / total;
            int bar = (int)Math.round((val * 1.0 / Math.max(1, maxVal)) * barWidth);

            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(2, line, String.format("%-"+labelWidth+"s ", p));

            TextColor color = PARTY_COLOR.get(p) == null ? TextColor.ANSI.WHITE : PARTY_COLOR.get(p);
            g.setForegroundColor(color);
            if (bar > 0) g.putString(2 + labelWidth + 1, line, repeat('█', bar));

            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            String pctStr = String.format("%4.1f%%", pct);
            g.putString(2 + labelWidth + 1 + Math.max(1, bar) + 1, line, pctStr);
            line++;
        }
        g.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    // ===== Votos por sexo (colores) =====
    // Dibuja dos barras comparativas (H y M) con colores y totales
    private static void drawPorSexo(TextGraphics g, StatsModel model, int top, int width) {
        g.putString(2, top, "Votos por sexo");
        int line = top + 2;
        Map<String, Long> data = model.snapshotSexo();

        long hombres = data.get("Hombre") == null ? 0L : data.get("Hombre");
        long mujeres = data.get("Mujer") == null ? 0L : data.get("Mujer");
        long max = Math.max(1, Math.max(hombres, mujeres));
        int barW = Math.min(60, Math.max(20, width));

        // Hombre (azul)
        g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        g.putString(2, line, String.format("%-7s | ", "Hombre"));
        g.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        int hb = (int)Math.round((hombres * 1.0 / max) * barW);
        if (hb > 0) g.putString(12, line, repeat('█', hb));
        g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        g.putString(12 + Math.max(1, hb) + 1, line, String.valueOf(hombres));
        line++;

        // Mujer (rosa/magenta)
        g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        g.putString(2, line, String.format("%-7s | ", "Mujer"));
        g.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);
        int mb = (int)Math.round((mujeres * 1.0 / max) * barW);
        if (mb > 0) g.putString(12, line, repeat('█', mb));
        g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        g.putString(12 + Math.max(1, mb) + 1, line, String.valueOf(mujeres));

        g.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    // ===== Votos por estado (todas las entidades) =====
    // Muestra TODOS los estados con barras, conteo y porcentaje distribuidos en columnas
    private static void drawPorEstado(TextGraphics g, StatsModel model, int top, int width) {
        g.putString(2, top, "Votos por estado");
        int startLine = top + 2;
        Map<String, Long> raw = model.snapshotEstados();

        // Lista oficial (32 estados)
        String[] ESTADOS_NOMBRE = {
            "Aguascalientes","Baja California","Baja California Sur","Campeche","Coahuila",
            "Colima","Chiapas","Chihuahua","Ciudad de México","Durango","Guanajuato",
            "Guerrero","Hidalgo","Jalisco","México","Michoacán","Morelos","Nayarit",
            "Nuevo León","Oaxaca","Puebla","Querétaro","Quintana Roo","San Luis Potosí",
            "Sinaloa","Sonora","Tabasco","Tlaxcala","Tamaulipas","Veracruz","Yucatán",
            "Zacatecas"
        };

        LinkedHashMap<String, Long> data = new LinkedHashMap<String, Long>();
        for (String e : ESTADOS_NOMBRE) {
            data.put(e, raw.get(e) == null ? 0L : raw.get(e));
        }

        drawEstadosBarrasMultiCol(g, data, startLine, model.getTotal());
    }

    // Helper para render multi-columna de estados, calculando anchos/filas disponibles
    private static void drawEstadosBarrasMultiCol(TextGraphics g, LinkedHashMap<String, Long> data, int startLine, long totalVotes) {
        int cols = g.getSize().getColumns();
        int rows = g.getSize().getRows();
        int top = startLine;
        int bottom = rows - 2;
        int maxRows = Math.max(1, bottom - top);

        int n = data.size(); // 32
        int colsNeeded = (n > maxRows ? 3 : 2); // 2 o 3 columnas
        int rowsPerCol = (int)Math.ceil(n * 1.0 / colsNeeded);
        int colWidth = (cols - 6) / colsNeeded;

        int labelWidth = 18;
        int countWidth = 5;
        int pctWidth = 6;
        int barWidth = Math.max(10, colWidth - (labelWidth + 1 + countWidth + 1 + pctWidth + 3));

        long max = 1;
        for (Long v : data.values()) if (v != null && v > max) max = v;

        int i = 0;
        for (Entry<String, Long> e : data.entrySet()) {
            int col = i / rowsPerCol;
            int row = i % rowsPerCol;
            int y = top + row;
            int x = 2 + col * colWidth;
            i++;

            long val = e.getValue() == null ? 0L : e.getValue();
            int bar = (int)Math.round((val * 1.0 / Math.max(1, max)) * barWidth);
            double pct = (val * 100.0) / Math.max(1, totalVotes);

            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(x, y, String.format("%-" + labelWidth + "s %"+countWidth+"d ", e.getKey(), val));
            g.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            if (bar > 0) g.putString(x + labelWidth + 1 + countWidth + 1, y, repeat('█', bar));
            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(x + labelWidth + 1 + countWidth + 1 + Math.max(1, bar) + 1, y, String.format("%4.1f%%", pct));
        }
        g.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    // ===== Votos por edad (buckets, siempre todos) =====
    // Agrupa edades en rangos y dibuja barras H/M, mostrando también % por rango
    private static void drawPorEdadYSexoBucket(TextGraphics g, StatsModel model, int top, int width, int rows) {
        g.putString(2, top, "Votos por edad");
        int line = top + 2;
        Map<Integer, Map<String, Long>> mapa = model.snapshotEdadYSexo();

        String[] etiquetas = {"18-25", "26-35", "36-45", "46-60", "61-75", "76-90"};
        int[][] rangos = {{18,25},{26,35},{36,45},{46,60},{61,75},{76,90}};

        LinkedHashMap<String, Long> hombres = new LinkedHashMap<String, Long>();
        LinkedHashMap<String, Long> mujeres = new LinkedHashMap<String, Long>();
        for (String e : etiquetas) { hombres.put(e, 0L); mujeres.put(e, 0L); }

        for (Entry<Integer, Map<String, Long>> e : mapa.entrySet()) {
            int edad = e.getKey().intValue();
            String bucket = null;
            for (int i=0;i<rangos.length;i++) {
                if (edad >= rangos[i][0] && edad <= rangos[i][1]) { bucket = etiquetas[i]; break; }
            }
            if (bucket == null) continue;
            Map<String, Long> sexMap = e.getValue();
            long h = sexMap.get("Hombre") == null ? 0L : sexMap.get("Hombre");
            long m = sexMap.get("Mujer") == null ? 0L : sexMap.get("Mujer");
            hombres.put(bucket, hombres.get(bucket) + h);
            mujeres.put(bucket, mujeres.get(bucket) + m);
        }

        int barW = Math.min(28, Math.max(12, width/3));
        long max = 1;
        for (Long v : hombres.values()) if (v != null && v > max) max = v;
        for (Long v : mujeres.values()) if (v != null && v > max) max = v;

        long total = Math.max(1, model.getTotal());

        for (String bucket : etiquetas) {
            long hv = hombres.get(bucket) == null ? 0L : hombres.get(bucket);
            long mv = mujeres.get(bucket) == null ? 0L : mujeres.get(bucket);
            int hb = (int)Math.round((hv * 1.0 / Math.max(1, max)) * barW);
            int mb = (int)Math.round((mv * 1.0 / Math.max(1, max)) * barW);

            double pctBucket = ((hv + mv) * 100.0) / total;

            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(2, line, String.format("%-6s | ", bucket));
            g.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);  // H
            if (hb>0) g.putString(11, line, repeat('█', hb));
            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(11+Math.max(1,hb)+1, line, String.valueOf(hv));
            g.putString(11+barW+8, line, String.format("%4.1f%%", pctBucket));
            line++;

            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(2, line, "       | ");
            g.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT); // M
            if (mb>0) g.putString(11, line, repeat('█', mb));
            g.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
            g.putString(11+Math.max(1,mb)+1, line, String.valueOf(mv));
            line += 2;
        }
        g.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    // ===== Util =====
    // Utilidad pequeñita para repetir caracteres (evito depender de Java 11+)
    private static String repeat(char ch, int times) {
        if (times <= 0) return "";
        StringBuilder sb = new StringBuilder(times);
        for (int i=0;i<times;i++) sb.append(ch);
        return sb.toString();
    }
}
