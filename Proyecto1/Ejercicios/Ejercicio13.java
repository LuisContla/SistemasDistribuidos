// Ejercicio 13: Ahorro en cuenta con reinversión (20 años)
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Calcular cuánto dinero habrá al final de 20 años si al inicio de cada año se añaden $10,000,
el rendimiento anual es 5% y se reinvierten las ganancias cada año.
*/

public class Ejercicio13 {
    public static void main(String[] args) {
        double saldo = 0.0;
        for (int anio = 1; anio <= 20; anio++) {
            saldo += 10000.0;
            saldo *= 1.05;
        }
        System.out.println("Saldo al final de 20 años: $" + saldo);
    }
}
