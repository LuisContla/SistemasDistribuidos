// Ejercicio 17: Leer 10 enteros, guardar en orden inverso y mostrar
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Leer 10 números enteros, guardarlos en orden inverso al introducido y mostrarlos en pantalla.
*/

import java.util.Scanner;

public class Ejercicio17 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] arr = new int[10];

        System.out.println("Ingresa 10 numeros enteros:");
        for (int i = 0; i < 10; i++) {
            arr[i] = sc.nextInt();
        }

        System.out.println("En orden inverso:");
        for (int i = 9; i >= 0; i--) {
            System.out.print(arr[i] + (i > 0 ? " " : "\n"));
        }
        sc.close();
    }
}
