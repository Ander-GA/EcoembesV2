# Ecoembes Management API - Backend ♻️⚙️

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)](https://gradle.org/)

## 📝 Descripción del Proyecto

Este repositorio contiene el **Backend (API RESTful)** de un sistema distribuido diseñado para la gestión y monitorización de infraestructuras de reciclaje. 

El sistema centraliza la lógica de negocio para administrar plantas de reciclaje, flotas de contenedores, niveles de llenado y asignaciones de empleados, sirviendo los datos a las aplicaciones cliente de forma segura y estructurada.

> 🖥️ **Nota:** Este proyecto es la API del servidor. Puedes encontrar el cliente de escritorio (Java Swing) que consume esta API en el repositorio: [Ecoembes Client](https://github.com/Ander-GA/EcoembesClient).

## ⚙️ Arquitectura y Patrones

El servidor está estructurado bajo principios de código limpio y separación de responsabilidades:

* **Arquitectura Multicapa:** Separación estricta entre Controladores (Facade), Servicios (Lógica de Negocio) y Repositorios (DAO).
* **Patrón DTO (Data Transfer Object):** Implementado para empaquetar y transferir la información a través de la red, ocultando la estructura interna de la base de datos al cliente.
* **Patrón DAO y Entidades:** Uso de persistencia relacional para modelar `Containers`, `PlantasDeReciclaje`, `Empleados` y sus relaciones.
* **Gateways Externos:** Diseño preparado para la integración con sistemas de terceros mediante interfaces (`IRecyclingPlantGateway`).

## 🚀 Despliegue Local

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/Ander-GA/](https://github.com/Ander-GA/)(https://github.com/Ander-GA/EcoembesV2.git)
   ```
2. **Ejecutar el servidor:**
   ```bash
     ./gradlew bootRun
   ```
El servidor se iniciará y quedará a la escucha de peticiones HTTP en el puerto configurado (por defecto 8080).
