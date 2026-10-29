package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDao<T> {
    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());
    protected abstract Optional<T> mapRsToClass(ResultSet rs);

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, "Fallo JDBC: " + operacion, e);
    }

    protected List<T> consultar(PreparedStatement ps) {
        if (ps == null) return List.of();
        try (ResultSet rs = ps.executeQuery()) {
            List<T> resultado = new ArrayList<>();
            while (rs.next()) {
                Optional<T> entidad = mapRsToClass(rs);
                if (entidad.isEmpty()) return List.of();
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

    protected int ejecutar(PreparedStatement ps) {
        if (ps == null) return 0;
        try { return ps.executeUpdate(); }
        catch (SQLException | RuntimeException e) {
            registrar("ejecutar", e);
            return 0;
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
            throw new IllegalArgumentException("Error al consultar el importe", e);
        }
    }

}
