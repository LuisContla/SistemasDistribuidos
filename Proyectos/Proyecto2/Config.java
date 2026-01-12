/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Archivo de configuración centralizada: parámetros “tuneables” del juego.
 * Cambiando estos valores puedes ajustar el comportamiento sin tocar la lógica.
 */
public final class Config {
    private Config() {}

    // ---------- Ventana / ejecución ----------
    public static final int SCREEN_W = 1280;                // ancho ventana (px)
    public static final int SCREEN_H = 720;                 // alto ventana (px)
    public static final int DEFAULT_ASTEROIDS = 30;         // asteroides por defecto (CLI puede sobreescribir)
    public static final boolean AUTOPILOT_ON_START = true;  // iniciar en modo AUTO o MANUAL

    // ---------- Ronda / HUD ----------
    public static final double ROUND_DURATION_S = 30.0;     // duración objetivo para 100% (s)
    public static final int HUD_BAR_W = 300;                // ancho barra progreso (px)
    public static final int HUD_BAR_H = 10;                 // alto  barra progreso (px)
    public static final int HUD_TEXT_X = 12;                // posición HUD texto X
    public static final int HUD_TEXT_Y = 20;                // posición HUD texto Y

    // ---------- Spawns ----------
    public static final double START_SAFE_CLEAR_PX = 180.0; // zona segura inicial alrededor de la nave

    // ---------- IA: evasión / corredor ----------
    public static final double AI_HORIZON_S = 2.5;          // horizonte para predecir cercanía (s)
    public static final double AI_SAFETY_PAD_PX = 20.0;     // margen extra de seguridad (px)
    public static final double AI_LEFT_FRAC  = 0.25;        // límite izq del corredor (25% W)
    public static final double AI_RIGHT_FRAC = 0.75;        // límite der del corredor (75% W)
    public static final double AI_SOFT_PX = 140.0;          // rampa suave cerca del límite (px)
    public static final double AI_CENTER_BIAS = 0.10;       // sesgo al centro dentro del corredor
    public static final double AI_WOBBLE_AMP = 0.15;        // ondulación (amplitud) para no ir constante
    public static final double AI_WOBBLE_FREQ = 0.7;        // ondulación (frecuencia) Hz aprox
    public static final double AI_DEAD_ZONE = 0.08;         // zona muerta del empuje -> evita vibración

    // ---------- IA: disparo / amenazas ----------
    public static final double AI_THREAT_RADIUS_PX = 300.0; // radio seguro para considerar “amenaza”
    public static final double AI_LOOKAHEAD_S = 1.2;        // ventana para medir amenaza (s)
    public static final double AI_ALIGN_DOT = 0.95;         // alineación mínima (cos θ) para disparar

    // ---------- Ship (nave) ----------
    public static final double SHIP_ANG_SPEED_DEG_S = 540;  // vel. angular (°/s)
    public static final double SHIP_AX_MAX = 220.0;         // aceleración horizontal (px/s^2)
    public static final double SHIP_V_MAX  = 420.0;         // velocidad máxima horizontal (px/s)
    public static final double SHIP_FRICTION = 0.90;        // fricción [0..1]
    public static final double SHIP_LENGTH = 26.0;          // tamaño del triángulo (px)
    public static final double SHIP_COLLISION_RADIUS = 16.0;// radio de colisión (px)
    public static final double SHIP_BULLET_SPEED = 520.0;   // velocidad de bala (px/s)
    public static final double SHIP_SHOT_COOLDOWN_S = 0.25; // cadencia de tiro (s)
    public static final double SHIP_THRUSTER_THRESHOLD = 20.0; // umbral para dibujar “escape” trasero

    // ---------- Bullet (bala) ----------
    public static final double BULLET_RADIUS_PX = 3.0;      // radio visual/colisión de la bala
    public static final double BULLET_LIFE_S = 1.5;         // vida de la bala (s)

    // ---------- Asteroid (asteroide) ----------
    public static final double AST_MIN_R = 14.0;
    public static final double AST_MAX_R = 50.0;
    public static final double AST_MIN_SPEED = 30.0;
    public static final double AST_MAX_SPEED = 170.0;
    public static final int    AST_MIN_VERTS = 8;
    public static final int    AST_MAX_VERTS = 12;
    public static final double AST_SHAPE_JITTER = 0.30;     // “dentado” del polígono
    public static final double AST_EDGE_NOISE_DEG = 20.0;   // ruido al entrar desde borde
}
