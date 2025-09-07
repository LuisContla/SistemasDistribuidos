// Ejercicio 3: Radio de circunferencia inscrita en triángulo
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribe un programa que calcule el radio de la circunferencia inscrita en un triángulo.
Puedes usar r = 2A / P, donde A es el área (Herón) y P el perímetro.
*/

import java.util.Scanner;

public class Ejercicio3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Lado a: ");
        double a = sc.nextDouble();
        System.out.print("Lado b: ");
        double b = sc.nextDouble();
        System.out.print("Lado c: ");
        double c = sc.nextDouble();

        double s = (a + b + c) / 2.0; // semiperimetro
        double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
        double perimetro = a + b + c;
        double r = (2.0 * area) / perimetro;

        System.out.println("Radio de la circunferencia inscrita: " + r);
        sc.close();
    }
}
