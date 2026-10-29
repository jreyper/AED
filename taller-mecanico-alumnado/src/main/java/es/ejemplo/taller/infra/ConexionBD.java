package es.ejemplo.taller.infra;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConexionBD {
    private ConexionBD() {}

    /**
     * TODO: verificar el archivo antes de conectar y comprobar las tablas.
     * Devuelve una conexión abierta cuya responsabilidad de cierre tiene el llamador.
     * Si la ruta, el archivo o el esquema no son válidos, lanza SQLException.
     */
    public static Connection abrir(String url) throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:"))
            throw new IllegalArgumentException("URL SQLite no válida");
        String ruta = url.substring("jdbc:sqlite:".length());
        if (!ruta.equals(":memory:") && !ruta.startsWith("file:") &&
                !Files.isRegularFile(Path.of(ruta)))
            throw new IllegalArgumentException("La base de datos no existe: " +
                    ruta);
        Connection cn = DriverManager.getConnection(url);
        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            cn.close();
            throw e;
        }
        return cn;
    }
    }
