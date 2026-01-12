/*
 * PROYECTO 3 — Simulación de votaciones
 * Autor: Contla Mota Luis Andrés 
 * Grupo: 7CV3 
 */
// ----------------------------------------------------------------------------------
// Este archivo forma parte de la simulación de votaciones en tiempo real.
// Traté de mantener el código simple y bien comentado para facilitar su lectura.
// ----------------------------------------------------------------------------------

// Catálogo mínimo de partidos (para la demo)
public enum Partido {
    MC, MORENA, PAN, PRD, PRI, PT, PVEM;

    // Para la simulación: elige un partido al azar
    public static Partido random() {
        Partido[] vals = values();
        return vals[(int)(Math.random() * vals.length)];
    }
}
