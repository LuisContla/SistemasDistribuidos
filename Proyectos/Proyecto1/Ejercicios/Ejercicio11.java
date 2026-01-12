// Ejercicio 11: Números de Armstrong (suma de cubos de dígitos)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Encontrar los números entre 1 y 5000 que cumplan: suma del cubo de sus dígitos = número.
Ejemplo: 153 = 1^3 + 5^3 + 3^3.
*/

public class Ejercicio11 {
    public static void main(String[] args) {
        System.out.println("Numeros Armstrong entre 1 y 5000:");
        for (int n = 1; n <= 5000; n++) {
            int temp = n, suma = 0;
            while (temp > 0) {
                int dig = temp % 10;
                suma += dig * dig * dig;
                temp /= 10;
            }
            if (suma == n) {
                System.out.println(n);
            }
        }
    }
}
