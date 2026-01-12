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

public class VotoReader implements Runnable {
    private final File file;
    private final StatsModel model;

    public VotoReader(String path, StatsModel model) {
        this.file = new File(path);
        this.model = model;
    }

    @Override
    public void run() { // lector tipo 'tail -f' con RandomAccessFile
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long pos = 0L;
            while (true) {
                long len = raf.length();
                if (len < pos) { // archivo truncado
                    pos = 0;
                }
                if (len > pos) {
                    raf.seek(pos);
                    String line;
                    while ((line = raf.readLine()) != null) {
                        String decoded = new String(line.getBytes("ISO-8859-1"), StandardCharsets.UTF_8);
                        procesar(decoded);
                    }
                    pos = raf.getFilePointer();
                }
                Thread.sleep(200); // polling ligero
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parsea una línea 'CURP,PARTIDO' y actualiza el modelo de conteos
    private void procesar(String linea) {
        String[] parts = linea.split(",");
        if (parts.length != 2) return;
        String curp = parts[0].trim();
        String partido = parts[1].trim();
        model.registrarVoto(curp, partido);
    }
}
