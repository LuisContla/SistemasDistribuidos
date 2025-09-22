/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Utilidades 2D simples usadas en colisiones y mediciones.
 */
public final class Math2D {
    private Math2D() {}

    /** Distancia euclidiana entre (x1,y1) y (x2,y2). */
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        return Math.sqrt(dx*dx + dy*dy);
    }
}
