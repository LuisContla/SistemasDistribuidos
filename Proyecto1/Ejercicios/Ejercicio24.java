// Ejercicio 24: Frecuencia de letras (A-Z) en un archivo de texto
// Código creado por Luis Andrés Contla Mota
// Sistemas Distribuidos 7CV3

/*
INSTRUCCIONES:
Escribe un programa que lea un archivo de texto plano y cuente cuántas veces aparece cada letra.
Mostrar frecuencias para A–Z (no distinguir mayúsculas/minúsculas).
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Ejercicio24 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Archivo a analizar: ");
        String in = sc.nextLine();
        sc.close();

        long[] cnt = new long[26];
        Arrays.fill(cnt, 0L);

        try (BufferedReader br = new BufferedReader(new FileReader(in))) {
            int ch;
            while ((ch = br.read()) != -1) {
                char c = Character.toUpperCase((char) ch);
                if (c >= 'A' && c <= 'Z') {
                    cnt[c - 'A']++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.println("Frecuencias de letras A-Z:");
        for (int i = 0; i < 26; i++) {
            char letra = (char)('A' + i);
            System.out.println(letra + ": " + cnt[i]);
        }
    }
}
