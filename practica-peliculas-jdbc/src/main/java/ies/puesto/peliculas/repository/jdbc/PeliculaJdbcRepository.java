package ies.puesto.peliculas.repository.jdbc;

import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.repository.PeliculaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class PeliculaJdbcRepository implements PeliculaRepository {
    private static final String SELECT_TODAS = "SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos FROM peliculas ORDER BY pelicula_codigo";
    private static final String SELECT_CODIGO = "SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos FROM peliculas WHERE pelicula_codigo = ?";
    private static final String INSERTAR = "INSERT INTO peliculas (pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos) VALUES (?, ?, ?, ?) ON CONFLICT(pelicula_codigo) DO NOTHING";
    private static final String ACTUALIZAR = "UPDATE peliculas SET titulo_texto = ?, estreno_anio = ?, duracion_minutos = ? WHERE pelicula_codigo = ?";
    private static final String ELIMINAR = "DELETE FROM peliculas WHERE pelicula_codigo = ?";
    private final JdbcTemplate jdbcTemplate;
    private final PeliculaRowMapper rowMapper;
    private final PeliculaEntityMapper entityMapper;

    public PeliculaJdbcRepository(JdbcTemplate jdbcTemplate, PeliculaRowMapper rowMapper, PeliculaEntityMapper entityMapper) {
        this.jdbcTemplate = jdbcTemplate; this.rowMapper = rowMapper; this.entityMapper = entityMapper;
    }
    @Override public List<Pelicula> buscarTodas() {
        return jdbcTemplate.query(SELECT_TODAS, rowMapper).stream().map(entityMapper::aPelicula).toList();
    }
    @Override public Optional<Pelicula> buscarPorCodigo(String identificador) {
        return jdbcTemplate.query(SELECT_CODIGO, rowMapper, identificador).stream().findFirst().map(entityMapper::aPelicula);
    }
    @Override public boolean insertarSiAusente(Pelicula pelicula) {
        PeliculaEntity fila = entityMapper.aEntity(pelicula);
        return jdbcTemplate.update(INSERTAR, fila.getCodigoFila(), fila.getTituloFila(), fila.getAnioFila(), fila.getMinutosFila()) == 1;
    }
    @Override public boolean actualizarSiExiste(Pelicula pelicula) {
        PeliculaEntity fila = entityMapper.aEntity(pelicula);
        return jdbcTemplate.update(ACTUALIZAR, fila.getTituloFila(), fila.getAnioFila(), fila.getMinutosFila(), fila.getCodigoFila()) == 1;
    }
    @Override public boolean eliminar(String identificador) {
        return jdbcTemplate.update(ELIMINAR, identificador) == 1;
    }
}
