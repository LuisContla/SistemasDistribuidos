/*
 * PROYECTO 3 — Simulación de votaciones
 * Autor: Contla Mota Luis Andrés
 * Grupo: 7CV3
 */
// ----------------------------------------------------------------------------------
// Este archivo forma parte de la simulación de votaciones en tiempo real.
// Traté de mantener el código simple y bien comentado para facilitar su lectura.
// ----------------------------------------------------------------------------------

import java.util.*;

// Renderizador simple de barras en texto (usado en versiones previas; lo conservo por si sirve)
public class TextBarChart {
    public static List<String> render(Map<String, Long> data, int width) {
        List<String> lines = new ArrayList<>();
        if (data.isEmpty()) { lines.add("(sin datos)"); return lines; }
        long max = data.values().stream().mapToLong(Long::longValue).max().orElse(1);
        for (var e : data.entrySet()) {
            int bar = (int)Math.round((e.getValue() * 1.0 / max) * Math.max(1, width));
            String label = e.getKey();
            String barras = "#".repeat(Math.max(0, bar));
            lines.add(String.format("%-12s | %-" + width + "s %d", label, barras, e.getValue()));
        }
        return lines;
    }
}
