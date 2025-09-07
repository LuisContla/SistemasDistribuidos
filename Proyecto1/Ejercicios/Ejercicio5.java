// Ejercicio 5: Días de vacaciones por antigüedad
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribir un programa que indique cuántos días de vacaciones corresponden según años trabajados:
- 1 a 5 años: 5 días
- 6 a 10 años: 10 días
- 11 a 19: 10 + (años - 10) días
- 20+ años: 20 + 2*(años - 20) hasta máximo 45 días.
*/

import java.util.Scanner;

public class Ejercicio5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Anios trabajados: ");
        int anios = sc.nextInt();

        int dias;
        if (anios <= 0) {
            dias = 0;
        } else if (anios <= 5) {
            dias = 5;
        } else if (anios <= 10) {
            dias = 10;
        } else if (anios <= 19) {
            dias = 10 + (anios - 10);
        } else {
            dias = 20 + 2 * (anios - 20);
            if (dias > 45) dias = 45;
        }

        System.out.println("Dias de vacaciones: " + dias);
        sc.close();
    }
}
