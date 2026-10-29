package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class MecanicoDao extends AbstractDao{
    private final String url;
    public MecanicoDao(String url) { this.url = url; }
    
    public List<Mecanico> listar() {
        String sql = "SELECT id_mecanico,nombre,especialidad,tarifa_hora FROM mecanicos";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }
    public Optional<Mecanico> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos WHERE id_mecanico=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return Optional.empty();
        }
    }
    public int insertar(Mecanico dato) {
        if (dato == null || dato.getTarifaHora() == null || dato.getTarifaHora().compareTo(BigDecimal.ZERO) < 0){
            return 0;
        }
        String query = "INSERT INTO mecanicos (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            int filas = ps.executeUpdate();

            if (filas == 0){
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1):0;
            }
        } catch (SQLException e) {
            registrar("insertar cliente", e);
            throw new IllegalArgumentException("Error al insertar el cliente", e);
        }
    }
    public int actualizar(Mecanico dato) {
        if (dato == null || dato.getIdMecanico() == null )
            return 0;
        String sql = " UPDATE mecanicos SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id_mecanico = ? ";

        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.setInt(4, dato.getIdMecanico());
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
        String sql = "DELETE FROM mecanicos WHERE id_mecanico = ?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar cliente", e);
            throw new NullPointerException();
        }
    }
    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null) return List.of();
        String sql = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos WHERE especialidad=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return List.of();
        }
    }

    @Override
    protected Optional mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Mecanico(rs.getInt("id_mecanico"), rs.getString("nombre"),
                    rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
