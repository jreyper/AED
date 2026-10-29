package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class VehiculoDao extends AbstractDao {
    private final String url;
    public VehiculoDao(String url) { this.url = url; }

    public List<Vehiculo> listar() {
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }
    public Optional<Vehiculo> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE matricula=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return Optional.empty();
        }
    }
    public int insertar(Vehiculo dato) {
        if (dato == null){
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        String query = "INSERT INTO vehiculos (matricula, marca, modelo, anio, id_cliente) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5, dato.getIdCliente());
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
    public int actualizar(Vehiculo dato) {
        if (dato == null || dato.getMatricula() == null )
            throw new IllegalArgumentException("debes tener id");
        String sql = " UPDATE vehiculos SET marca = ?, modelo = ?, anio = ?, id_cliente = ? WHERE matricula = ? ";

        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getMarca());
            ps.setString(2, dato.getModelo());
            ps.setInt(3, dato.getAnio());
            ps.setInt(4, dato.getIdCliente());
            ps.setString(5, dato.getMatricula());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("actualizar cliente", e);
            throw new IllegalArgumentException("error al actualizar", e);
        }
    }
    public int eliminar(String id) {
        if (id == null) {
            throw new NullPointerException();
        }
        String sql = "DELETE FROM vehiculos WHERE matricula = ?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar cliente", e);
            throw new NullPointerException();
        }
    }
    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        if (idCliente == null) return List.of();
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE id_cliente=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return List.of();
        }
    }
    public List<Vehiculo> listarPorMarca(String marca) {
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE marca = ?";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, marca);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }

    @Override
    protected Optional mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(rs.getString("matricula"), rs.getString("marca"),
                    rs.getString("modelo"), rs.getInt("anio"), rs.getInt("id_cliente")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
