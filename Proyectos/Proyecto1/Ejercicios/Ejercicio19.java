// Ejercicio 19: ¿Arreglo de 10 números en orden descendente (no creciente)?
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Leer 10 números y verificar si están ordenados de forma descendente (no creciente),
es decir, si cada elemento es menor o igual que el anterior.
*/

import java.util.Scanner;

public class Ejercicio19 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] a = new int[10];

        System.out.println("Ingresa 10 numeros:");
        for (int i = 0; i < 10; i++) {
            a[i] = sc.nextInt();
        }

        boolean orden = true;
        for (int i = 1; i < 10; i++) {
            if (a[i] > a[i - 1]) {
                orden = false;
                break;
            }
        }

        System.out.println("Estan en orden descendente (no creciente)? " + (orden ? "Si" : "No"));
        sc.close();
    }
}
