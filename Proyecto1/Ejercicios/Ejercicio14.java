// Ejercicio 14: Imprimir números de Y en Y desde X hasta 200
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Leer un número X y un número Y. Mostrar los números de Y en Y, comenzando en X, hasta llegar a 200.
Ejemplo: X=8, Y=2 -> 8, 10, 12, 14, ...
Usar while (o until).
*/

import java.util.Scanner;

public class Ejercicio14 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("X inicial: ");
        int x = sc.nextInt();
        System.out.print("Y incremento: ");
        int y = sc.nextInt();

        int i = x;
        while (i <= 200) {
            System.out.print(i + (i + y <= 200 ? ", " : ""));
            i += y;
        }
        sc.close();
    }
}
