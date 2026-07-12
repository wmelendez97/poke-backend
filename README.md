# Poke Backend 🐉

_Este proyecto es un backend que se conecta a la PokeAPI._

---

## Comenzando 🚀

Este proyecto está realizado con **Spring Boot 3.2.5** y se conecta a una base de datos **PostgreSQL 15.2**.

### Tecnologías Clave 🛠️

*   **Spring Boot 3.2.5**
*   **Java 17**
*   **Maven 3.9.9**
*   **PostgreSQL 15.2**
*   **Flyway** (migraciones de BD)
*   **Resilience4j** (Circuit Breaker)
*   **Springdoc OpenAPI** (Swagger UI)
*   **JWT** (JSON Web Tokens)
*   **Lombok**

---

## Ejecución del proyecto 🏃‍♂️

### Pre-requisitos 📋

Para ejecutar el proyecto y sus migraciones, es necesario poseer [Docker](https://www.docker.com/products/docker-desktop/).
Si dispones de Docker, puedes usar `docker-compose up --build`.
Si prefieres usar ASDF, asegúrate de tener `java temurin-17.0.16+8` y `maven 3.9.9` instalados con `asdf install`.

### Con Docker Compose 🐳

Para ejecutar el proyecto completo (API + Base de datos):

```bash
docker-compose up --build
```

Esto levantará:
-   **API**: `http://localhost:8080`
-   **PostgreSQL**: `localhost:5432`

### Con ASDF (Local con wsl) 💻

1.  Asegúrate de tener las versiones correctas de Java y Maven instaladas con ASDF:
    ```bash
    asdf plugin add java
    asdf plugin add maven
    asdf install
    ```
2.  Ejecuta los tests:
    ```bash
    mvn test
    ```
3.  Construye el proyecto:
    ```bash
    mvn clean package -DskipTests
    ```
4.  Ejecuta la aplicación:
    ```bash
    java -jar target/poke-backend.war
    ```

### Configuración de Base de Datos 💾

El archivo `src/main/resources/application.properties.example` contiene un ejemplo de configuración de base de datos.
**Deberás crear un archivo `application.properties`** en [src/main/resources](src/main/resources) con los valores de conexión a la base de datos.

Ejemplo de `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/poke_db
spring.datasource.username=user
spring.datasource.password=pass
...
jwt.secret=KeyDefinitionForJWTVeryLongInOrderToProgressWithTheTechnicalTest2026
```

---

## Endpoints disponibles 🌐

*   **Documentación de pruebas y Colección JSON**: [docs/](docs/)
*   **Swagger UI**: `http://localhost:8080/api/swagger-ui/index.html`
*   **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`
*   **Actuator**: `http://localhost:8080/actuator`

---

## Detener el proyecto 🛑

### Con Docker Compose:

```bash
docker-compose down
```

Para eliminar volúmenes (reset completo de BD):
```bash
docker-compose down -v
```
