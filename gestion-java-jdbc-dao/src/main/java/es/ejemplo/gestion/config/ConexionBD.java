
package es.ejemplo.gestion.config;

import es.ejemplo.gestion.exception.AplicationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre una conexión en la base SQLite indicada por el constructor. */
public class ConexionBD {

    private String url;

    public ConexionBD(String url) {
        this.url = url;

        try {
            Path path = Path.of(url.substring(12));
            if (!Files.exists(path)) {
                throw new AplicationException("La base de datos no existe");
            }
        } catch (AplicationException e) {
            throw e;
        }
    }

    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new AplicationException("Error de conexion con SQLite");
        }
    }
}
