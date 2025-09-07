// Ejercicio 20: Rotar 10 números una posición hacia adelante
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Leer una serie de 10 números, moverlos una posición hacia adelante en el arreglo y mostrar el resultado.
Ejemplo: 1 2 3 4 5 -> 5 1 2 3 4.
*/

import java.util.Scanner;

public class Ejercicio20 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] a = new int[10];

        System.out.println("Ingresa 10 numeros:");
        for (int i = 0; i < 10; i++) {
            a[i] = sc.nextInt();
        }

        int ultimo = a[9];
        for (int i = 9; i > 0; i--) {
            a[i] = a[i - 1];
        }
        a[0] = ultimo;

        System.out.println("Arreglo rotado:");
        for (int i = 0; i < 10; i++) {
            System.out.print(a[i] + (i < 9 ? " " : "\n"));
        }
        sc.close();
    }
}
