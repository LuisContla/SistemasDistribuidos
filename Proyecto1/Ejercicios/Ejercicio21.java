// Ejercicio 21: Suma de todos los elementos de una matriz 5x5
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribe un programa que lea un arreglo bidimensional 5x5 y muestre la suma total de sus elementos.
*/

import java.util.Scanner;

public class Ejercicio21 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] m = new int[5][5];

        System.out.println("Ingresa los 25 numeros (5x5):");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                m[i][j] = sc.nextInt();
            }
        }

        long suma = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                suma += m[i][j];
            }
        }

        System.out.println("Suma total = " + suma);
        sc.close();
    }
}
