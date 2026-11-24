package ies.puesto.peliculas.repository.jdbc;

import ies.puesto.peliculas.model.Pelicula;
import org.junit.jupiter.api.Test;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PeliculaJdbcMappersTest {
    @Test void entityMapperConservaLosCuatroValoresEnAmbosSentidos() {
        PeliculaEntityMapper mapper = new PeliculaEntityMapper();
        Pelicula original = new Pelicula("PEL123", "Blade Runner", 1982, 117);
        PeliculaEntity fila = mapper.aEntity(original);
        assertEquals("PEL123", fila.getCodigoFila());
        assertEquals("Blade Runner", fila.getTituloFila());
        assertEquals(1982, fila.getAnioFila());
        assertEquals(117, fila.getMinutosFila());
        Pelicula recuperada = mapper.aPelicula(fila);
        assertEquals("PEL123", recuperada.getIdentificador());
        assertEquals("Blade Runner", recuperada.getNombre());
        assertEquals(1982, recuperada.getAnioLanzamiento());
        assertEquals(117, recuperada.getMinutosProyeccion());
    }

    @Test void rowMapperLeeLasCuatroColumnasSql() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("pelicula_codigo")).thenReturn("PEL456");
        when(rs.getString("titulo_texto")).thenReturn("Arrival");
        when(rs.getInt("estreno_anio")).thenReturn(2016);
        when(rs.getInt("duracion_minutos")).thenReturn(116);
        PeliculaEntity fila = new PeliculaRowMapper().mapRow(rs, 0);
        assertEquals("PEL456", fila.getCodigoFila());
        assertEquals("Arrival", fila.getTituloFila());
        assertEquals(2016, fila.getAnioFila());
        assertEquals(116, fila.getMinutosFila());
    }
}
