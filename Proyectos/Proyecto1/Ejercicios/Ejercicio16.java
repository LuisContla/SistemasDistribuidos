// Ejercicio 16: Promedio de números hasta que se ingrese 0
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Leer números mientras sean distintos de 0. Al final, calcular y mostrar el promedio de los números leídos.
Usar while.
*/

import java.util.Scanner;

public class Ejercicio16 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int suma = 0, count = 0;

        System.out.println("Ingresa numeros (0 para terminar):");

        while (true) {
            int num = sc.nextInt();
            if (num == 0) break;
            suma += num;
            count++;
        }

        if (count > 0) {
            double prom = (double) suma / count;
            System.out.println("Promedio = " + prom);
        } else {
            System.out.println("No se ingresaron numeros validos.");
        }

        sc.close();
    }
}
