/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Panel principal del juego:
 * - Gestiona el bucle (Timer), entrada de teclado, IA (autopiloto) y estado.
 * - Dibuja escena (asteroides, balas, nave) y HUD/overlays.
 * - Reglas de fin: GAME OVER por colisión o COMPLETADO al llegar al 100% de tiempo.
 * - Nota: se usa una ÚNICA clase interna KeyAction para minimizar .class generados.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel {
    // ===== Estado del mundo =====
    private final List<Asteroid> asteroids = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private Ship ship;
    private final int nAsteroids;

    // ===== Bucle =====
    private final Timer timer;
    private double elapsedSec = 0.0, finalSeconds = 0.0;
    private long lastNs;
    private boolean finished = false, gameOver = false, completed = false;

    // ===== Entrada (manual) =====
    private boolean left, right;            // movimiento L/R (A/D o ←/→)
    private boolean rotW, rotA, rotS, rotD; // rotación con WASD (solo manual)

    // ===== UI =====
    private final JButton restartBtn = new JButton("Reiniciar");

    // ===== IA =====
    private boolean autopilot = Config.AUTOPILOT_ON_START;

    // ===== Clase interna única para acciones de teclado =====
    /** Envuelve un Runnable para mapear acciones del teclado sin generar muchas clases anónimas. */
    private static class KeyAction extends AbstractAction {
        private final Runnable fn;
        KeyAction(Runnable fn) { this.fn = fn; }
        @Override public void actionPerformed(java.awt.event.ActionEvent e) { fn.run(); }
    }

    /** Constructor: prepara el mundo, teclado, UI y arranca el timer. */
    public GamePanel(int nAsteroids) {
        this.nAsteroids = Math.max(1, nAsteroids);
        setBackground(Color.black);
        setLayout(null);

        initGameState(); // crea nave y asteroides iniciales

        // Timer del bucle de juego (≈60 FPS) — usa lambda (no genera .class extra)
        timer = new Timer(16, ignored -> updateAndRepaint());
        lastNs = System.nanoTime();
        timer.start();

        setFocusable(true);
        requestFocusInWindow();
        installKeyBindings(); // mapea teclado con KeyAction

        // Botón de reinicio — visible solo en overlays (lambda = OK)
        restartBtn.setFocusable(false);
        restartBtn.addActionListener(ignored -> resetGame());
        restartBtn.setVisible(false);
        add(restartBtn);
    }

    /** Inicializa/reinicia el estado del juego (nave, listas, tiempo, flags). */
    private void initGameState() {
        ship = new Ship(Config.SCREEN_W * 0.5, Config.SCREEN_H * 0.75);

        asteroids.clear();
        for (int i = 0; i < this.nAsteroids; i++) {
            asteroids.add(Asteroid.randomSpawnAvoid(
                Config.SCREEN_W, Config.SCREEN_H,
                ship.getX(), ship.getY(),
                Config.START_SAFE_CLEAR_PX
            ));
        }

        bullets.clear();
        elapsedSec = 0.0;
        finalSeconds = 0.0;
        finished = gameOver = completed = false;
        left = right = false;
        rotW = rotA = rotS = rotD = false;
        lastNs = System.nanoTime();
    }

    /** Limpia estado y reanuda la simulación desde cero. */
    private void resetGame() {
        initGameState();
        restartBtn.setVisible(false);
        timer.start();
        requestFocusInWindow();
        repaint();
    }

    /** Calcula y coloca el botón de reiniciar centrado bajo el overlay. */
    private void positionRestartButton() {
        int bw = 160, bh = 36;
        int bx = (getWidth() - bw) / 2;
        int by = getHeight() / 2 + 90;
        restartBtn.setBounds(bx, by, bw, bh);
    }

    /** Re-ubica el botón si la ventana cambiara de tamaño (seguridad). */
    @Override public void invalidate() {
        super.invalidate();
        if (restartBtn != null) positionRestartButton();
    }

    /** Teclado:
     *  - M alterna AUTO/MANUAL (en release para evitar auto-repeat).
     *  - WASD rota (y A/D también empujan L/R en manual).
     *  - Flechas ←/→ también mueven en manual.
     *  - Espacio dispara solo en manual (en AUTO dispara la IA).
     */
    private void installKeyBindings() {
        final int WHEN = JComponent.WHEN_IN_FOCUSED_WINDOW;
        final InputMap im = getInputMap(WHEN);
        final ActionMap am = getActionMap();

        // --- Toggle AI (release, cubre m/M) ---
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, true), "toggle_ai");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.SHIFT_DOWN_MASK, true), "toggle_ai");
        am.put("toggle_ai", new KeyAction(() -> { autopilot = !autopilot; repaint(); }));

        // --- WASD: rotación (y A/D mueven si no hay autopiloto) ---
        String[] rkeys = {"W","A","S","D"};
        for (String k : rkeys) {
            im.put(KeyStroke.getKeyStroke(k), "press_r_"+k);
            im.put(KeyStroke.getKeyStroke("released "+k), "release_r_"+k);
        }
        am.put("press_r_W",    new KeyAction(() -> rotW = true));
        am.put("release_r_W",  new KeyAction(() -> rotW = false));

        am.put("press_r_A",    new KeyAction(() -> { rotA = true; if (!autopilot) left = true; }));
        am.put("release_r_A",  new KeyAction(() -> { rotA = false; if (!autopilot) left = false; }));

        am.put("press_r_S",    new KeyAction(() -> rotS = true));
        am.put("release_r_S",  new KeyAction(() -> rotS = false));

        am.put("press_r_D",    new KeyAction(() -> { rotD = true; if (!autopilot) right = true; }));
        am.put("release_r_D",  new KeyAction(() -> { rotD = false; if (!autopilot) right = false; }));

        // --- Flechas ←/→ (solo manual) ---
        String[] mkeys = {"LEFT","RIGHT"};
        for (String k : mkeys) {
            im.put(KeyStroke.getKeyStroke(k), "press_m_"+k);
            im.put(KeyStroke.getKeyStroke("released "+k), "release_m_"+k);
        }
        am.put("press_m_LEFT",   new KeyAction(() -> { if (!autopilot) left  = true; }));
        am.put("release_m_LEFT", new KeyAction(() -> { if (!autopilot) left  = false; }));
        am.put("press_m_RIGHT",  new KeyAction(() -> { if (!autopilot) right = true; }));
        am.put("release_m_RIGHT",new KeyAction(() -> { if (!autopilot) right = false; }));

        // --- Disparo (solo manual; el AUTO dispara por IA) ---
        im.put(KeyStroke.getKeyStroke("SPACE"), "shoot");
        am.put("shoot", new KeyAction(() -> {
            if (!autopilot) {
                Bullet b = ship.tryShoot();
                if (b != null) bullets.add(b);
            }
        }));
    }

    /** Bucle de juego:
     *  - Calcula dt y progreso por tiempo.
     *  - Actualiza IA o entrada manual, posiciones y colisiones.
     *  - Gestiona fin por completado o por colisión.
     */
    private void updateAndRepaint() {
        long now = System.nanoTime();
        double dt = (now - lastNs) / 1e9;
        if (dt <= 0 || dt > 0.1) dt = 0.016; // saneo
        lastNs = now;

        if (finished) { repaint(); return; }

        // Progreso basado en tiempo
        elapsedSec += dt;
        double progress = Math.min(1.0, elapsedSec / Config.ROUND_DURATION_S);
        if (progress >= 1.0) { completeRun(); repaint(); return; }

        // Asteroides + wrap
        for (Asteroid a : asteroids) { a.update(); a.wrap(Config.SCREEN_W, Config.SCREEN_H); }

        // IA o Manual
        if (autopilot) {
            runAutopilot(dt);
        } else {
            ship.aimWithWASD(rotW, rotA, rotS, rotD);
            ship.manualLR(left, right);
        }

        // Nave
        ship.update();
        ship.clampToScreen(Config.SCREEN_W, Config.SCREEN_H);

        // Balas
        Iterator<Bullet> ib = bullets.iterator();
        while (ib.hasNext()) {
            Bullet b = ib.next();
            b.update();
            if (!b.isAlive(Config.SCREEN_W, Config.SCREEN_H)) ib.remove();
        }

        // Colisión bala-asteroide → respawn en bordes
        ib = bullets.iterator();
        while (ib.hasNext()) {
            Bullet b = ib.next();
            boolean hit = false;
            Iterator<Asteroid> ia = asteroids.iterator();
            while (ia.hasNext()) {
                Asteroid a = ia.next();
                if (b.hits(a.x, a.y, a.radius)) {
                    ia.remove();
                    hit = true;
                    asteroids.add(Asteroid.edgeSpawn(Config.SCREEN_W, Config.SCREEN_H));
                    break;
                }
            }
            if (hit) ib.remove();
        }

        // Colisión nave-asteroide → GAME OVER
        for (Asteroid a : asteroids) {
            if (Math2D.distance(ship.getX(), ship.getY(), a.x, a.y) <= (a.radius + ship.getCollisionRadius())) {
                endRun(elapsedSec);
                break;
            }
        }

        repaint();
    }

    /** IA (autopiloto):
     *  - Evita choques con máxima cercanía predicha.
     *  - Mantiene la nave dentro del 50% central del ancho (25%-75%).
     *  - Dispara solo a “amenazas” dentro del radio seguro con lead simple.
     */
    private void runAutopilot(double dt) {
        left = right = false;
        final double W = Config.SCREEN_W, H = Config.SCREEN_H;

        double push = 0.0;

        // (1) Evasión por máxima cercanía en horizonte Config.AI_HORIZON_S
        for (Asteroid a : asteroids) {
            double rx = wrapDelta(a.x, ship.getX(), W);
            double ry = wrapDelta(a.y, ship.getY(), H);

            double rvx = a.vx - ship.getVx();
            double rvy = a.vy; // la nave no tiene vy

            double v2 = rvx*rvx + rvy*rvy;
            double t = 0.0;
            if (v2 > 1e-6) t = - (rx*rvx + ry*rvy) / v2;
            if (t < 0) t = 0;
            if (t > Config.AI_HORIZON_S) t = Config.AI_HORIZON_S;

            double px = rx + rvx * t;
            double py = ry + rvy * t;
            double d  = Math.sqrt(px*px + py*py);
            double safety = ship.getCollisionRadius() + a.radius + Config.AI_SAFETY_PAD_PX;

            if (d < safety) {
                double w = (safety - d) / safety; // [0..1]
                // px > 0 (amenaza a la derecha) → empujar a la izquierda (push positivo)
                push += Math.signum(px) * w;
            }
        }

        // (2) Corredor central (50%: 25%-75% W) con rampas suaves y sesgo al centro
        double leftBound  = Config.AI_LEFT_FRAC  * W;
        double rightBound = Config.AI_RIGHT_FRAC * W;
        double soft = Config.AI_SOFT_PX;

        if (ship.getX() < leftBound) {
            push -= 2.0; // empujar hacia la derecha
        } else if (ship.getX() < leftBound + soft) {
            double t = (leftBound + soft - ship.getX()) / soft; // 0..1
            push -= 0.8 * t;
        }

        if (ship.getX() > rightBound) {
            push += 2.0; // empujar hacia la izquierda
        } else if (ship.getX() > rightBound - soft) {
            double t = (ship.getX() - (rightBound - soft)) / soft; // 0..1
            push += 0.8 * t;
        }

        // Sesgo suave al centro para no “pegarse” a los bordes del corredor
        double center = 0.5 * W;
        double corridorHalf = (rightBound - leftBound) / 2.0;
        double offset = (ship.getX() - center) / corridorHalf; // -1..1
        push += Config.AI_CENTER_BIAS * offset;

        // (3) Variación leve para que no mantenga siempre la misma velocidad
        push += Config.AI_WOBBLE_AMP * Math.sin(elapsedSec * Config.AI_WOBBLE_FREQ);

        // (4) Aplica movimiento L/R a partir de push (con zona muerta)
        if (push > Config.AI_DEAD_ZONE)      { left = true;  right = false; }
        else if (push < -Config.AI_DEAD_ZONE){ right = true; left = false;  }
        else                                 { left = right = false;        }
        ship.manualLR(left, right);

        // (5) Selección de amenaza y disparo con lead (solo si hay amenaza)
        Asteroid target = pickThreatAsteroid(Config.AI_THREAT_RADIUS_PX, Config.AI_LOOKAHEAD_S);
        if (target != null) {
            double dx = wrapDelta(target.x, ship.getX(), W);
            double dy = wrapDelta(target.y, ship.getY(), H);
            double dist = Math.sqrt(dx*dx + dy*dy);

            double tFlight = dist / ship.getBulletSpeed();
            double tx = ship.getX() + dx + target.vx * tFlight;
            double ty = ship.getY() + dy + target.vy * tFlight;

            ship.aimTowards(tx, ty);

            double nx = Math.cos(ship.getAngle()), ny = Math.sin(ship.getAngle());
            double txv = tx - ship.getX(), tyv = ty - ship.getY();
            double tdist = Math.sqrt(txv*txv + tyv*tyv);
            if (tdist > 1) {
                double dot = (txv/tdist)*nx + (tyv/tdist)*ny; // cos del error angular
                if (dot > Config.AI_ALIGN_DOT) {
                    Bullet b = ship.tryShoot();
                    if (b != null) bullets.add(b);
                }
            }
        }
    }

    /** Selecciona un asteroide “amenaza” cuya distancia mínima predicha cae dentro del radio seguro. */
    private Asteroid pickThreatAsteroid(double safeRadius, double lookaheadSeconds) {
        final double W = Config.SCREEN_W, H = Config.SCREEN_H;
        Asteroid best = null;
        double bestD2 = Double.POSITIVE_INFINITY;

        for (Asteroid a : asteroids) {
            double rx = wrapDelta(a.x, ship.getX(), W);
            double ry = wrapDelta(a.y, ship.getY(), H);

            double rvx = a.vx - ship.getVx();
            double rvy = a.vy;

            double v2 = rvx*rvx + rvy*rvy;
            double t = 0.0;
            if (v2 > 1e-6) t = - (rx*rvx + ry*rvy) / v2;
            if (t < 0) t = 0;
            if (t > lookaheadSeconds) t = lookaheadSeconds;

            double px = rx + rvx * t;
            double py = ry + rvy * t;
            double d2 = px*px + py*py;

            if (d2 <= safeRadius * safeRadius && d2 < bestD2) {
                bestD2 = d2;
                best   = a;
            }
        }
        return best;
    }

    /** Diferencia “envuelta” para coordenadas con wrap (torus). */
    private double wrapDelta(double a, double b, double period) {
        double d = a - b;
        if (d >  period/2) d -= period;
        if (d < -period/2) d += period;
        return d;
    }

    /** Termina por colisión (GAME OVER) y muestra overlay. */
    private void endRun(double seconds) {
        finished = true; gameOver = true; timer.stop();
        finalSeconds = Math.min(seconds, Config.ROUND_DURATION_S);
        positionRestartButton(); restartBtn.setVisible(true);
    }

    /** Termina por objetivo de tiempo (COMPLETADO) y muestra overlay. */
    private void completeRun() {
        finished = true; completed = true; timer.stop();
        finalSeconds = Config.ROUND_DURATION_S;
        positionRestartButton(); restartBtn.setVisible(true);
    }

    /** Dibuja escena, HUD y overlays finales. */
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Escena
        g.setColor(Color.white);
        for (Asteroid a : asteroids) a.draw(g);
        for (Bullet b : bullets) b.draw(g);
        ship.draw(g);

        // HUD
        double progress = Math.min(1.0, elapsedSec / Config.ROUND_DURATION_S);
        int percent = (int)Math.round(progress * 100.0);
        g.drawString(
            "Progreso: " + percent + "%  (Objetivo: " + (int)Config.ROUND_DURATION_S +
            " s)   |   Rotar: WASD   |   Mover: A/D o ←/→   |   Disparo: Espacio (manual)   |   Modo: " +
            (autopilot ? "AUTO [M]" : "MANUAL [M]"),
            Config.HUD_TEXT_X, Config.HUD_TEXT_Y
        );

        g.drawRect(12, 28, Config.HUD_BAR_W, Config.HUD_BAR_H);
        int fill = (int)Math.round(Config.HUD_BAR_W * progress);
        g.fillRect(13, 29, Math.max(0, fill - 2), Config.HUD_BAR_H - 2);

        // Overlays
        if (gameOver || completed) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setComposite(old);

            g.setColor(Color.white);
            Font oldF = g.getFont();

            if (gameOver) {
                g.setFont(oldF.deriveFont(Font.BOLD, 64f));
                drawCentered(g, "GAME OVER", getHeight()/2 - 20);
                g.setFont(oldF.deriveFont(Font.PLAIN, 28f));
                drawCentered(g, "Sobreviviste " + String.format("%.1f", finalSeconds) + " s", getHeight()/2 + 30);
            } else {
                g.setFont(oldF.deriveFont(Font.BOLD, 64f));
                drawCentered(g, "¡COMPLETADO!", getHeight()/2 - 20);
                g.setFont(oldF.deriveFont(Font.PLAIN, 28f));
                drawCentered(g, "Tiempo: " + String.format("%.1f", finalSeconds) + " s", getHeight()/2 + 30);
            }
            g.setFont(oldF);
        }
    }

    /** Dibuja texto centrado horizontalmente en la coordenada Y indicada. */
    private void drawCentered(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(text)) / 2;
        g.drawString(text, tx, y);
    }
}
