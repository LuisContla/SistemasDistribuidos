public class Geeks {

    static volatile int variable_compartida = 0;

    static long hilo1Id;
    static long hilo2Id;

    private class Worker implements Runnable {
        private final int n;

        Worker(int n) { this.n = n; }

        @Override
        public void run() {
            long id = Thread.currentThread().threadId();
            for (int i = 0; i < n; i++) {
                modifica(id);
            }
        }
    }

    public static void modifica(long id) {
        if (id == hilo1Id) {
            variable_compartida++;
        } else if (id == hilo2Id) {
            variable_compartida--;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int n = (args.length > 0) ? Integer.parseInt(args[0]) : 1;

        Geeks g = new Geeks();

        Thread t1 = new Thread(g.new Worker(n));
        Thread t2 = new Thread(g.new Worker(n));

        hilo1Id = t1.threadId();
        hilo2Id = t2.threadId();

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(variable_compartida);
    }
}