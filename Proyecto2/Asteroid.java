/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Asteroide:
 * - Generación aleatoria (tamaño, velocidad y forma poligonal “dentada”).
 * - Spawn inicial en cualquier parte (con opción evitar zona).
 * - Respawn en bordes con velocidad hacia el interior.
 * - Wrap alrededor de la pantalla.
 */
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Random;

public class Asteroid {
    private static final Random R = new Random();

    // Estado (público para uso directo en colisiones/IA)
    public double x, y, vx, vy, radius;
    private final double[] shapeX, shapeY;
    private final int vertices;

    private Asteroid(double x, double y, double vx, double vy, double r,
                     double[] sx, double[] sy, int v) {
        this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.radius = r;
        this.shapeX = sx; this.shapeY = sy; this.vertices = v;
    }

    /** Crea un asteroide con parámetros y forma aleatoria. */
    private static Asteroid build(double x, double y, double vx, double vy, double r) {
        int v = Config.AST_MIN_VERTS + R.nextInt(Config.AST_MAX_VERTS - Config.AST_MIN_VERTS + 1);
        double[] sx = new double[v], sy = new double[v];
        for (int i = 0; i < v; i++) {
            double a = (i * 2 * Math.PI / v) + R.nextDouble() * Config.AST_SHAPE_JITTER;
            double rr = r * (0.75 + R.nextDouble() * 0.5);
            sx[i] = Math.cos(a) * rr;
            sy[i] = Math.sin(a) * rr;
        }
        return new Asteroid(x, y, vx, vy, r, sx, sy, v);
    }

    /** Spawn en cualquier parte (inicio). */
    public static Asteroid randomSpawn(int W, int H) {
        double r = Config.AST_MIN_R + R.nextDouble() * (Config.AST_MAX_R - Config.AST_MIN_R);
        double x = R.nextDouble() * W;
        double y = R.nextDouble() * H;
        double speed = Config.AST_MIN_SPEED + R.nextDouble() * (Config.AST_MAX_SPEED - Config.AST_MIN_SPEED);
        double ang = R.nextDouble() * Math.PI * 2;
        return build(x, y, Math.cos(ang) * speed, Math.sin(ang) * speed, r);
    }

    /** Spawn aleatorio evitando (centro, radio mínimo). */
    public static Asteroid randomSpawnAvoid(int W, int H, double avoidX, double avoidY, double minClear) {
        for (int attempts = 0; attempts < 100; attempts++) {
            double r = Config.AST_MIN_R + R.nextDouble() * (Config.AST_MAX_R - Config.AST_MIN_R);
            double x = R.nextDouble() * W;
            double y = R.nextDouble() * H;
            double dx = x - avoidX, dy = y - avoidY;
            double minCenter = minClear + r;
            if (dx*dx + dy*dy < minCenter*minCenter) continue;

            double speed = Config.AST_MIN_SPEED + R.nextDouble() * (Config.AST_MAX_SPEED - Config.AST_MIN_SPEED);
            double ang = R.nextDouble() * Math.PI * 2;
            return build(x, y, Math.cos(ang) * speed, Math.sin(ang) * speed, r);
        }
        // Fallback: en borde
        return edgeSpawn(W, H);
    }

    /** Spawn SOLO en bordes (respawn tras destrucción). */
    public static Asteroid edgeSpawn(int W, int H) {
        double r = Config.AST_MIN_R + R.nextDouble() * (Config.AST_MAX_R - Config.AST_MIN_R);

        int side = R.nextInt(4); // 0 arriba,1 abajo,2 izq,3 der
        double x, y;
        switch (side) {
            case 0:  x = R.nextDouble() * W; y = -r;      break;
            case 1:  x = R.nextDouble() * W; y = H + r;   break;
            case 2:  x = -r;                y = R.nextDouble() * H; break;
            default: x = W + r;             y = R.nextDouble() * H; break;
        }

        double speed = Config.AST_MIN_SPEED + R.nextDouble() * (Config.AST_MAX_SPEED - Config.AST_MIN_SPEED);
        double cx = W / 2.0, cy = H / 2.0;
        double a = Math.atan2(cy - y, cx - x);
        a += (R.nextDouble() - 0.5) * Math.toRadians(Config.AST_EDGE_NOISE_DEG);
        double vx = Math.cos(a) * speed, vy = Math.sin(a) * speed;

        return build(x, y, vx, vy, r);
    }

    /** Avanza un frame en base a la velocidad. */
    public void update() {
        double dt = 0.016;
        x += vx * dt; y += vy * dt;
    }

    /** Envuelve la posición para simular un “torus”. */
    public void wrap(int W, int H) {
        if (x < -radius) x = W + radius;
        if (x > W + radius) x = -radius;
        if (y < -radius) y = H + radius;
        if (y > H + radius) y = -radius;
    }

    /** Dibuja el polígono del asteroide. */
    public void draw(Graphics2D g) {
        Path2D poly = new Path2D.Double();
        poly.moveTo(x + shapeX[0], y + shapeY[0]);
        for (int i = 1; i < vertices; i++) poly.lineTo(x + shapeX[i], y + shapeY[i]);
        poly.closePath();
        g.draw(poly);
    }
}
