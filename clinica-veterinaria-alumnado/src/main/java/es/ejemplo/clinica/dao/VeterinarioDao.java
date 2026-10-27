
package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Veterinario;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public final class VeterinarioDao
        extends AbstractDao<Veterinario>
        implements IVeterinarioDao {

    public VeterinarioDao(String url) {
        super(url);
    }

    @Override
    protected Optional<Veterinario> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Veterinario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("especialidad"),
                    rs.getBigDecimal("tarifa_hora")
            ));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass veterinario", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Veterinario> listar() {
        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar veterinarios", e);
            return List.of();
        }
    }

    @Override
    public Optional<Veterinario> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar veterinario por id", e);
            return Optional.empty();
        }
    }

    @Override
    public int insertar(Veterinario entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("El veterinario no puede ser null");
        }

        String sql = "INSERT INTO veterinarios (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";

        try (Connection con = obtenerConexion()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecialidad());
            ps.setBigDecimal(3, entidad.getTarifaHora());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            registrar("insertar veterinario", e);
            throw new DaoException("Error al insertar el veterinario", e);
        }
    }

    @Override
    public int actualizar(Veterinario entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("El veterinario y su id son obligatorios");
        }

        String sql = "UPDATE veterinarios SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecialidad());
            ps.setBigDecimal(3, entidad.getTarifaHora());
            ps.setInt(4, entidad.getId());

            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("actualizar veterinario", e);
            throw new DaoException("Error al actualizar el veterinario", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser null");
        }

        String sql = "DELETE FROM veterinarios WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("eliminar veterinario", e);
            throw new DaoException("Error al eliminar el veterinario", e);
        }
    }

    @Override
    public List<Veterinario> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return List.of();
        }

        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE especialidad = ? ORDER BY tarifa_hora ASC";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar veterinarios por especialidad", e);
            return List.of();
        }
    }
}
