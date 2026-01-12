// Ejercicio 23: Copiar a otro archivo solo las líneas impares (1,3,5,...) del archivo original
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribe un programa que lea un archivo de texto y escriba en otro archivo solo las líneas impares del original.
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Ejercicio23 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Archivo de entrada: ");
        String in = sc.nextLine();
        System.out.print("Archivo de salida: ");
        String out = sc.nextLine();
        sc.close();

        try (BufferedReader br = new BufferedReader(new FileReader(in));
             BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {

            String linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                if (numLinea % 2 == 1) {
                    bw.write(linea);
                    bw.newLine();
                }
            }
            System.out.println("Listo. Se copiaron lineas impares a: " + out);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
