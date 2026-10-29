package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Reparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class ReparacionDao extends AbstractDao {
    private final String url;
    public ReparacionDao(String url) { this.url = url; }

    public List<Reparacion> listar() {
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }
    public Optional<Reparacion> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE id_reparacion=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return Optional.empty();
        }
    }
    public int insertar(Reparacion dato) {
        if (dato == null){
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        String query = "INSERT INTO reparaciones (matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6, dato.getEstado());
            int filas = ps.executeUpdate();

            if (filas == 0){
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1):0;
            }
        } catch (SQLException e) {
            registrar("insertar cliente", e);
            return  0;
        }
    }
    public int actualizar(Reparacion dato) {
        if (dato == null || dato.getIdReparacion() == null )
            throw new IllegalArgumentException("debes tener id");
        String sql = " UPDATE reparaciones SET matricula = ?, id_mecanico = ?, descripcion = ?, fecha = ?, horas_trabajo = ?, estado = ? WHERE id_reparacion = ? ";

        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6, dato.getEstado());
            ps.setInt(7, dato.getIdReparacion());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("actualizar cliente", e);
            throw new IllegalArgumentException("error al actualizar", e);
        }
    }
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException();
        }
        String sql = "DELETE FROM reparaciones WHERE id_reparaciones = ?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar cliente", e);
            return 1;
        }
    }
    public List<Reparacion> buscarPorVehiculo(String valor) {
        if (valor == null) return List.of();
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE matricula=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, valor);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return List.of();
        }
    }
    public List<Reparacion> buscarPorMecanico(Integer valor) {
        if (valor == null) return List.of();
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones WHERE id_mecanico=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, valor);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return List.of();
        }
    }
    public List<Reparacion> listarPendientes() {
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado FROM reparaciones";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }

    @Override
    protected Optional mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Reparacion(rs.getInt("id_reparacion"), rs.getString("matricula"),
                    rs.getInt("id_mecanico"), rs.getString("descripcion"), rs.getString("fecha"), rs.getBigDecimal("horas_trabajo"), rs.getString("estado")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
