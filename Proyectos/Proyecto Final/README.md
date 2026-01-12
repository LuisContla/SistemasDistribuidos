# Proyecto Final - Simulador de Banca Electrónica

## 📋 Descripción del Proyecto

Este proyecto implementa un **simulador de banca electrónica** basado en una arquitectura de microservicios distribuidos. El sistema está diseñado para demostrar los principios de sistemas distribuidos mediante la implementación de cuatro servicios especializados: autenticación, gestión de cuentas, procesamiento de transacciones y auditoría. La solución utiliza tecnologías en la nube de AWS (RDS, SQS y SNS) para garantizar escalabilidad, confiabilidad y comunicación asincrónica entre componentes. El frontend desarrollado en React proporciona una interfaz intuitiva para interactuar con los servicios de banca electrónica, mientras que el backend implementado en Java asegura el procesamiento seguro de operaciones financieras.

## ✏️ Instalación y compilación

### Pre-requisitos
- ☕ Java JDK 17 (o superior)
- 📦 Maven (para compilar los servicios Java)
- 🟢 Node.js & npm (para correr el Frontend)
- 🌐 Conexión a Internet (Necesaria para conectar con AWS RDS, SQS y SNS)

### Front-End
- 🟢 Instalar la versión más reciente de Node.js
- 📁 Abrir el proyecto sd-frontend en consola o IDE
- 📥 Ejecutar el comando `npm install`
- ▶️ Ejecutar el comando `npm run dev`

### Back-End

#### Paso 1️⃣ - Compilar Microservicios

Compilar cada microservicio para generar el archivo `.jar` ejecutable. Abre una terminal en la carpeta raíz de cada servicio y ejecuta:

```bash
mvn clean package
```

Repite este proceso para cada microservicio:

| Servicio | Descripción |
|----------|-------------|
| 🔐 AuthService | Servicio de autenticación |
| 💰 AccountService | Gestión de cuentas bancarias |
| 💸 TransactionService | Procesamiento de transacciones |
| 📊 AuditService | Registro de auditoría |

#### Paso 2️⃣ - Transferir archivos JAR a las instancias en la nube

Usa el comando `scp` para subir los archivos a los servidores. **Reemplaza `PON_LA_IP_AQUI` por la IP pública real** de cada máquina.

> ⚠️ **Nota:** Estos comandos asumen que estás en la carpeta donde tienes guardado el archivo `VPC-key.pem`. Si ves error de "Identity file not found", asegúrate de estar en la carpeta correcta.

```bash
# 🔐 AuthService
scp -i VPC-key.pem /mnt/c/Users/Karla/Documents/proyectoFinal/AuthService/target/auth-service-1.0-SNAPSHOT.jar ubuntu@PON_LA_IP_AQUI:/home/ubuntu

# 💰 AccountService (ejecutar 2 veces: Instancia 1 e Instancia 2)
scp -i VPC-key.pem /mnt/c/Users/Karla/Documents/proyectoFinal/AccountService/target/account-service-1.0-SNAPSHOT.jar ubuntu@PON_LA_IP_AQUI:/home/ubuntu

# 💸 TransactionService (ejecutar 2 veces: Instancia 1 e Instancia 2)
scp -i VPC-key.pem /mnt/c/Users/Karla/Documents/proyectoFinal/TransactionService/target/transaction-service-1.0-SNAPSHOT.jar ubuntu@PON_LA_IP_AQUI:/home/ubuntu

# 📊 AuditService
scp -i VPC-key.pem /mnt/c/Users/Karla/Documents/proyectoFinal/AuditService/target/audit-service-1.0-SNAPSHOT.jar ubuntu@PON_LA_IP_AQUI:/home/ubuntu
```

> 📌 **Ajusta** la ruta `/mnt/c/Users/Karla/Documents/proyectoFinal/` según la ubicación real de los archivos JAR en tu sistema.

#### Paso 3️⃣ - Ejecutar los microservicios en las instancias

Conectate a cada instancia y ejecuta los servicios:

```bash
# 🔐 Auth Service
java -jar auth-service-1.0-SNAPSHOT.jar

# 💰 Account Service
java -jar account-service-1.0-SNAPSHOT.jar

# 💸 Transaction Service
java -jar transaction-service-1.0-SNAPSHOT.jar

# 📊 Audit Service
java -jar audit-service-1.0-SNAPSHOT.jar
```

#### ⚠️ Solución de problemas

Si recibes el error **"Address already in use"** (porque se quedó pegado el proceso anterior), ejecuta este comando antes de iniciar el JAR:

```bash
pkill -f java
```

## Autores
Proyecto desarrollado por:
- 👤 Contla Mota Luis Andres
- 👤 Maya Fabela Jose Eduardo
- 👤 Herrera Tovar Karla Elena
