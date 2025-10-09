import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

class Curp {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        int n = Integer.parseInt(args[0]); // Cantidad de CURPs por lista
        int m = Integer.parseInt(args[1]); // Número de listas

        ArrayList<ArrayList<String>> listasDeCurps = new ArrayList<>();

        // Medir el tiempo total de ejecución
        long startTimeTotal = System.nanoTime(); // Medir tiempo total al principio

        for (int i = 0; i < m; i++) {
            ArrayList<String> curpsLista = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                String nuevaCurp = getCURP();
                insertarOrdenado(curpsLista, nuevaCurp);
            }
            listasDeCurps.add(curpsLista);
        }

        // Obtener el número de núcleos del CPU
        int numCores = Runtime.getRuntime().availableProcessors();

        // Cambiar el tamaño del ThreadPool para cada ejecución, desde 1 hasta numCores
        // + 1
        for (int poolSize = 1; poolSize <= numCores + 1; poolSize++) {
            ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

            long startTime = System.nanoTime();

            ArrayList<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < listasDeCurps.size(); i++) {
                final int index = i;
                futures.add(threadPool.submit(() -> {
                    // Ordenar la lista
                    ArrayList<String> listaOrdenada = listasDeCurps.get(index);
                    ordenarCurps(listaOrdenada);
                    return null;
                }));
            }

            for (Future<Void> future : futures) {
                future.get();
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;

            threadPool.shutdown();

            System.out.println("Tiempo de ejecución con " + poolSize + " hilos: " + duration + " ms");
        }

        long endTimeTotal = System.nanoTime();
        long totalDuration = (endTimeTotal - startTimeTotal) / 1000000000;
        System.out.println("\nTiempo total de ejecución: " + totalDuration + " s");

    }

    static void insertarOrdenado(ArrayList<String> lista, String nuevaCurp) {
        Iterator<String> iterador = lista.iterator();
        int indice = 0;
        while (iterador.hasNext()) {
            String actual = iterador.next();
            if (nuevaCurp.substring(0, 4).compareTo(actual.substring(0, 4)) < 0) {
                break;
            }
            indice++;
        }
        lista.add(indice, nuevaCurp);
    }

    static void ordenarCurps(ArrayList<String> lista) {
        lista.sort((curp1, curp2) -> curp1.substring(0, 4).compareTo(curp2.substring(0, 4)));
    }

    static String getCURP() {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String Entidad[] = { "AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC",
                "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS" };
        int indice;

        StringBuilder sb = new StringBuilder(18);

        for (int i = 0; i < 4; i++) {
            indice = (int) (Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }

        for (int i = 0; i < 6; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }

        indice = (int) (Sexo.length() * Math.random());
        sb.append(Sexo.charAt(indice));

        sb.append(Entidad[(int) (Math.random() * 32)]);

        for (int i = 0; i < 3; i++) {
            indice = (int) (Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }

        for (int i = 0; i < 2; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }

        return sb.toString();
    }
}