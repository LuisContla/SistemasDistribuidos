/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Nave del jugador:
 * - Movimiento horizontal con MRUA y fricción.
 * - Orientación suave hacia un ángulo objetivo (WASD o IA).
 * - Disparo en la dirección actual (sale por la punta).
 */
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class Ship {
    // Estado
    private double x, y;       // posición (centro)
    private double vx, ax;     // velocidad y aceleración en X
    private double angle = 0.0, targetAngle = 0.0; // orientación actual y objetivo (rad)
    private double timeSinceLastShot = 1e9;        // cooldown de disparo

    public Ship(double x0, double y0) { this.x = x0; this.y = y0; }

    /** Define el ángulo objetivo con WASD (combinado). */
    public void aimWithWASD(boolean w, boolean a, boolean s, boolean d) {
        int dx = (d ? 1 : 0) - (a ? 1 : 0);
        int dy = (s ? 1 : 0) - (w ? 1 : 0);
        if (dx != 0 || dy != 0) targetAngle = Math.atan2(dy, dx);
    }

    /** Apunta hacia un punto del mundo (usado por la IA). */
    public void aimTowards(double tx, double ty) {
        targetAngle = Math.atan2(ty - y, tx - x);
    }

    /** Ajusta aceleración para mover izquierda/derecha. */
    public void manualLR(boolean left, boolean right) {
        ax = 0;
        if (left)  ax -= Config.SHIP_AX_MAX;
        if (right) ax += Config.SHIP_AX_MAX;
    }

    /** Avanza un frame: MRUA en X y rotación suave hacia targetAngle. */
    public void update() {
        double dt = 0.016;

        // Movimiento en X
        vx += ax * dt;
        if (ax == 0) vx *= Config.SHIP_FRICTION;
        if (Math.abs(vx) > Config.SHIP_V_MAX) vx = Math.signum(vx) * Config.SHIP_V_MAX;
        x += vx * dt;

        // Rotación suave
        double diff = wrapAngle(targetAngle - angle);
        double maxStep = Math.toRadians(Config.SHIP_ANG_SPEED_DEG_S) * dt;
        if (diff >  maxStep) diff =  maxStep;
        if (diff < -maxStep) diff = -maxStep;
        angle = wrapAngle(angle + diff);

        timeSinceLastShot += dt;
    }

    /** Evita que la nave salga de la pantalla. */
    public void clampToScreen(int W, int H) {
        double r = Config.SHIP_COLLISION_RADIUS;
        if (x < r) { x = r; vx = 0; }
        if (x > W - r) { x = W - r; vx = 0; }
        if (y < r) y = r;
        if (y > H - r) y = H - r;
    }

    /** Dispara una bala desde la punta en la dirección actual (cooldown). */
    public Bullet tryShoot() {
        if (timeSinceLastShot < Config.SHIP_SHOT_COOLDOWN_S) return null;
        timeSinceLastShot = 0.0;

        double nx = Math.cos(angle), ny = Math.sin(angle);
        double px = x + nx * (Config.SHIP_LENGTH / 2 + 2);
        double py = y + ny * (Config.SHIP_LENGTH / 2 + 2);
        double bvx = nx * Config.SHIP_BULLET_SPEED + vx * 0.25;
        double bvy = ny * Config.SHIP_BULLET_SPEED;

        return new Bullet(px, py, bvx, bvy);
    }

    /** Dibuja el triángulo de la nave y un “escape” trasero si se mueve. */
    public void draw(Graphics2D g) {
        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(angle);

        // Triángulo apuntando hacia +X
        Path2D tri = new Path2D.Double();
        tri.moveTo(Config.SHIP_LENGTH/2, 0);
        tri.lineTo(-Config.SHIP_LENGTH/2, -Config.SHIP_LENGTH/2);
        tri.lineTo(-Config.SHIP_LENGTH/2,  Config.SHIP_LENGTH/2);
        tri.closePath();

        g.setColor(Color.white);
        g.draw(tri);

        // “Escape” solo detrás si hay movimiento visible
        if (Math.abs(vx) > Config.SHIP_THRUSTER_THRESHOLD) {
            int backX1 = (int)(-Config.SHIP_LENGTH/2);
            int backY1 = 0;
            int backX2 = (int)(-Config.SHIP_LENGTH/2 - 10);
            int backY2 = 0;
            g.drawLine(backX1, backY1, backX2, backY2);
        }

        g.setTransform(old);
    }

    /** Normaliza un ángulo a (-π, π]. */
    private static double wrapAngle(double a) {
        while (a <= -Math.PI) a += Math.PI * 2;
        while (a >   Math.PI) a -= Math.PI * 2;
        return a;
    }

    // Getters (para IA/colisiones)
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getAngle() { return angle; }
    public double getCollisionRadius() { return Config.SHIP_COLLISION_RADIUS; }
    public double getBulletSpeed() { return Config.SHIP_BULLET_SPEED; }
}
