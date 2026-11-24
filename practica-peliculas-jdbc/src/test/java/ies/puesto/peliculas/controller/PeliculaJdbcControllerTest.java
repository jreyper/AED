package ies.puesto.peliculas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"jdbc", "test"})
class PeliculaJdbcControllerTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @BeforeAll static void crearDirectorioPruebas() throws IOException { Files.createDirectories(Path.of("target/test-data")); }

    @BeforeEach void prepararBase() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS peliculas (pelicula_codigo TEXT NOT NULL PRIMARY KEY CHECK (pelicula_codigo GLOB 'PEL[0-9][0-9][0-9]'), titulo_texto TEXT NOT NULL CHECK (length(trim(titulo_texto)) BETWEEN 1 AND 120), estreno_anio INTEGER NOT NULL CHECK (typeof(estreno_anio) = 'integer' AND estreno_anio BETWEEN 1888 AND 2100), duracion_minutos INTEGER NOT NULL CHECK (typeof(duracion_minutos) = 'integer' AND duracion_minutos BETWEEN 1 AND 1000))");
        jdbc.update("DELETE FROM peliculas");
        jdbc.update("INSERT INTO peliculas VALUES ('PEL001','Matrix',1999,136)");
        jdbc.update("INSERT INTO peliculas VALUES ('PEL002','Origen',2010,148)");
    }
    @Test void listarDevuelve200() throws Exception { mvc.perform(get("/api/v1/jdbc/peliculas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value("PEL001")); }
    @Test void obtenerDevuelve200() throws Exception { mvc.perform(get("/api/v1/jdbc/peliculas/PEL001")).andExpect(status().isOk()).andExpect(jsonPath("$.titulo").value("Matrix")); }
    @Test void obtenerInexistenteDevuelve404() throws Exception { mvc.perform(get("/api/v1/jdbc/peliculas/PEL099")).andExpect(status().isNotFound()).andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("PEL099"))); }
    @Test void codigoInvalidoDevuelve400() throws Exception { mvc.perform(get("/api/v1/jdbc/peliculas/ABC")).andExpect(status().isBadRequest()); }
    @Test void crearDevuelve201() throws Exception { mvc.perform(post("/api/v1/jdbc/peliculas").contentType("application/json").content("{\"codigo\":\"PEL003\",\"titulo\":\"Dune\",\"anioEstreno\":2021,\"duracionMinutos\":155}")).andExpect(status().isCreated()).andExpect(jsonPath("$.codigo").value("PEL003")); }
    @Test void duplicadoDevuelve409() throws Exception { mvc.perform(post("/api/v1/jdbc/peliculas").contentType("application/json").content("{\"codigo\":\"PEL001\",\"titulo\":\"Duplicada\",\"anioEstreno\":2020,\"duracionMinutos\":100}")).andExpect(status().isConflict()).andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("PEL001"))); }
    @Test void cuerpoInvalidoDevuelve400() throws Exception { mvc.perform(post("/api/v1/jdbc/peliculas").contentType("application/json").content("{\"codigo\":\"PEL003\",\"titulo\":\"   \",\"anioEstreno\":1800,\"duracionMinutos\":0}")).andExpect(status().isBadRequest()); }
    @Test void actualizarDevuelve200() throws Exception { mvc.perform(put("/api/v1/jdbc/peliculas/PEL001").contentType("application/json").content("{\"titulo\":\"Matrix Reloaded\",\"anioEstreno\":2003,\"duracionMinutos\":138}")).andExpect(status().isOk()).andExpect(jsonPath("$.codigo").value("PEL001")).andExpect(jsonPath("$.titulo").value("Matrix Reloaded")); }
    @Test void actualizarInexistenteDevuelve404() throws Exception { mvc.perform(put("/api/v1/jdbc/peliculas/PEL099").contentType("application/json").content("{\"titulo\":\"No existe\",\"anioEstreno\":2000,\"duracionMinutos\":90}")).andExpect(status().isNotFound()); }
    @Test void eliminarDevuelve204YBorra() throws Exception { mvc.perform(delete("/api/v1/jdbc/peliculas/PEL001")).andExpect(status().isNoContent()).andExpect(content().string("")); }
    @Test void eliminarInexistenteDevuelve404() throws Exception { mvc.perform(delete("/api/v1/jdbc/peliculas/PEL099")).andExpect(status().isNotFound()); }
}
