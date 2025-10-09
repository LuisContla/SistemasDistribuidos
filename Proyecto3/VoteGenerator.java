
/*
 * Proyecto: Sistemas Distribuidos - Simulador de Votaciones (Programa 1)
 * Archivo : VoteGenerator.java
 * Autor   : [TU NOMBRE] - [TU GRUPO]
 * Descripción:
 *   Genera votos sintéticos en tiempo real y los agrega (append) al archivo VOTOS.dat
 *   a razón de N registros por segundo.
 *   Formato de cada línea: CURP,PARTIDO
 *
 * Requisitos:
 *   - Java 11+ (probado en 11 y 17)
 *   - Curp.java en el mismo directorio (usamos Curp.getCURP())
 *
 * Uso:
 *   Compilar: javac Curp.java VoteGenerator.java
 *   Ejecutar: java VoteGenerator -n 50            # 50 votos/seg
 *             java VoteGenerator -n 100 -o datos/VOTOS.dat
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoteGenerator {
    private static final List<String> PARTIDOS = Arrays.asList(
        "MC","MORENA","PAN","PRD","PRI","PT","PVEM"
    );

    public static void main(String[] args) {
        int rate = 50; // valor por defecto
        File outFile = new File("VOTOS.dat");
        // Permite configurar vía CLI
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-n":
                case "--rate":
                    if (i+1 >= args.length) {
                        System.err.println("Falta el valor para -n/--rate");
                        System.exit(1);
                    }
                    rate = Integer.parseInt(args[++i]);
                    break;
                case "-o":
                case "--output":
                    if (i+1 >= args.length) {
                        System.err.println("Falta la ruta para -o/--output");
                        System.exit(1);
                    }
                    outFile = new File(args[++i]);
                    break;
                default:
                    // ignorar argumentos desconocidos para mantenerlo simple
                    break;
            }
        }

        if (rate <= 0) {
            System.err.println("El parámetro -n debe ser > 0");
            System.exit(1);
        }

        // Asegurar carpeta de salida
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();

        System.out.println("Generando votos en: " + outFile.getAbsolutePath());
        System.out.println("Tasa: " + rate + " votos/seg. (Ctrl+C para terminar)");

        final Random rnd = new Random();

        // Precálculo de periodo por registro
        final long nanosPorVoto = Duration.ofSeconds(1).toNanos() / rate;
        long siguiente = System.nanoTime();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8, /*append*/ true)
)) {
            while (true) {
                // Generar una tanda de 'rate' cada segundo distribuyendo uniformemente en el tiempo
                // Escribimos 1 voto y dormimos el tiempo necesario para quedar a 'nanosPorVoto'
                String curp = Curp.getCURP(); // usa el generador que enviaste
                String partido = PARTIDOS.get(rnd.nextInt(PARTIDOS.size()));
                bw.write(curp + "," + partido);
                bw.newLine();
                bw.flush(); // importante para que el lector vea los datos en tiempo real

                // Esperar hasta el momento planificado para el siguiente voto
                siguiente += nanosPorVoto;
                long espera = siguiente - System.nanoTime();
                if (espera > 0) {
                    long millis = espera / 1_000_000L;
                    int nanos   = (int)(espera % 1_000_000L);
                    try {
                        Thread.sleep(millis, nanos);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    // Si estamos retrasados, re-sincronizamos el reloj para evitar deriva indefinida
                    siguiente = System.nanoTime();
                }
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo en " + outFile + ": " + e.getMessage());
        }
    }
}
