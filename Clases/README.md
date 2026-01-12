# 📚 Clases - Sistemas Distribuidos

Materiales y ejercicios de cada sesión de clase del curso **Sistemas Distribuidos** en ESCOM (9no Semestre). Progresión estructurada de conceptos desde lo básico hasta arquitecturas complejas.

---

## 🗂️ Estructura y Progresión

### Fase 1️⃣: Fundamentos (Clases 5-7)

#### [Clase 5](./Clase%205/) 🐍 Ejercicios Básicos en Java y Python
**Tema:** Introducción a programación concurrente y búsqueda de patrones

**Contenido:**
- Generación de cadenas aleatorias
- Búsqueda de patrones (IPN)
- Optimización de búsquedas
- Medición de tiempos de ejecución
- Comparación Java vs Python

**Archivos:**
- `ejercicio_1.java` - Búsqueda manual de patrones
- `ejercicio_2.java` - Búsqueda optimizada con StringBuilder
- `ejercicio_3.py` - Implementación en Python

**Conceptos clave:**
- ✓ Generación de números aleatorios
- ✓ Medición de rendimiento
- ✓ Optimización de memoria
- ✓ Diferencias entre lenguajes

---

#### [Clase 6](./Clase%206/) 📐 Geometría - Coordenadas y Rectángulos
**Tema:** Programación orientada a objetos con figuras geométricas

**Contenido:**
- Clase `Coordenada` para representar puntos
- Clase `Rectangulo` con cálculo de áreas
- Polígonos irregulares
- Ordenamiento por magnitud

**Archivos:**
- `Coordenada.java` - Clase para representar puntos
- `Rectangulo.java` - Clase para rectángulos
- `PruebaRectangulo.java` - Programa de prueba
- `Ejercicio 3/` - Polígonos irregulares

**Conceptos clave:**
- ✓ Encapsulación
- ✓ Getters y Setters
- ✓ Cálculo de áreas
- ✓ ArrayList y ordenamiento

---

#### [Clase 7](./Clase%207/) 🔶 Figuras Geométricas - Herencia y Polimorfismo
**Tema:** Orientación a objetos avanzada

**Contenido:**
- Clase abstracta `Figura`
- Interfaces `Desplazable`
- Clases `Rectangulo` y `TrianguloEq`
- Desplazamiento de figuras
- Cálculo de áreas

**Archivos:**
- `Figura.java` - Clase abstracta base
- `Desplazable.java` - Interfaz
- `Coordenada.java` - Clase auxiliar
- `Rectangulo.java` - Implementación
- `TrianguloEq.java` - Triángulo equilátero
- `Clase7.java` - Programa principal

**Conceptos clave:**
- ✓ Herencia
- ✓ Polimorfismo
- ✓ Interfaces
- ✓ Clases abstractas

---

### Fase 2️⃣: Concurrencia (Clases 10-12)

#### [Clase 10](./Clase%2010/) 🔄 Pilas Compartidas - Concurrencia
**Tema:** Sincronización con threads

**Contenido:**
- Estructura de datos compartida: Pila
- Patrón Productor-Consumidor
- Sincronización con métodos `synchronized`
- `notify()` y `notifyAll()`
- Impresor en tiempo real

**Archivos:**
- `PilaCompartida.java` - Pila thread-safe
- `Geeks.java` - Variables compartidas y atomicidad

**Conceptos clave:**
- ✓ Threads en Java
- ✓ Secciones críticas
- ✓ Sincronización
- ✓ Productor-Consumidor
- ✓ Race conditions

---

#### [Clase 12](./Clase%2012/) 🆔 Validación CURP
**Tema:** Concurrencia avanzada con ThreadPools

**Contenido:**
- Generación de CURPs válidos
- Ordenamiento concurrente
- `ExecutorService` y `ThreadPool`
- `Future` para resultados asíncrónos
- Medición de rendimiento con diferentes tamaños de pool

**Archivos:**
- `Curp.java` - Generador y ordenador de CURPs

**Conceptos clave:**
- ✓ ExecutorService
- ✓ Fixed Thread Pools
- ✓ Future y callbacks
- ✓ Análisis de rendimiento

---

### Fase 3️⃣: Servidores Web Básicos (Clases 13-14)

#### [Clase 13](./Clase%2013/) 🌐 WebServer Básico
**Tema:** Introducción a servidores HTTP

**Contenido:**
- HttpServer de Java
- Endpoints básicos: `/status` y `/task`
- Manejo de requests POST
- Cálculo de multiplicaciones
- Headers personalizados

**Archivos:**
- `WebServer.java` - Servidor HTTP con 2 endpoints

**Conceptos clave:**
- ✓ HttpServer
- ✓ HttpContext
- ✓ HttpExchange
- ✓ Handlers
- ✓ Thread Pools en servidores

---

#### [Clase 14](./Clase%2014/) 📊 WebServer Intermedio
**Tema:** Análisis de headers y debugging

**Contenido:**
- Análisis de headers HTTP
- Debugging con headers personalizados (X-Debug)
- Medición de tiempos en nanosegundos
- Información de request/response

**Archivos:**
- `WebServer.java` - WebServer con análisis de headers

**Conceptos clave:**
- ✓ Headers HTTP
- ✓ Request Body
- ✓ Debug info
- ✓ Timing information

---

### Fase 4️⃣: Servidores Web Avanzados (Clases 20-23)

#### [Clase 20](./Clase%2020/) 🔗 WebServer - APIs Externas
**Tema:** Integración con APIs externas

**Contenido:**
- Endpoints: `/task`, `/status`, `/quotes`
- Llamadas a APIs externas
- HttpClient para requests
- Manejo de respuestas JSON

**Archivos:**
- `WebServer.java` - WebServer con integración de APIs

**Conceptos clave:**
- ✓ HttpClient
- ✓ API calls
- ✓ JSON responses
- ✓ Error handling

---

#### [Clase 21](./Clase%2021/) 📮 WebServer - JSONPlaceholder
**Tema:** POST requests y múltiples endpoints

**Contenido:**
- Endpoints: `/task`, `/status`, `/quotes`, `/posts`, `/translate`
- Integración con JSONPlaceholder
- Integración con Google Translate API
- Query parameters
- URL encoding

**Archivos:**
- `WebServer.java` - WebServer completo con 5 endpoints

**Conceptos clave:**
- ✓ GET y POST
- ✓ Query strings
- ✓ Multiple APIs
- ✓ URL encoding/decoding

---

#### [Clase 22](./Clase%2022/) 🌍 WebServer - Google Cloud Storage
**Tema:** Integración con servicios en la nube

**Contenido:**
- Endpoints anteriores más `/gcs`
- Integración con Google Cloud Storage JSON API
- Autenticación con Bearer tokens
- Manejo de bytes y archivos

**Archivos:**
- `WebServer.java` - WebServer con 6 endpoints incluyendo GCS

**Conceptos clave:**
- ✓ Cloud APIs
- ✓ Authentication
- ✓ Binary data handling
- ✓ Bearer tokens

---

#### [Clase 23](./Clase%2023/) 🔤 Serialización JSON con GSON
**Tema:** Procesamiento avanzado de JSON

**Contenido:**
- Librería GSON de Google
- Parsing y serialización de JSON
- Llamadas a Breaking Bad Quotes API
- Integración con Google Translate v2
- Manejo de JsonArray y JsonObject

**Archivos:**
- `GsonExample.java` - Ejemplo con GSON

**Conceptos clave:**
- ✓ GSON library
- ✓ JSON parsing
- ✓ JSON serialization
- ✓ API integration

---

### Fase 5️⃣: Temas Especializados (Clases 27, 39)

#### [Clase 27](./Clase%2027/) 🎓 Conceptos Avanzados
**Tema:** Temas avanzados de sistemas distribuidos

**Contenido:**
- Ampliación de conceptos previos
- Patrones de diseño
- Optimizaciones avanzadas

---

#### [Clase 39](./Clase%2039/) 🏗️ Maven Project - WebServer Completo
**Tema:** Proyecto profesional con Maven

**Contenido:**
- Estructura Maven estándar
- WebServer con interfaz web
- HTML, CSS, JavaScript frontend
- Jackson para JSON
- UI assets integrados
- Tests unitarios

**Archivos:**
- `my-app/`
  - `pom.xml` - Configuración Maven
  - `src/main/java/` - Código Java
  - `src/main/resources/` - Assets (HTML, CSS, JS)
  - `src/test/` - Tests

**Clases Java:**
- `App.java` - Punto de entrada
- `WebServer.java` - Servidor HTTP
- `FrontendSearchRequest.java` - Modelo de request
- `FrontendSearchResponse.java` - Modelo de response

**Assets:**
- `index.html` - Interfaz web
- `javascript.js` - Lógica frontend
- `style.css` - Estilos

**Conceptos clave:**
- ✓ Maven project structure
- ✓ POM configuration
- ✓ Frontend + Backend
- ✓ REST API design
- ✓ Jackson databind

---

### Fase 6️⃣: Documentación y Evaluación (Clases 35, 38, 40, 42)

#### [Clase 35](./Clase%2035/) 📖 Documentación de Sistemas Distribuidos
**Tema:** Documentación técnica

---

#### [Clase 38](./Clase%2038/) 🏗️ Análisis y Diseño de Arquitecturas
**Tema:** Diseño de sistemas distribuidos

---

#### [Clase 40](./Clase%2040/) 🔒 Seguridad en Sistemas Distribuidos
**Tema:** Seguridad y encriptación

---

#### [Clase 42](./Clase%2042/) ⚡ Evaluación y Mejora de Rendimiento
**Tema:** Optimización y evaluación final

---

## 📈 Progresión de Temas

```
Semana 1-2:   Fundamentos (OOP)
Semana 3-4:   Concurrencia y Threads
Semana 5-6:   Servidores Web Básicos
Semana 7-10:  Servidores Web Avanzados + APIs
Semana 11-12: Proyectos integrados
Semana 13-14: Evaluación y mejora
```

---

## 🛠️ Tecnologías por Clase

| Clase | Lenguaje | Tecnología | Tema |
|-------|----------|-----------|------|
| 5 | Java, Python | Básico | Algoritmos |
| 6-7 | Java | OOP | Clases y Herencia |
| 10-12 | Java | Threads | Concurrencia |
| 13-14 | Java | HttpServer | Web Básico |
| 20-23 | Java | HttpServer + APIs | Web Avanzado |
| 27 | Java | Varios | Especializado |
| 39 | Java | Maven | Proyecto Profesional |
| 35, 38, 40, 42 | Documentación | Análisis | Evaluación |

---

## 📚 Cómo Usar Este Repositorio

### Orden Recomendado de Estudio:
1. **Comenzar por:** Clase 5 (fundamentos)
2. **Seguir con:** Clases 6-7 (OOP)
3. **Luego:** Clases 10-12 (concurrencia)
4. **Después:** Clases 13-14 (web básico)
5. **Continuar:** Clases 20-23 (web avanzado)
6. **Proyecto:** Clase 39 (integración completa)
7. **Finalmente:** Clases 35-42 (evaluación)

### Para cada clase:
1. Lee el README de la carpeta
2. Revisa los archivos `.java`
3. Ejecuta el código con ejemplos
4. Modifica y experimenta

---

## 🚀 Cómo Compilar y Ejecutar

### Clases básicas (5-27):
```bash
cd "Clase X"
javac archivo.java
java NombreClase
```

### Clase 39 (Maven):
```bash
cd "Clase 39/my-app"
mvn clean package
java -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 💡 Requisitos Previos

- JDK 8 o superior
- Maven 3.6+
- Conocimiento básico de Java
- Experiencia con línea de comandos

---

## 📊 Estadísticas

- **Total de clases:** 14 carpetas principales
- **Archivos Java:** ~25+
- **Archivos de configuración:** pom.xml, etc.
- **Líneas de código:** ~2,000+

---

## 🔗 Enlaces Relacionados

- [📍 Volver al Repositorio Principal](../)
- [📝 Tareas](../Tareas/)
- [🎯 Proyectos](../Proyectos/)

---

**Última actualización:** 12 de enero de 2026
**Autor:** Luis C. - Estudiante ESCOM 9no Semestre

> 💡 **Recomendación:** Estudia de manera ordenada siguiendo la progresión. Cada clase construye sobre los conceptos anteriores.
