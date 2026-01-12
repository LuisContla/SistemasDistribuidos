# 🎯 Proyectos - Sistemas Distribuidos

Proyectos prácticos desarrollados durante el curso **Sistemas Distribuidos** en ESCOM (9no Semestre). Progresión desde ejercicios básicos hasta una arquitectura completa de microservicios.

---

## 📋 Índice de Proyectos

| Proyecto | Descripción | Complejidad | Tecnología |
|----------|-------------|-------------|-----------|
| [🔧 Proyecto 1](#proyecto-1--ejercicios-java) | 24 Ejercicios Java | ⭐ Básica | Java |
| [🎮 Proyecto 2](#proyecto-2--juego-asteroides) | Juego 2D | ⭐⭐ Intermedia | Java Swing |
| [📊 Proyecto 3](#proyecto-3--estadísticas-electorales) | App de estadísticas | ⭐⭐ Intermedia | Java |
| [🏆 Proyecto Final](#proyecto-final--sistema-distribuido-completo) | Microservicios | ⭐⭐⭐ Avanzada | Java + React |

---

## Proyecto 1 🔧 Ejercicios Java

**Objetivo:** Consolidar fundamentos de programación orientada a objetos en Java.

**Descripción General:**
Colección de 24 ejercicios progresivos que cubren desde conceptos básicos hasta estructuras de datos y algoritmos. Diseñados para desarrollar habilidades fundamentales en Java.

**Ubicación:** [`./Proyecto1/Ejercicios/`](./Proyecto1/Ejercicios/)

### Contenido por Ejercicio:

#### Ejercicios Básicos (1-4)
- **Ejercicio 1** - Hola Mundo y variables
- **Ejercicio 2** - Operaciones aritméticas
- **Ejercicio 3** - Estructuras condicionales (if/else)
- **Ejercicio 4** - Bucles (for, while)

#### Ejercicios de Métodos (5-8)
- **Ejercicio 5** - Definición de métodos
- **Ejercicio 6** - Métodos con parámetros
- **Ejercicio 7** - Métodos con retorno
- **Ejercicio 8** - Sobrecarga de métodos

#### POO Básica (9-12)
- **Ejercicio 9** - Clases y objetos
- **Ejercicio 10** - Constructores
- **Ejercicio 11** - Encapsulación (getters/setters)
- **Ejercicio 12** - Métodos de instancia

#### POO Intermedia (13-16)
- **Ejercicio 13** - Herencia simple
- **Ejercicio 14** - Sobrescritura de métodos
- **Ejercicio 15** - Polimorfismo
- **Ejercicio 16** - Clases abstractas

#### Estructuras de Datos (17-20)
- **Ejercicio 17** - ArrayList
- **Ejercicio 18** - HashMap
- **Ejercicio 19** - LinkedList
- **Ejercicio 20** - Stack y Queue

#### Algoritmos y Utilidades (21-24)
- **Ejercicio 21** - Ordenamiento (bubble sort)
- **Ejercicio 22** - Búsqueda binaria
- **Ejercicio 23** - Recursión
- **Ejercicio 24** - Manejo de excepciones

### Archivos:
- 📄 `Ejercicio1.java` - `Ejercicio24.java` (24 archivos)
- 📄 `README.md` - Descripción del proyecto
- 📄 `Ejercicios.txt` - Lista de ejercicios

### Tecnologías:
- ✓ Java 8+
- ✓ Colecciones de Java
- ✓ OOP
- ✓ Algoritmos básicos

### Cómo ejecutar:
```bash
cd Proyecto1/Ejercicios
javac Ejercicio1.java
java Ejercicio1
```

---

## Proyecto 2 🎮 Juego Asteroides

**Objetivo:** Aplicar conceptos de programación gráfica y física en Java.

**Descripción General:**
Implementación de un juego clásico de asteroides en 2D. El jugador controla una nave espacial, dispara proyectiles y destruye asteroides. Demuestra uso de gráficos, colisiones y lógica de juego.

**Ubicación:** [`./Proyecto2/`](./Proyecto2/)

### Componentes Principales:

#### Clases de Entidades
- **`Ship.java`** 🚀 - Nave controlada por el jugador
  - Movimiento (arriba, abajo, izquierda, derecha)
  - Rotación
  - Disparo de proyectiles
  - Colisión

- **`Asteroid.java`** 💫 - Asteroides
  - Movimiento aleatorio
  - Tamaño variable (grande, mediano, pequeño)
  - Fragmentación al ser destruido
  - Colisión con proyectiles

- **`Bullet.java`** 🔫 - Proyectiles
  - Movimiento en línea recta
  - Desaparición al salir de pantalla
  - Colisión con asteroides

#### Clases de Control
- **`GamePanel.java`** 🎨 - Panel principal
  - Renderizado de gráficos
  - Loop del juego
  - Detección de colisiones
  - Gestión de eventos

- **`SimpleGui2.java`** 🖼️ - Interfaz gráfica
  - Ventana principal
  - Configuración inicial
  - Manejo de eventos del teclado

#### Clases Auxiliares
- **`Config.java`** ⚙️ - Configuración
  - Constantes del juego
  - Tamaños, velocidades, colores

- **`Math2D.java`** 📐 - Utilidades matemáticas
  - Cálculos de distancia
  - Detección de colisiones circulares
  - Vectores 2D

### Archivos:
```
Proyecto2/
├── Ship.java
├── Asteroid.java
├── Bullet.java
├── GamePanel.java
├── SimpleGui2.java
├── Config.java
├── Math2D.java
└── README.md
```

### Características del Juego:
- ✓ Control con flechas del teclado
- ✓ Múltiples asteroides
- ✓ Fragmentación de asteroides
- ✓ Sistema de puntuación
- ✓ Detectción de colisiones
- ✓ Gráficos 2D con Swing

### Tecnologías:
- ✓ Java Swing
- ✓ Graphics2D
- ✓ Física 2D básica
- ✓ Detección de colisiones

### Cómo ejecutar:
```bash
cd Proyecto2
javac *.java
java SimpleGui2
```

---

## Proyecto 3 📊 Estadísticas Electorales

**Objetivo:** Procesar y analizar datos electorales en tiempo real.

**Descripción General:**
Aplicación para generar, leer y mostrar estadísticas de votos. Demuestra lectura/escritura de archivos, procesamiento de datos y visualización en consola.

**Ubicación:** [`./Proyecto3/`](./Proyecto3/)

### Componentes Principales:

#### Clases de Datos
- **`Partido.java`** 🏛️ - Representa un partido político
  - Nombre
  - Votos obtenidos
  - Métodos de acceso

- **`VotoGenerator.java`** 🔄 - Generador de datos
  - Crea votos aleatorios
  - Genera archivos con datos
  - Distribución realista

#### Clases de Procesamiento
- **`VotoReader.java`** 📖 - Lector de datos
  - Lee archivos de votos
  - Procesa datos
  - Cuenta resultados

- **`StatsModel.java`** 📈 - Modelo de estadísticas
  - Almacena resultados de votación
  - Cálculos de estadísticas
  - Datos agregados

#### Clases de Utilidad
- **`CURPUtils.java`** 🆔 - Utilidades CURP
  - Generación de CURPs
  - Validación de CURPs
  - Extracción de información

- **`TextBarChart.java`** 📊 - Gráficos en texto
  - Genera gráficas de barras en consola
  - Escala automática
  - Etiquetas

#### Clase Principal
- **`StatsApp.java`** 🖥️ - Aplicación principal
  - Interfaz con el usuario
  - Menú de opciones
  - Coordinación de procesos

### Archivos:
```
Proyecto3/
├── VotoReader.java
├── VotoGenerator.java
├── StatsModel.java
├── StatsApp.java
├── CURPUtils.java
├── Partido.java
├── TextBarChart.java
├── README.md
└── lib/                    # Librerías externas
```

### Características:
- ✓ Generación de datos de votación
- ✓ Lectura de archivos
- ✓ Cálculo de estadísticas
- ✓ Visualización de resultados
- ✓ Gráficos de barras en consola
- ✓ Manejo de CURPs

### Tecnologías:
- ✓ File I/O
- ✓ Colecciones (ArrayList, HashMap)
- ✓ Procesamiento de datos
- ✓ Visualización en consola

### Cómo ejecutar:
```bash
cd Proyecto3
javac *.java
java StatsApp
```

---

## Proyecto Final 🏆 Sistema Distribuido Completo

**Objetivo:** Diseñar e implementar una arquitectura de microservicios completa.

**Descripción General:**
Sistema empresarial distribuido con múltiples microservicios, frontend web y componentes de monitoreo. Demuestra principios de arquitectura de microservicios, comunicación distribuida y escalabilidad.

**Ubicación:** [`./Proyecto%20Final/`](./Proyecto%20Final/)

### Arquitectura General:

```
┌─────────────────────────────────────────────────────────┐
│                    FRONTEND (React)                      │
│                   (sd-frontend)                          │
└────────────────┬────────────────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
┌───────▼────────┐  ┌─────▼──────────┐
│   API Gateway  │  │  Load Balancer │
└───────┬────────┘  └─────┬──────────┘
        │                 │
┌───────┴──────────────────┴────────────────────┐
│          MICROSERVICIOS (Java/Spring)         │
│                                               │
│  ┌──────────────┐  ┌──────────────┐         │
│  │ AuthService  │  │AccountService│         │
│  └──────────────┘  └──────────────┘         │
│                                               │
│  ┌────────────────────┐  ┌──────────────┐   │
│  │TransactionService  │  │ AuditService │   │
│  └────────────────────┘  └──────────────┘   │
│                                               │
│  ┌──────────────┐  ┌──────────────┐         │
│  │MonitorService│  │ClientSimulator│        │
│  └──────────────┘  └──────────────┘         │
└───────┬──────────────────────────────────────┘
        │
┌───────▼────────────────┐
│   sd-monitor           │
│   Dashboard de         │
│   Monitoreo            │
└────────────────────────┘
```

### Servicios Backend:

#### 🔐 AuthService
- Autenticación de usuarios
- Generación de tokens
- Validación de credenciales
- Seguridad

#### 📦 AccountService
- Gestión de cuentas de usuarios
- Información de perfiles
- Actualización de datos
- Validación de datos

#### 💰 TransactionService
- Procesamiento de transacciones
- Validación de transacciones
- Historial de movimientos
- Auditoría de transacciones

#### 🔍 AuditService
- Logging de eventos
- Trazabilidad de operaciones
- Reportes de auditoría
- Compliance

#### 👁️ MonitorService
- Monitoreo de servicios
- Métricas del sistema
- Health checks
- Alertas

#### 🎛️ ClientSimulator
- Simulación de clientes
- Pruebas de carga
- Generación de datos de prueba
- Análisis de comportamiento

### Frontend (React):

#### 📱 sd-frontend
- Interfaz de usuario moderna
- TypeScript
- Vite para build
- React para UI
- Componentes reutilizables

**Estructura:**
```
sd-frontend/
├── src/
│   ├── App.tsx
│   ├── main.tsx
│   ├── api/              # Llamadas a APIs
│   ├── auth/             # Autenticación
│   ├── layouts/          # Layouts comunes
│   ├── pages/            # Páginas principales
│   └── assets/           # Recursos estáticos
├── package.json
├── tsconfig.json
└── vite.config.ts
```

### Monitoreo:

#### 🔔 sd-monitor
- Dashboard en tiempo real
- Visualización de métricas
- Alertas
- Reporting

### Archivos Generados:

```
Proyecto Final/
├── proyectoFinal/
│   ├── AccountService/
│   ├── AuditService/
│   ├── AuthService/
│   ├── ClientSimulator/
│   ├── MonitorService/
│   ├── TransactionService/
│   └── pom.xml (Maven multi-módulo)
├── sd-frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── sd-monitor/
│   ├── src/
│   └── pom.xml
├── README.md
└── (archivos .jar compilados)
```

### Tecnologías Utilizadas:

**Backend:**
- ✓ Java 11+
- ✓ Spring Boot
- ✓ Spring Security
- ✓ Maven
- ✓ REST APIs

**Frontend:**
- ✓ React 18+
- ✓ TypeScript
- ✓ Vite
- ✓ Axios para HTTP

**DevOps:**
- ✓ Docker
- ✓ Docker Compose
- ✓ Logging distribuido
- ✓ Monitoreo

**Base de Datos:**
- ✓ SQL (para transacciones)
- ✓ Cache distribuido
- ✓ Message Queues

### Características Principales:

- ✓ Autenticación y autorización
- ✓ Servicios independientes
- ✓ API Gateway
- ✓ Load Balancing
- ✓ Service Discovery
- ✓ Circuit Breakers
- ✓ Logging distribuido
- ✓ Monitoreo en tiempo real
- ✓ Escalabilidad horizontal
- ✓ Resiliencia y tolerancia a fallos

### Cómo Ejecutar:

#### Compilar servicios:
```bash
cd proyectoFinal
mvn clean package
```

#### Ejecutar servicios individuales:
```bash
java -jar AccountService/target/account-service-1.0-SNAPSHOT.jar
java -jar AuthService/target/auth-service-1.0-SNAPSHOT.jar
# ... y así para otros servicios
```

#### Ejecutar frontend:
```bash
cd sd-frontend
npm install
npm run dev
```

#### Ejecutar monitor:
```bash
cd sd-monitor
mvn spring-boot:run
```

---

## 🚀 Comparativa de Complejidad

| Aspecto | Proyecto 1 | Proyecto 2 | Proyecto 3 | Proyecto Final |
|---------|-----------|-----------|-----------|---|
| **Arquivos** | 24 | 7 | 7 | 15+ |
| **Líneas de código** | ~500 | ~1,500 | ~1,200 | ~5,000+ |
| **Complejidad** | ⭐ | ⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| **Conceptos** | Básicos | Gráficos | Archivos | Distribuido |
| **Tiempo estimado** | 1-2 horas | 3-4 horas | 2-3 horas | 10+ horas |

---

## 📚 Progresión Recomendada

1. **Proyecto 1** - Comienza aquí para consolidar fundamentos
2. **Proyecto 2** - Desarrolla habilidades con gráficos y física
3. **Proyecto 3** - Aprende procesamiento de datos
4. **Proyecto Final** - Integra todo en una arquitectura completa

---

## 💡 Conceptos Aprendidos por Proyecto

### Proyecto 1
- Tipos de datos y variables
- Control de flujo
- Métodos y funciones
- POO: Clases, herencia, polimorfismo
- Colecciones y estructuras de datos

### Proyecto 2
- Graphics2D y Swing
- Programación de juegos
- Detección de colisiones
- Física 2D
- Eventos de teclado

### Proyecto 3
- Lectura/escritura de archivos
- Procesamiento de datos
- Estadísticas
- Interfaz de usuario en consola
- Modelado de datos

### Proyecto Final
- Arquitectura de microservicios
- REST APIs
- Seguridad distribuida
- Comunicación asíncrona
- Monitoreo y observabilidad
- Frontend moderno (React)
- DevOps básico

---

## 🔗 Enlaces Relacionados

- [📍 Volver al Repositorio Principal](../)
- [📚 Clases](../Clases/)
- [📝 Tareas](../Tareas/)

---

## 📊 Estadísticas Totales

- **Total de proyectos:** 4
- **Archivos Java:** ~45+
- **Líneas de código:** ~8,000+
- **Tecnologías:** 10+
- **Conceptos:** 50+

---

**Última actualización:** 12 de enero de 2026
**Autor:** Luis C. - Estudiante ESCOM 9no Semestre

> 💡 **Recomendación:** Realiza los proyectos en orden. Cada uno prepara las habilidades necesarias para el siguiente.
