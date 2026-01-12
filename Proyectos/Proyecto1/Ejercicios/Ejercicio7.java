// Ejercicio 7: Recibo de CFE (Hogar vs Negocio)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
La CFE cobra por kW según tabulador.
Hogar (por bloques): 0-250:$0.65 | 251-500:$0.85 | 501-1200:$1.50 | 1201-2100:$2.50 | 2101+:$3.00
Negocio: $5.00 por kW (tarifa fija). Pedir kW y tipo y calcular total.
*/

import java.util.Scanner;

public class Ejercicio7 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("kW consumidos (entero): ");
        int kw = sc.nextInt();
        System.out.print("Tipo de contrato (Hogar/Negocio): ");
        String tipo = sc.next().trim().toLowerCase();

        double total = 0.0;
        if (tipo.equals("negocio")) {
            total = kw * 5.0;
        } else if (tipo.equals("hogar")) {
            int restante = kw;

            int bloque = Math.min(restante, 250);
            total += bloque * 0.65;
            restante -= bloque;

            if (restante > 0) {
                bloque = Math.min(restante, 250);
                total += bloque * 0.85;
                restante -= bloque;
            }
            if (restante > 0) {
                bloque = Math.min(restante, 700);
                total += bloque * 1.50;
                restante -= bloque;
            }
            if (restante > 0) {
                bloque = Math.min(restante, 900);
                total += bloque * 2.50;
                restante -= bloque;
            }
            if (restante > 0) {
                total += restante * 3.00;
            }
        } else {
            System.out.println("Tipo no valido. Usa 'Hogar' o 'Negocio'.");
            sc.close();
            return;
        }

        System.out.println("Total a pagar: $" + total);
        sc.close();
    }
}
