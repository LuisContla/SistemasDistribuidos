import java.util.Random;

public class PilaCompartida {
    private final char[] pila = new char[10];
    private int tope = -1;
    private boolean modificada = false;

    private final Random rand = new Random();

    public synchronized void push(char c) {
        if (tope < pila.length - 1) {
            pila[++tope] = c;
            modificada = true;
            notifyAll();
        }
    }

    public synchronized char pop() {
        if (tope >= 0) {
            char c = pila[tope--];
            modificada = true;
            notifyAll();
            return c;
        }
        return '\0';
    }

    public synchronized void imprimir() {
        if (modificada) {
            // limpiar pantalla
            System.out.print("\033[H\033[2J");
            System.out.flush();

            System.out.println("Contenido de la pila:");
            for (int i = tope; i >= 0; i--) {
                System.out.println("[" + i + "] " + pila[i]);
            }
            System.out.println("Tope = " + tope);
            System.out.println("------------------");
            modificada = false;
        }
    }

    // --- Hilo Productor ---
    private class Productor implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(rand.nextInt(1000) + 500);
                } catch (InterruptedException e) { e.printStackTrace(); }
                char c = (char) ('A' + rand.nextInt(26));
                push(c);
            }
        }
    }

    // --- Hilo Consumidor ---
    private class Consumidor implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(rand.nextInt(1200) + 500);
                } catch (InterruptedException e) { e.printStackTrace(); }
                pop();
            }
        }
    }

    // --- Hilo Impresor ---
    private class Impresor implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) { e.printStackTrace(); }
                imprimir();
            }
        }
    }

    public static void main(String[] args) {
        PilaCompartida pc = new PilaCompartida();

        Thread productor = new Thread(pc.new Productor());
        Thread consumidor = new Thread(pc.new Consumidor());
        Thread impresor  = new Thread(pc.new Impresor());

        productor.start();
        consumidor.start();
        impresor.start();
    }
}