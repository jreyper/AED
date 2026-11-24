# Recorrido de los datos: POST y GET por código

La API usa la ruta base `/api/v1/jdbc/peliculas`. El controlador solo está activo con el perfil `jdbc`. En todas las capas se conserva la información, pero cambian deliberadamente los nombres de las propiedades.

## POST /api/v1/jdbc/peliculas

Ejemplo de JSON:

```json
{"codigo":"PEL003","titulo":"Dune","anioEstreno":2021,"duracionMinutos":155}
```

1. **Entrada web:** Spring recibe el JSON y construye `PeliculaRequest`. `@Valid` comprueba el patrón del código, que el título no esté vacío y que año y duración estén dentro de los rangos.
2. **Frontera API → servicio:** `PeliculaJdbcController.crear` llama a `PeliculaApiMapper.aDatosCreacion`. Los nombres cambian así: `codigo` → `referenciaSolicitada`; `titulo` → `nombreSolicitado`; `anioEstreno` → `anioSolicitado`; `duracionMinutos` → `minutosSolicitados`.
3. **Frontera servicio → dominio:** `PeliculaService.crear` utiliza `PeliculaDominioMapper.aPelicula(DatosCreacionPelicula)`. Se construye `Pelicula` con `identificador`, `nombre`, `anioLanzamiento` y `minutosProyeccion`.
4. **Frontera dominio → JDBC:** el servicio llama al contrato `PeliculaRepository`. La implementación `PeliculaJdbcRepository` convierte mediante `PeliculaEntityMapper.aEntity`: `identificador` → `codigoFila`; `nombre` → `tituloFila`; `anioLanzamiento` → `anioFila`; `minutosProyeccion` → `minutosFila`.
5. **SQL:** `PeliculaJdbcRepository` ejecuta el INSERT parametrizado. En SQLite, los cuatro valores se guardan en `pelicula_codigo`, `titulo_texto`, `estreno_anio` y `duracion_minutos`. `ON CONFLICT(pelicula_codigo) DO NOTHING` evita insertar un código duplicado.
6. **Respuesta:** si `update(...) == 1`, el servicio devuelve la misma `Pelicula`. `PeliculaApiMapper.aRespuesta` transforma sus cuatro valores en `PeliculaResponse` (`codigo`, `titulo`, `anioEstreno`, `duracionMinutos`). El controlador responde **201 Created**. Si el código ya existe, el repositorio devuelve `false`, el servicio devuelve `Optional.empty()` y el controlador responde **409 Conflict** con `ProblemDetail` que nombra el código. Los errores de validación de entrada los responde Spring con **400**.

## GET /api/v1/jdbc/peliculas/{codigo}

Ejemplo: `GET /api/v1/jdbc/peliculas/PEL001`.

1. **Entrada web:** `PeliculaJdbcController.obtener` recibe el código de la ruta. `@Pattern` exige el formato `PEL` seguido de tres dígitos.
2. **Servicio:** el controlador llama a `PeliculaService.obtener(codigo)`. El servicio delega en `PeliculaRepository.buscarPorCodigo`; no conoce SQL ni clases web.
3. **SQL y fila:** `PeliculaJdbcRepository` ejecuta el SELECT con `?`. `PeliculaRowMapper` lee por nombre las columnas `pelicula_codigo`, `titulo_texto`, `estreno_anio` y `duracion_minutos` y crea `PeliculaEntity` con `codigoFila`, `tituloFila`, `anioFila` y `minutosFila`.
4. **Frontera JDBC → dominio:** `PeliculaEntityMapper.aPelicula` crea el objeto de dominio: `codigoFila` → `identificador`; `tituloFila` → `nombre`; `anioFila` → `anioLanzamiento`; `minutosFila` → `minutosProyeccion`.
5. **Frontera dominio → API:** si existe, `PeliculaApiMapper.aRespuesta` convierte `identificador` → `codigo`; `nombre` → `titulo`; `anioLanzamiento` → `anioEstreno`; `minutosProyeccion` → `duracionMinutos`. El controlador devuelve **200 OK** con JSON. Si no existe, el `Optional` vacío se convierte en **404 Not Found** con `ProblemDetail` y el código consultado. Si el formato de la ruta es incorrecto, Spring responde **400 Bad Request**.

## Estados HTTP de las operaciones

| Operación | Éxito | Errores previstos |
|---|---:|---|
| GET lista | 200 | — |
| GET por código | 200 | 400 código inválido, 404 no existe |
| POST | 201 | 400 cuerpo inválido, 409 código duplicado |
| PUT | 200 | 400 código/cuerpo inválido, 404 no existe |
| DELETE | 204 sin cuerpo | 400 código inválido, 404 no existe |
