// Ejercicio 12: Números perfectos entre 1 y 10000
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Un número perfecto es igual a la suma de sus divisores propios (sin incluirse).
Escribe un programa que indique los números perfectos entre 1 y 10000.
*/

public class Ejercicio12 {
    public static void main(String[] args) {
        System.out.println("Numeros perfectos entre 1 y 10000:");
        for (int n = 2; n <= 10000; n++) {
            int suma = 1;
            for (int d = 2; d <= n / 2; d++) {
                if (n % d == 0) suma += d;
            }
            if (suma == n) {
                System.out.println(n);
            }
        }
    }
}
