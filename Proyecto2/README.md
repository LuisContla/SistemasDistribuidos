# Proyecto 2 – Asteroides (Swing/AWT)

**Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3**

Pequeño juego estilo *Asteroids* hecho en **Java (Swing/AWT)**. Incluye modo **manual** y **autopiloto (IA)** que esquiva asteroides y dispara solo a amenazas dentro de un radio seguro. Todo el comportamiento clave es **configurable** desde `Config.java`.

---

## 🎮 Demo
> *(Opcional: agrega aquí un GIF o screenshots del juego en ejecución. Ej.: `docs/demo.gif`)*

---

## ✨ Características
- Ventana mínima **1280×720**.
- Nave:
  - Movimiento **horizontal** con aceleración y fricción.
  - **Rotación suave** hacia un ángulo objetivo (WASD).
  - Disparo **direccional** desde la punta.
- **IA (Autopiloto)**:
  - Evita colisiones con predicción de **máxima cercanía**.
  - Prefiere mantenerse en el **50% central** del ancho (25%–75%), con rampas suaves y sesgo al centro.
  - Dispara **solo** a asteroides cuya cercanía predicha cae dentro de un **radio seguro**.
- Progreso por **tiempo** (por defecto 30 s) con HUD y barra.
- Overlays de **GAME OVER** (“Sobreviviste X.X s”) y **¡COMPLETADO!**.
- Botón **Reiniciar** en pantalla de fin.
- Asteroides:
  - *Spawn* inicial al azar **evitando** una zona segura alrededor de la nave.
  - *Respawn* **en bordes** al ser destruidos; *wrap* (toroide) durante el movimiento.

---

## ⌨️ Controles
- **M**: Alternar **AUTO/MANUAL** (en *release* para evitar auto-repeat).
- **W / A / S / D**: Rotación del “frente” de la nave.
- **A / D** o **← / →**: Movimiento **izquierda / derecha** (solo en **MANUAL**).
- **Espacio**: Disparar (solo en **MANUAL**; en **AUTO** dispara la IA).
- **Reiniciar**: Botón en pantalla cuando termina la partida.

> Si juegas siempre en **AUTO**, puedes ignorar los controles de movimiento/disparo.

---

## 🧰 Requisitos
- **Java 8+** (JDK).

---

## 🛠️ Compilación y ejecución
```bash
# Compilar
javac Config.java SimpleGui2.java GamePanel.java Ship.java Asteroid.java Bullet.java Math2D.java

# Ejecutar con el número de asteroides (opcional)
java SimpleGui2 40
# Si omites el argumento, usa Config.DEFAULT_ASTEROIDS
```

---

## 🗂️ Estructura del proyecto
```
.
├── Config.java        # Configuración central (parámetros "tuneables")
├── SimpleGui2.java    # JFrame / entrada al programa
├── GamePanel.java     # Bucle del juego, IA, HUD, inputs y colisiones
├── Ship.java          # Lógica y render de la nave
├── Asteroid.java      # Lógica y render de asteroides (spawn, wrap)
├── Bullet.java        # Lógica y render de balas
└── Math2D.java        # Utilidades 2D simples
```

---

## ⚙️ Configuración (edita `Config.java`)
Todos los “números mágicos” están centralizados. Cambia estos valores sin tocar la lógica:

### Ventana / Ejecución
- `SCREEN_W`, `SCREEN_H`: dimensiones de la ventana.
- `DEFAULT_ASTEROIDS`: número de asteroides por defecto.
- `AUTOPILOT_ON_START`: iniciar en **AUTO** o **MANUAL**.

### Ronda / HUD
- `ROUND_DURATION_S`: segundos para completar 100%.
- `HUD_BAR_W/H`, `HUD_TEXT_X/Y`: tamaños/posiciones del HUD.

### Spawns
- `START_SAFE_CLEAR_PX`: radio seguro para no *spawnear* encima de la nave al inicio.

### IA – evasión / corredor
- `AI_HORIZON_S`, `AI_SAFETY_PAD_PX`: predicción y margen.
- `AI_LEFT_FRAC`, `AI_RIGHT_FRAC`: corredor (25%–75% por defecto).
- `AI_SOFT_PX`: rampa suave cerca del corredor.
- `AI_CENTER_BIAS`: sesgo al centro para evitar “pegarse” a bordes.
- `AI_WOBBLE_AMP/FREQ`, `AI_DEAD_ZONE`: variación y zona muerta del empuje.

### IA – disparo / amenazas
- `AI_THREAT_RADIUS_PX`: radio seguro para considerar **amenaza**.
- `AI_LOOKAHEAD_S`: ventana para medir cercanía predicha.
- `AI_ALIGN_DOT`: umbral de alineación (cos θ) para disparar.

### Nave
- `SHIP_ANG_SPEED_DEG_S`, `SHIP_AX_MAX`, `SHIP_V_MAX`, `SHIP_FRICTION`.
- `SHIP_LENGTH`, `SHIP_COLLISION_RADIUS`.
- `SHIP_BULLET_SPEED`, `SHIP_SHOT_COOLDOWN_S`.
- `SHIP_THRUSTER_THRESHOLD` (decorativo).

### Bala
- `BULLET_RADIUS_PX`, `BULLET_LIFE_S`.

### Asteroide
- `AST_MIN/MAX_R`, `AST_MIN/MAX_SPEED`, `AST_MIN/MAX_VERTS`, `AST_SHAPE_JITTER`, `AST_EDGE_NOISE_DEG`.

---

## 🧪 Reglas del juego
- **GAME OVER** al colisionar nave–asteroide (overlay con “Sobreviviste X.X s”).
- **COMPLETADO** al alcanzar 100% del tiempo (`ROUND_DURATION_S`).
- Al destruir un asteroide, aparece otro **en el borde** con dirección hacia el interior.

---

## 📝 Notas de implementación
- Entrada con **Key Bindings** y **una sola** clase interna `KeyAction` para minimizar `.class` generados.
- **IA** basada en:
  - Predicción de **máxima cercanía** en un horizonte `AI_HORIZON_S`.
  - Empuje horizontal acumulado con rampa en límites del corredor y sesgo al centro.
  - Disparo con **lead** simple y alineación `AI_ALIGN_DOT`.

---

## 🐞 Problemas conocidos
- Si el framerate cae, la predicción puede sentirse “conservadora”. Ajusta `AI_HORIZON_S` o `AI_SAFETY_PAD_PX`.
- Con números de asteroides muy altos, podría ser necesario aumentar el tamaño de la ventana o bajar el *cooldown* de balas.

---

## 👤 Créditos
- **Alumno:** Luis Andrés Contla Mota
- **Materia:** Sistemas Distribuidos 7CV3
- **Proyecto:** 2
