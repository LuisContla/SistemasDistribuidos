// Ejercicio 10: Suma de 1 hasta N
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribe un programa que solicite un número entero y calcule la suma de 1 hasta ese número.
Usar ciclo for.
*/

import java.util.Scanner;

public class Ejercicio10 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Numero entero: ");
        int n = sc.nextInt();

        int suma = 0;
        for (int i = 1; i <= n; i++) {
            suma += i;
        }

        System.out.println("Suma de 1 hasta " + n + " = " + suma);
        sc.close();
    }
}
