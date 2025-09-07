import java.util.Scanner;
public class ejercicio_1{
    public static void main(String[] args){
    long inicio = System.nanoTime(); 
    Scanner scanner = new Scanner(System.in);
    System.out.println("Introduce cuantas palabras: ");
    int n = scanner.nextInt();
    int index = 0;
    char[] cadena = new char[n*4];
    for(int i=0; i<n; i++){
        for(int j=0; j<3; j++){
            cadena[index++] = (char) (65 + (int) (Math.random() * 26));
        }
        if (i < n-1){
                cadena[index++] = ' ';
        }
    }
    int contador = 0;
    System.out.println("Posiciones donde aparece 'IPN':");
    for(int i=0; i<=cadena.length - 3; i++){
        if (cadena[i] == 'I' && cadena[i+1] == 'P' && cadena[i+2] == 'N') {
                contador++;
                System.out.println("Posición: " + i);
        }
    }
    System.out.println("\nTotal de apariciones de 'IPN': " + contador);
    long fin = System.nanoTime(); 
    double duracion = (fin - inicio) / 1_000_000_000.0; 
    System.out.println("Tiempo de ejecución: " + duracion + " segundos");
    }
}