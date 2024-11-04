# Proyecto Magneto - Detección de Mutantes

Este proyecto, **Magneto**, es una aplicación desarrollada en **Spring Boot** que determina si una secuencia de ADN pertenece a un mutante (cuando en la matriz del ADN se repiten más de una secuencia de 4 letras iguales seguidas, ya se en vertical, horizontal o diagonal). Utiliza una **base de datos H2** embebida y está desplegado en **Render**.

## Tabla de Contenidos
- [Requisitos](#requisitos)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Instalación](#instalación)
- [Endpoints](#endpoints)
  - [POST /mutant](#post-mutant)
  - [GET /stats](#get-stats)

## Requisitos

- **Java 17** o superior
- **Gradle 7.0** o superior
- **Docker** (opcional, para despliegue en contenedor)

## Tecnologías

- **Spring Boot** (Framework principal)
- **H2 Database** (Base de datos embebida)
- **Jakarta Validation** (Validación de ADN)
- **Lombok** (Reducción de código boilerplate)
- **Render** (Despliegue en nube)

## Estructura del Proyecto

- **`controllers`**: Controladores REST para los endpoints.
- **`services`**: Lógica de negocio y procesamiento de ADN.
- **`repositories`**: Interacción con la base de datos.
- **`dto`**: Data Transfer Objects para comunicación.
- **`validators`**: Validadores para la estructura de ADN.
- **`entities`**: Entidades JPA para la base de datos.

## Instalación

1. Descargar el archivo comprimido del proyecto.
2. Extraer en su equipo.
3. Abrir con un IDE de Java, como IntelliJ IDEA.
4. Instalar dependencias si es necesario.
5. Ejecutar.
6. En application.properties está el link para acceder a swagger y probar las peticiones a la API, como también está el link para acceder a la base de datos H2 y ver el registro de los ADN.
  
## Endpoints

### POST /mutant
**Descripción:** Recibe una secuencia de ADN y determina si es de un mutante.  
**URL:** `/mutant`  
**Método:** `POST`

**Request Body:**
```
{
  "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
}
```
**Responses:**
- `200 OK` si el ADN es mutante.
- `403 FORBIDDEN` si el ADN es humano.

### GET /stats
**Descripción:** Devuelve estadísticas de las verificaciones de ADN. 
**URL:** `/stats`  
**Método:** `GET`

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

