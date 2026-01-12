// Ejercicio 15: Juego de adivinar un número entre 1 y 100
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
La computadora elige un número al azar entre 1 y 100.
El usuario intenta adivinar. Indicar si el intento es mayor o menor que el secreto.
Termina cuando acierta o cuando ingresa 0 para salir.
*/

import java.util.Scanner;

public class Ejercicio15 {
    public static void main(String[] args) {
        int secreto = (int)(Math.random() * 100) + 1;
        Scanner sc = new Scanner(System.in);

        System.out.println("Adivina el numero (1 a 100), 0 para salir.");

        while (true) {
            System.out.print("Tu numero: ");
            int intento = sc.nextInt();

            if (intento == 0) {
                System.out.println("Saliste del juego.");
                break;
            }
            if (intento == secreto) {
                System.out.println("Correcto! Era " + secreto);
                break;
            } else if (intento < secreto) {
                System.out.println("Es mayor.");
            } else {
                System.out.println("Es menor.");
            }
        }
        sc.close();
    }
}
