// Ejercicio 1: Centígrados a Fahrenheit
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribir un programa que reciba una cantidad en grados centígrados e indique a cuánto equivalen en grados Fahrenheit.
*/

import java.util.Scanner;

public class Ejercicio1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingresa la temperatura en °C: ");
        double c = sc.nextDouble();
        double f = (c * 9.0/5.0) + 32.0;
        System.out.println(c + " °C equivalen a " + f + " °F");
        sc.close();
    }
}
