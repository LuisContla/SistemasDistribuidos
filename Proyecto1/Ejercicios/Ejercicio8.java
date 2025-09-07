// Ejercicio 8: Ordenar 3 números de mayor a menor (sin ciclos)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribe un programa que pida tres números y los muestre ordenados de mayor a menor.
No usar ciclos ni algoritmos de ordenamiento; puede resolverse con if.
*/

import java.util.Scanner;

public class Ejercicio8 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Numero 1: ");
        int a = sc.nextInt();
        System.out.print("Numero 2: ");
        int b = sc.nextInt();
        System.out.print("Numero 3: ");
        int c = sc.nextInt();

        if (a < b) { int t = a; a = b; b = t; }
        if (a < c) { int t = a; a = c; c = t; }
        if (b < c) { int t = b; b = c; c = t; }

        System.out.println("Orden (mayor a menor): " + a + ", " + b + ", " + c);
        sc.close();
    }
}
