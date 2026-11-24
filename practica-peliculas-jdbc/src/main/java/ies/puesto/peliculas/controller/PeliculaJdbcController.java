package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puesto.peliculas.controller.api.PeliculaRequest;
import ies.puesto.peliculas.controller.api.PeliculaResponse;
import ies.puesto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puesto.peliculas.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@Profile("jdbc")
@RequestMapping("/api/v1/jdbc/peliculas")
@Tag(name = "Películas JDBC", description = "CRUD de películas almacenadas en SQLite mediante JDBC")
public class PeliculaJdbcController {
    private final PeliculaService servicio;
    private final PeliculaApiMapper mapper;
    public PeliculaJdbcController(PeliculaService servicio, PeliculaApiMapper mapper) {
        this.servicio = servicio; this.mapper = mapper;
    }
    @GetMapping
    @Operation(summary = "Listar películas")
    @ApiResponse(responseCode = "200", description = "Listado de películas")
    public List<PeliculaResponse> listar() { return mapper.aRespuestas(servicio.listar()); }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener una película por código")
    @ApiResponse(responseCode = "200", description = "Película encontrada")
    @ApiResponse(responseCode = "400", description = "Código inválido")
    @ApiResponse(responseCode = "404", description = "Película no encontrada")
    public ResponseEntity<?> obtener(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo) {
        return servicio.obtener(codigo).<ResponseEntity<?>>map(p -> ResponseEntity.ok(mapper.aRespuesta(p)))
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "No existe ninguna película con código " + codigo));
    }
    @PostMapping
    @Operation(summary = "Crear una película")
    @ApiResponse(responseCode = "201", description = "Película creada")
    @ApiResponse(responseCode = "400", description = "Cuerpo inválido")
    @ApiResponse(responseCode = "409", description = "El código ya existe")
    public ResponseEntity<?> crear(@Valid @RequestBody PeliculaRequest peticion) {
        return servicio.crear(mapper.aDatosCreacion(peticion))
                .<ResponseEntity<?>>map(p -> ResponseEntity.created(URI.create("/api/v1/jdbc/peliculas/" + p.getIdentificador())).body(mapper.aRespuesta(p)))
                .orElseGet(() -> error(HttpStatus.CONFLICT, "Ya existe una película con código " + peticion.codigo()));
    }
    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar una película existente")
    @ApiResponse(responseCode = "200", description = "Película actualizada")
    @ApiResponse(responseCode = "400", description = "Código o cuerpo inválido")
    @ApiResponse(responseCode = "404", description = "Película no encontrada")
    public ResponseEntity<?> actualizar(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo,
                                        @Valid @RequestBody PeliculaEdicionRequest peticion) {
        return servicio.actualizar(codigo, mapper.aDatosEdicion(peticion))
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(mapper.aRespuesta(p)))
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "No existe ninguna película con código " + codigo));
    }
    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar una película")
    @ApiResponse(responseCode = "204", description = "Película eliminada sin cuerpo")
    @ApiResponse(responseCode = "400", description = "Código inválido")
    @ApiResponse(responseCode = "404", description = "Película no encontrada")
    public ResponseEntity<?> eliminar(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo) {
        return servicio.eliminar(codigo) ? ResponseEntity.noContent().build()
                : error(HttpStatus.NOT_FOUND, "No existe ninguna película con código " + codigo);
    }
    private ResponseEntity<ProblemDetail> error(HttpStatus estado, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(estado == HttpStatus.CONFLICT ? "Conflicto" : "Recurso no encontrado");
        problema.setType(URI.create("about:blank"));
        return ResponseEntity.status(estado).body(problema);
    }
}
