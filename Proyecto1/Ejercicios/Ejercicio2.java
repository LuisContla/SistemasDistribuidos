// Ejercicio 2: Multiplicación de fracciones
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
La multiplicación de fracciones se define como (a/b) * (c/d) = (a*c)/(b*d).
Escriba un programa que solicite los valores de a, b, c y d (enteros) y calcule el valor de la multiplicación.
Debe mostrar el resultado en número con decimales y como fracción (ej. 17/33).
*/

import java.util.Scanner;

public class Ejercicio2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("a: ");
        int a = sc.nextInt();
        System.out.print("b: ");
        int b = sc.nextInt();
        System.out.print("c: ");
        int c = sc.nextInt();
        System.out.print("d: ");
        int d = sc.nextInt();

        int num = a * c;
        int den = b * d;
        double resultado = (double) num / den;

        System.out.println("Resultado fraccion: " + num + "/" + den);
        System.out.println("Resultado decimal: " + resultado);

        sc.close();
    }
}
