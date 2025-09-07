import java.util.Random;
import java.util.Scanner;

public class Clase5 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el numero de palabras a generar (n): ");
        int n = scanner.nextInt();
        scanner.nextLine(); // limpiar buffer

        long inicio = System.currentTimeMillis(); // tiempo inicial

        StringBuilder cadenota = new StringBuilder(n * 4);
        Random random = new Random();

        // Cadena de Palabras Aleatorias
        for (int i = 0; i < n; i++) {
            cadenota.append(generateRandomWord(3));
            if (i != n - 1) {
                cadenota.append(' ');
            }
        }

        // Buscar IPN en la cadenota
        String cadenaBuscar = cadenota.toString();
        String objetivo = "IPN";

        int count = 0;
        int fromIndex = 0;

        System.out.println("\n--- Ocurrencias ---");

        while (true) {
            int idx = cadenaBuscar.indexOf(objetivo, fromIndex);
            if (idx == -1) break;
            count++;
            System.out.println("Posicion: " + idx);
            fromIndex = idx + 1;
        }

        long fin = System.currentTimeMillis(); // tiempo final
        double segundos = (fin - inicio) / 1000.0;

        // Resultados
        System.out.println("\n--- Resultados ---");
        System.out.println("Numero de palabras generadas: " + n);
        System.out.println("Longitud de la cadenota: " + cadenaBuscar.length());
        System.out.println("Total de ocurrencias de \"IPN\": " + count);
        System.out.printf("Tiempo de ejecucion: %.4f segundos%n", segundos);

        // Preguntar si quiere ver la cadenota
        System.out.print("\n¿Desea mostrar la cadenota completa? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("s") || respuesta.equals("si")) {
            System.out.println("\nCadenota generada:");
            System.out.println(cadenaBuscar);
        } else {
            System.out.println("\nCadenota no mostrada.");
        }

        scanner.close();
    }

    // Método para generar la palabra aleatoria
    public static String generateRandomWord(int length) {
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterSet.length());
            sb.append(characterSet.charAt(index));
        }
        
        return sb.toString();
    }
}