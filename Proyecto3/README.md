# PROYECTO 3 — Simulación de votaciones

Autor: **Contla Mota Luis Andrés** (7CV3)  
Tecnologías: **Java 8+**, **Lanterna 3.1.3** (UI en modo texto)

---

## 1) Requisitos previos

- **Java JDK 8 o superior** (`java`, `javac` en tu PATH).
- **Windows** (probado). También funciona en Linux/macOS con pequeñas diferencias de ejecución.
- Carpeta `lib/` con **Lanterna 3.1.3**:
  - Archivo: `lib/lanterna-3.1.3.jar`
  - Si no lo tienes, descarga el **JAR principal** desde Maven Central y colócalo en `lib/`.

> **Nota Windows:** la UI usa el **emulador de terminal** de Lanterna y la fuente Consolas 18 para mejor legibilidad.

---

## 2) Estructura de carpetas sugerida

```
Proyecto3/
├─ lib/
│  └─ lanterna-3.1.3.jar
├─ CURPUtils.java
├─ Partido.java
├─ StatsApp.java
├─ StatsModel.java
├─ TextBarChart.java
├─ VotoGenerator.java
├─ VotoReader.java
└─ (se generará) VOTOS.dat
```

---

## 3) ¿Qué hace cada componente? (resumen rápido)

- **VotoGenerator**: genera votos de prueba continuamente y los escribe en `VOTOS.dat` (CURP,PARTIDO).
- **VotoReader**: hilo que “escucha” el archivo (estilo `tail -f`) y actualiza el modelo en tiempo real.
- **StatsModel**: acumula conteos por **partido**, **sexo**, **estado** y **edad** (con rangos).
- **StatsApp**: interfaz con **Lanterna** (gráficos de barras en colores, teclado para cambiar de vista).

---

## 4) Compilación

### Windows (PowerShell o CMD)

```powershell
# Situarte en la carpeta del proyecto
cd Ruta\A\Proyecto3

# (Opcional) Limpiar .class antiguos
Remove-Item *.class -ErrorAction SilentlyContinue

# Compilar todos los .java
javac -encoding UTF-8 -cp "lib\lanterna-3.1.3.jar;." *.java
```

### Linux / macOS (bash/zsh)

```bash
cd /ruta/a/Proyecto3
rm -f *.class
javac -encoding UTF-8 -cp "lib/lanterna-3.1.3.jar:." *.java
```

> Si en PowerShell ves “**error: no source files**” al usar `*.java`, asegúrate de estar en la carpeta correcta. El comodín `*.java` **sí** funciona en `cmd.exe` y PowerShell si estás en la ruta correcta.

---

## 5) Ejecución

Primero, inicia el generador de votos (en una consola) y **déjalo corriendo**:

### Windows
```powershell
java -cp "lib\lanterna-3.1.3.jar;." VotoGenerator 5
```
El `5` es opcional y representa la **velocidad** de generación (votos/seg). Cambia el número para más/menos tráfico.

Luego, en **otra** consola, abre la UI:

```powershell
# En Windows se recomienda javaw para evitar problemas con TTY
javaw -cp "lib\lanterna-3.1.3.jar;." StatsApp
```

### Linux / macOS
```bash
java -cp "lib/lanterna-3.1.3.jar:." VotoGenerator 5
java -cp "lib/lanterna-3.1.3.jar:." StatsApp
```

> **Ruta del archivo**: `VOTOGenerator` y `StatsApp` usan por defecto `VOTOS.dat` en el **directorio actual**. Asegúrate de ejecutar ambos desde la **misma carpeta**.

---

## 6) Controles de la UI

- **[0] Resumen**: barras por partido (porcentaje del total) con color por partido.
- **[1] Votos por sexo**: barras azul (H) y rosa (M), con totales.
- **[2] Votos por estado**: muestra los **32 estados** con barra, cantidad y porcentaje, en **2–3 columnas**.
- **[3] Votos por edad**: rangos `18–25`, `26–35`, `36–45`, `46–60`, `61–75`, `76–90` con barras H/M y % del total.
- **[Q] Salir**.

> La ventana se abre amplia (**140×42**). Puedes redimensionarla; la UI se adapta.

---

## 7) Personalización rápida

- **Fuente y tamaño**: en `StatsApp.java` se usa Consolas 18. Puedes cambiarla por *JetBrains Mono*, *Fira Code*, etc.
- **Colores por partido**: en `StatsApp.java`, mapa `PARTY_COLOR`.
- **Rangos de edad**: en `StatsApp.java`, arreglos `etiquetas` y `rangos`.
- **Velocidad de datos**: parámetro de `VotoGenerator` (entero, votos/segundo).

---

## 8) Problemas comunes (FAQ)

- **“To start java on Windows, use javaw!”**  
  Usa `javaw` para `StatsApp` en Windows. Ejemplo:
  ```powershell
  javaw -cp "lib\lanterna-3.1.3.jar;." StatsApp
  ```

- **“stty.exe no encontrado / CreateProcess error=2”**  
  De nuevo: usa `javaw` (evita que Lanterna intente usar TTY estilo Unix en Windows).

- **Colores o caracteres raros**  
  Compila con `-encoding UTF-8` y usa una fuente monoespaciada (Consolas, DejaVu, JetBrains Mono).

- **No veo datos**  
  Verifica que **VotoGenerator** esté corriendo y escribiendo `VOTOS.dat` en la **misma carpeta**.

