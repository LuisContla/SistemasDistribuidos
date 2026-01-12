// Ejercicio 9: Número capicúa (0 a 9999, sin ciclos)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Lee un número entre 0 y 9999 e indica si es capicúa.
No usar ciclos; puede resolverse con divisiones enteras y módulo.
*/

import java.util.Scanner;

public class Ejercicio9 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Numero (0 a 9999): ");
        int n = sc.nextInt();

        if (n < 0 || n > 9999) {
            System.out.println("Fuera de rango.");
            sc.close();
            return;
        }

        boolean esCap;
        if (n < 10) {
            esCap = true;
        } else if (n < 100) {
            int a = n / 10;
            int b = n % 10;
            esCap = (a == b);
        } else if (n < 1000) {
            int a = n / 100;
            int c = n % 10;
            esCap = (a == c);
        } else {
            int a = n / 1000;
            int b = (n / 100) % 10;
            int c = (n / 10) % 10;
            int d = n % 10;
            esCap = (a == d) && (b == c);
        }

        System.out.println("Es capicua? " + (esCap ? "Si" : "No"));
        sc.close();
    }
}
