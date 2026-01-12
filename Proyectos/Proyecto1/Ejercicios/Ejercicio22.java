// Ejercicio 22: Multiplicación de matrices (tamaños y datos por teclado)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Solicitar al usuario los tamaños de las dos matrices a multiplicar, luego leer sus valores,
realizar la multiplicación y mostrar el resultado. Validar que cA == rB.
*/

import java.util.Scanner;

public class Ejercicio22 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Filas de A: ");
        int rA = sc.nextInt();
        System.out.print("Columnas de A: ");
        int cA = sc.nextInt();

        System.out.print("Filas de B: ");
        int rB = sc.nextInt();
        System.out.print("Columnas de B: ");
        int cB = sc.nextInt();

        if (cA != rB) {
            System.out.println("No se pueden multiplicar: columnas de A deben ser iguales a filas de B.");
            sc.close();
            return;
        }

        int[][] A = new int[rA][cA];
        int[][] B = new int[rB][cB];

        System.out.println("Ingresa datos de A (" + rA + "x" + cA + "):");
        for (int i = 0; i < rA; i++) {
            for (int j = 0; j < cA; j++) {
                A[i][j] = sc.nextInt();
            }
        }

        System.out.println("Ingresa datos de B (" + rB + "x" + cB + "):");
        for (int i = 0; i < rB; i++) {
            for (int j = 0; j < cB; j++) {
                B[i][j] = sc.nextInt();
            }
        }

        int[][] C = new int[rA][cB];
        for (int i = 0; i < rA; i++) {
            for (int j = 0; j < cB; j++) {
                int suma = 0;
                for (int k = 0; k < cA; k++) {
                    suma += A[i][k] * B[k][j];
                }
                C[i][j] = suma;
            }
        }

        System.out.println("Resultado C (" + rA + "x" + cB + "):");
        for (int i = 0; i < rA; i++) {
            for (int j = 0; j < cB; j++) {
                System.out.print(C[i][j] + (j < cB - 1 ? " " : ""));
            }
            System.out.println();
        }

        sc.close();
    }
}
