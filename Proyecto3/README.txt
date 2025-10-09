
# Proyecto: Simulador de Votaciones (Sistemas Distribuidos)

Este paquete incluye:
- `Curp.java` (tu generador, usado como dependencia)
- `CurpParser.java` (utilidades para extraer sexo/estado/edad de la CURP)
- `VoteGenerator.java` (Programa 1: genera votos a `VOTOS.dat`)
- `VoteStats.java` (Programa 2: lee `VOTOS.dat` y muestra barras de porcentaje en colores, y consultas por consola)

## Formato de `VOTOS.dat`
Cada línea: `CURP,PARTIDO` (CSV simple, UTF-8), p. ej.:
```
ABCD000101HDFXXX01,MORENA
EFGH990312MNLXXX02,PAN
```

## Partidos configurados
MC, MORENA, PAN, PRD, PRI, PT, PVEM (fijos en el código).

## Requisitos
- Java 11+ (probado con 11 y 17).
- **Lanterna 3.x** (un único `lanterna.jar`) en el mismo directorio para `VoteStats`.
  - Descarga el JAR desde su repositorio oficial (ver instrucciones en el mensaje del chat). Colócalo como `lanterna.jar` junto a los .java.

## Compilación
**Linux / macOS**
```bash
javac Curp.java CurpParser.java VoteGenerator.java
javac -cp lanterna.jar Curp.java CurpParser.java VoteStats.java
```

**Windows (PowerShell o CMD)**
```bat
javac Curp.java CurpParser.java VoteGenerator.java
javac -cp lanterna.jar; Curp.java CurpParser.java VoteStats.java
```

> Nota: Compilar de nuevo `Curp.java` con `VoteStats.java` garantiza que el bytecode esté disponible para ambos programas.

## Ejecución
1) **Generador** (produce `VOTOS.dat` en el directorio actual, agrega al final):
   - Linux/macOS:
     ```bash
     java VoteGenerator -n 50               # 50 votos/seg
     java VoteGenerator -n 100 -o datos/VOTOS.dat
     ```
   - Windows:
     ```bat
     java VoteGenerator -n 50
     java VoteGenerator -n 100 -o datos\VOTOS.dat
     ```

2) **Estadísticas en vivo** (barras horizontales por partido + consultas):
   - Linux/macOS:
     ```bash
     java -cp .:lanterna.jar VoteStats VOTOS.dat
     ```
   - Windows:
     ```bat
     java -cp .;lanterna.jar VoteStats VOTOS.dat
     ```

## Consultas por consola (en `VoteStats`)
Mientras corre la visualización, puedes escribir y presionar **Enter**:
- `sexo`    → Totales por sexo (H/M)
- `estado`  → Totales por estado (muestra top 10)
- `edad X`  → Votos con **edad exactamente X**, desglosados por sexo. Ejemplo: `edad 30`

## Notas
- Si `VOTOS.dat` ya existe, `VoteStats` empieza leyendo desde el **final** (sólo lo nuevo).
- `VoteGenerator` escribe con *flush* inmediato para que la UI vea los datos en tiempo real.
- Las barras usan colores aproximados por partido; el ancho se adapta al tamaño de la terminal.
- El archivo se maneja en modo **acumulado**, como pide la práctica.
