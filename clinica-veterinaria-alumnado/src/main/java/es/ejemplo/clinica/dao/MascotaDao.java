package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Consulta;
import es.ejemplo.clinica.modelo.Mascota;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class MascotaDao extends AbstractDao<Mascota> implements IMascotaDao {
    @Override
    protected Optional<Mascota> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Mascota(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getString("fecha_nacimiento"),
                    rs.getInt("id_cliente")
            ));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass mascota", e);
            return Optional.empty();
        }
    }

    public MascotaDao(String url) {
        super(url);
    }

    @Override
    public List<Mascota> listar() {
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar mascotas", e);
            return List.of();
        }
    }

    @Override
    public Optional<Mascota> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar mascota por id", e);
            return Optional.empty();
        }
    }

    @Override
    public int insertar(Mascota entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La mascota no puede ser null");
        }

        String sql = "INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, id_cliente) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecie());
            ps.setString(3, entidad.getRaza());
            ps.setString(4, entidad.getFechaNacimiento());
            ps.setInt(5, entidad.getIdCliente());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            registrar("insertar mascota", e);
            throw new DaoException("Error al insertar la mascota", e);
        }
    }

    @Override
    public int actualizar(Mascota entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException(
                    "La mascota y su id son obligatorios"
            );
        }

        String sql = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, id_cliente = ? WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecie());
            ps.setString(3, entidad.getRaza());
            ps.setString(4, entidad.getFechaNacimiento());
            ps.setInt(5, entidad.getIdCliente());
            ps.setInt(6, entidad.getId());

            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("actualizar mascota", e);
            throw new DaoException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser null");
        }

        String sql = "DELETE FROM mascotas WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("eliminar mascota", e);
            throw new DaoException("Error al eliminar la mascota", e);
        }
    }

    @Override
    public List<Mascota> buscarPorCliente(Integer idCliente) {
        if (idCliente == null) {
            return List.of();
        }

        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id_cliente = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar mascotas por cliente", e);
            return List.of();
        }
    }

    @Override
    public List<Mascota> listarPorEspecie(String especie) {
        if (especie == null || especie.isBlank()) {
            return List.of();
        }

        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE especie = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, especie);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar mascotas por especie", e);
            return List.of();
        }
    }
}
