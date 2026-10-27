package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionDB;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDao<T> extends ConexionDB {
    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());

    protected abstract Optional<T> mapRsToClass(ResultSet rs);

    protected AbstractDao(String url) {
        super(url);
    }

    protected List<T> consultar(PreparedStatement ps) {
        if (ps == null) return List.of();
        try (ResultSet rs = ps.executeQuery()) {
            List<T> resultado = new ArrayList<>();
            while (rs.next()) {
                Optional<T> entidad = mapRsToClass(rs);
                if (entidad.isEmpty()) return List.of(); // No devolver una lista parcial
                resultado.add(entidad.get());
            }
            return resultado;
        } catch (SQLException | RuntimeException e) {
            registrar("consultar", e);
            return List.of();
        }
    }

    protected Optional<T> consultarUno(PreparedStatement ps) {
        if (ps == null) return Optional.empty();
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapRsToClass(rs) : Optional.empty();
        } catch (SQLException | RuntimeException e) {
            registrar("consultarUno", e);
            return Optional.empty();
        }
    }

    protected int insertarId(PreparedStatement ps) {
        try {
            if (ps == null) {
                return 0;
            }

            int filas = ps.executeUpdate();
            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            registrar("Fallo en la consulta de Insertar: ", e);
            return 0;
        }
    }

    protected int ejecutar(PreparedStatement ps) {
        if (ps == null) return 0;
        try {
            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("ejecutar", e);
            return 0;
        }
    }

    protected int consultarEntero(PreparedStatement ps) {
        if (ps == null) return 0;
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException | RuntimeException e) {
            registrar("consultarEntero", e);
            return 0;
        }
    }

    protected double consultarDecimal(PreparedStatement ps) {
        if (ps == null) return 0;
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException | RuntimeException e) {
            registrar("consultarDecimal", e);
            return 0;
        }
    }

    protected Map<String, Integer> consultarAgrupado(PreparedStatement ps) {
        if (ps == null) return Map.of();
        try (ResultSet rs = ps.executeQuery()) {
            Map<String, Integer> mapa = new LinkedHashMap<>();
            while (rs.next()) mapa.put(rs.getString(1), rs.getInt(2));
            return mapa;
        } catch (SQLException | RuntimeException e) {
            registrar("consultarAgrupado", e);
            return Map.of();
        }
    }

    protected BigDecimal consultarBigDecimal(PreparedStatement ps) {
        if (ps == null) {
            return BigDecimal.ZERO;
        }

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal resultado = rs.getBigDecimal(1);
                return resultado != null ? resultado : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException | RuntimeException e) {
            registrar("consultarBigDecimal", e);
            throw new DaoException("Error al consultar el importe", e);
        }
    }

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, "Fallo JDBC: " + operacion, e);
    }

}
