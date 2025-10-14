/*
 * PROYECTO 3 — Simulación de votaciones
 * Autor: Contla Mota Luis Andrés
 * Grupo: 7CV3
 */
// ----------------------------------------------------------------------------------
// Este archivo forma parte de la simulación de votaciones en tiempo real.
// Traté de mantener el código simple y bien comentado para facilitar su lectura.
// ----------------------------------------------------------------------------------

import java.io.*;
import java.nio.charset.StandardCharsets;

public class VotoGenerator {
    // Generador de eventos: escribe N votos por segundo en VOTOS.dat en formato CSV simple
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Uso: java VotoGenerator <registros_por_segundo>");
            System.exit(1);
        }
        int n = Integer.parseInt(args[0]);
        if (n <= 0) n = 1;

        File out = new File("VOTOS.dat");
        try (FileOutputStream fos = new FileOutputStream(out, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {

            while (true) { // loop infinito, se puede detener con Ctrl+C en la consola
                long start = System.currentTimeMillis();
                for (int i = 0; i < n; i++) {
                    String curp = CURPUtils.generarCURP();
                    String partido = Partido.random().name();
                    bw.write(curp + "," + partido);
                    bw.newLine();
                }
                bw.flush();
                long elapsed = System.currentTimeMillis() - start;
                long sleep = Math.max(0, 1000 - elapsed);
                Thread.sleep(sleep);
            }
        }
    }
}
