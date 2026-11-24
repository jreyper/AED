# API REST de películas con JDBC

Proyecto de gestión de películas desarrollado con Java 21, Spring Boot, JDBC y SQLite.

## Ejecutar

```bash
mvn clean test
mvn spring-boot:run -Dspring-boot.run.profiles=jdbc
```

La API queda disponible en `http://localhost:8080/api/v1/jdbc/peliculas`.

La documentación interactiva de Swagger está disponible en `http://localhost:8080/swagger-ui.html`.

La base de datos utilizada está en `data/peliculas.sqlite3`. Para comprobar que los datos persisten, crea una película, detén la aplicación, vuelve a iniciarla y consulta su código.

La explicación de los recorridos de creación y consulta está en `docs/SERVICIO.md`.
