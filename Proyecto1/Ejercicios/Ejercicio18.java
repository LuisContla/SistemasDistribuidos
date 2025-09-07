// Ejercicio 18: Ciudad con el nombre más largo (10 nombres)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Leer un arreglo de 10 nombres de ciudades e indicar cuál tiene el nombre más largo.
*/

import java.util.Scanner;

public class Ejercicio18 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] ciudades = new String[10];

        System.out.println("Ingresa 10 nombres de ciudades:");
        sc.nextLine();
        for (int i = 0; i < 10; i++) {
            String linea = sc.nextLine();
            ciudades[i] = linea;
        }

        String mayor = ciudades[0];
        for (int i = 1; i < 10; i++) {
            if (ciudades[i].length() > mayor.length()) {
                mayor = ciudades[i];
            }
        }

        System.out.println("Ciudad con nombre mas largo: " + mayor + " (" + mayor.length() + " caracteres)");
        sc.close();
    }
}
