/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Bala:
 * - Se crea en la punta de la nave y viaja en su dirección.
 * - Tiene vida limitada y se elimina al expirar o salir de pantalla.
 * - Detección simple de colisión por radio.
 */
import java.awt.*;

public class Bullet {
    // Estado
    private double x, y, vx, vy;
    private double life = Config.BULLET_LIFE_S;

    public Bullet(double x, double y, double vx, double vy) {
        this.x = x; this.y = y; this.vx = vx; this.vy = vy;
    }

    /** Avanza un frame y decrementa vida. */
    public void update() {
        double dt = 0.016;
        x += vx * dt; y += vy * dt;
        life -= dt;
    }

    /** ¿Sigue viva y dentro de márgenes razonables? */
    public boolean isAlive(int W, int H) {
        if (life <= 0) return false;
        return y > -30 && y < H + 30 && x > -30 && x < W + 30;
    }

    /** Colisión por distancia al centro del asteroide. */
    public boolean hits(double ax, double ay, double ar) {
        double dx = ax - x, dy = ay - y;
        double rr = (ar + Config.BULLET_RADIUS_PX);
        return (dx*dx + dy*dy) <= rr*rr;
    }

    /** Dibujo del círculo de la bala. */
    public void draw(Graphics2D g) {
        int r = (int)Math.round(Config.BULLET_RADIUS_PX);
        g.fillOval((int)(x - r), (int)(y - r), r*2, r*2);
    }
}
