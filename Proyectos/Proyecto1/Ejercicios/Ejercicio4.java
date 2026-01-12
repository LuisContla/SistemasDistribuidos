// Ejercicio 4: Dormitorios por sexo y edad
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Una universidad asigna dormitorios por sexo y edad.
Entrada: sexo (H/M) y edad. Validar.
Reglas:
- Hombre, 18 años = Edificio A
- Mujer, 18 años = Edificio B
- Hombre, 19 a 22 años = Edificio C
- Mujer, 19 a 22 años = Edificio D
- Hombre, >22 años = Edificio E1
- Mujer, >22 años = Edificio E2
*/

import java.util.Scanner;

public class Ejercicio4 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Sexo (H/M): ");
        String sexo = sc.next().trim().toUpperCase();

        System.out.print("Edad: ");
        int edad = sc.nextInt();

        if (!(sexo.equals("H") || sexo.equals("M")) || edad < 0) {
            System.out.println("Datos no validos. Sexo debe ser H/M y edad >= 0.");
            sc.close();
            return;
        }

        String edificio;
        if (edad == 18) {
            edificio = (sexo.equals("H")) ? "Edificio A" : "Edificio B";
        } else if (edad >= 19 && edad <= 22) {
            edificio = (sexo.equals("H")) ? "Edificio C" : "Edificio D";
        } else if (edad > 22) {
            edificio = (sexo.equals("H")) ? "Edificio E1" : "Edificio E2";
        } else {
            edificio = "No asignado (menor de 18)";
        }

        System.out.println("Asignacion: " + edificio);
        sc.close();
    }
}
