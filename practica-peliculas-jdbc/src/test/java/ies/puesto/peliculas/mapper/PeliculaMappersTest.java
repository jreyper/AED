package ies.puesto.peliculas.mapper;

import ies.puesto.peliculas.controller.api.*;
import ies.puesto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.service.mapper.PeliculaDominioMapper;
import ies.puesto.peliculas.service.modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PeliculaMappersTest {
    private final PeliculaApiMapper api = new PeliculaApiMapper();
    private final PeliculaDominioMapper dominio = new PeliculaDominioMapper();

    @Test void mapperApiConservaLosCuatroDatosDeCreacion() {
        PeliculaRequest request = new PeliculaRequest("PEL123", "Origen", 2010, 148);
        DatosCreacionPelicula datos = api.aDatosCreacion(request);
        assertEquals("PEL123", datos.referenciaSolicitada()); assertEquals("Origen", datos.nombreSolicitado());
        assertEquals(2010, datos.anioSolicitado()); assertEquals(148, datos.minutosSolicitados());
        Pelicula p = dominio.aPelicula(datos);
        PeliculaResponse response = api.aRespuesta(p);
        assertEquals("PEL123", response.codigo()); assertEquals("Origen", response.titulo());
        assertEquals(2010, response.anioEstreno()); assertEquals(148, response.duracionMinutos());
    }
    @Test void mapperEdicionConservaDatosYUsaCodigoDeRuta() {
        DatosEdicionPelicula datos = api.aDatosEdicion(new PeliculaEdicionRequest("Interstellar", 2014, 169));
        Pelicula p = dominio.aPelicula("PEL007", datos);
        assertEquals("PEL007", p.getIdentificador()); assertEquals("Interstellar", p.getNombre());
        assertEquals(2014, p.getAnioLanzamiento()); assertEquals(169, p.getMinutosProyeccion());
    }
    @Test void mapperDeListaDevuelveTodasLasPeliculas() {
        var respuestas = api.aRespuestas(java.util.List.of(new Pelicula("PEL001", "A", 2001, 100), new Pelicula("PEL002", "B", 2002, 110)));
        assertEquals(2, respuestas.size()); assertEquals("PEL002", respuestas.get(1).codigo());
    }
}
