public class Coordenada {
    private double x;
    private double y;
    private double magnitud;

    public Coordenada(double x, double y) {
        this.x = x;
        this.y = y;
        this.magnitud = Math.sqrt(x * x + y * y); // calculamos magnitud
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getMagnitud() { return magnitud; }

    @Override
    public String toString() {
        return String.format("[%.3f, %.3f] | magnitud = %.12f", x, y, magnitud);
    }
}