// Ejercicio 6: Cobro en caseta por ejes
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Calcular el cobro de caseta según reglas:
- Motocicleta: $20
- 2 ejes (auto): $40
- 3 ejes (camioneta): $60
- 4–6 ejes (camión de carga): $250
- Eje adicional (>6): +$50 c/u
Ejemplo: 8 ejes => $350.
*/

import java.util.Scanner;

public class Ejercicio6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Es motocicleta? (s/n): ");
        String esMoto = sc.next().trim().toLowerCase();

        if (esMoto.equals("s")) {
            System.out.println("Cobro: $20");
            sc.close();
            return;
        }

        System.out.print("Numero de ejes: ");
        int ejes = sc.nextInt();

        int cobro;
        if (ejes == 2) {
            cobro = 40;
        } else if (ejes == 3) {
            cobro = 60;
        } else if (ejes >= 4 && ejes <= 6) {
            cobro = 250;
        } else if (ejes > 6) {
            cobro = 250 + (ejes - 6) * 50;
        } else {
            cobro = 0;
        }

        System.out.println("Cobro: $" + cobro);
        sc.close();
    }
}
