package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Tratamiento;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public final class TratamientoDao extends AbstractDao<Tratamiento> implements ITratamientoDao {
    @Override
    protected Optional<Tratamiento> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Tratamiento(
                    rs.getInt("id"),
                    rs.getInt("id_consulta"),
                    rs.getString("descripcion"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("precio_unitario")
            ));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass tratamiento", e);
            return Optional.empty();
        }
    }

    public TratamientoDao(String url) {
        super(url);
    }

    @Override
    public List<Tratamiento> listar() {
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar tratamientos", e);
            return List.of();
        }
    }

    @Override
    public Optional<Tratamiento> buscarPorId(Integer id) {
        if (id == null || id < 0) {
            return Optional.empty();
        }

        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar tratamiento por id", e);
            return Optional.empty();
        }
    }

    @Override
    public int insertar(Tratamiento entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException(
                    "El tratamiento no puede ser null"
            );
        }

        String sql = "INSERT INTO tratamientos (id_consulta, descripcion, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entidad.getIdConsulta());
            ps.setString(2, entidad.getDescripcion());
            ps.setInt(3, entidad.getCantidad());
            ps.setBigDecimal(4, entidad.getPrecioUnitario());

            int filas = ps.executeUpdate();

            if (filas == 0){
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1):0;
            }
        } catch (SQLException e) {
            registrar("insertar tratamiento", e);
            throw new DaoException("Error al insertar el tratamiento", e);
        }
    }

    @Override
    public int actualizar(Tratamiento entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException(
                    "El tratamiento y su id son obligatorios"
            );
        }

        String sql = "UPDATE tratamientos SET id_consulta = ?, descripcion = ?, cantidad = ?, precio_unitario = ? WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entidad.getIdConsulta());
            ps.setString(2, entidad.getDescripcion());
            ps.setInt(3, entidad.getCantidad());
            ps.setBigDecimal(4, entidad.getPrecioUnitario());
            ps.setInt(5, entidad.getId());

            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("actualizar tratamiento", e);
            throw new DaoException("Error al actualizar el tratamiento", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser null");
        }

        String sql = "DELETE FROM tratamientos WHERE id = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("eliminar tratamiento", e);
            throw new DaoException("Error al eliminar el tratamiento", e);
        }
    }

    @Override
    public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
        if (idConsulta == null) {
            return List.of();
        }

        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id_consulta = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar tratamientos por consulta", e);
            return List.of();
        }
    }

    @Override
    public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
        if (idConsulta == null) {
            return BigDecimal.ZERO;
        }

        String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) FROM tratamientos WHERE id_consulta = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);

           return consultarBigDecimal(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("calcular coste de tratamientos", e);
            throw new DaoException(
                    "Error al calcular el coste de los tratamientos", e
            );
        }
    }
 
}
