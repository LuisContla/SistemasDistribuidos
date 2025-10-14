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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class StatsModel {
    private final Map<String, AtomicLong> votosPorPartido = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> votosPorSexo = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> votosPorEstado = new ConcurrentHashMap<>();
    private final Map<Integer, Map<String, AtomicLong>> votosPorEdadYSexo = new ConcurrentHashMap<>();
    private final AtomicLong total = new AtomicLong();

    // Punto central: a cada voto incrementa todas las métricas necesarias
    public void registrarVoto(String curp, String partido) {
        total.incrementAndGet();
        votosPorPartido.computeIfAbsent(partido, k -> new AtomicLong()).incrementAndGet();

        String sexo = CURPUtils.sexoDe(curp);
        votosPorSexo.computeIfAbsent(sexo, k -> new AtomicLong()).incrementAndGet();

        String estado = CURPUtils.estadoNombre(curp);
        votosPorEstado.computeIfAbsent(estado, k -> new AtomicLong()).incrementAndGet();

        int edad = CURPUtils.edadDe(curp);
        if (edad >= 18 && edad <= 100) { // solo mayores de edad en el reporte por edades
            Map<String, AtomicLong> porSexo = votosPorEdadYSexo.computeIfAbsent(edad, k -> new ConcurrentHashMap<>());
            porSexo.computeIfAbsent(sexo, k -> new AtomicLong()).incrementAndGet();
        }
    }

    public long getTotal() { return total.get(); }

    public Map<String, Long> snapshotPartidos() {
        return snapshot(votosPorPartido);
    }
    public Map<String, Long> snapshotSexo() {
        return snapshot(votosPorSexo);
    }
    public Map<String, Long> snapshotEstados() {
        return snapshot(votosPorEstado);
    }
    public Map<Integer, Map<String, Long>> snapshotEdadYSexo() {
        Map<Integer, Map<String, Long>> out = new TreeMap<>();
        for (var e : votosPorEdadYSexo.entrySet()) {
            Map<String, Long> inner = new TreeMap<>();
            for (var s : e.getValue().entrySet()) inner.put(s.getKey(), s.getValue().get());
            out.put(e.getKey(), inner);
        }
        return out;
    }

    // Toma una 'foto' del mapa concurrente para pintar en la UI sin bloquear
    private static Map<String, Long> snapshot(Map<String, AtomicLong> src) {
        Map<String, Long> out = new TreeMap<>((a,b)->a.compareToIgnoreCase(b));
        for (var e : src.entrySet()) out.put(e.getKey(), e.getValue().get());
        return out;
    }
}
