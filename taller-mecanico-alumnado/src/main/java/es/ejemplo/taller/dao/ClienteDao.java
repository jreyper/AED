package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class ClienteDao extends AbstractDao{
    private final String url;
    public ClienteDao(String url) { this.url = url; }

    public List<Cliente> listar() {
        String sql = "SELECT id_cliente,nombre,email,telefono FROM clientes";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id_cliente, nombre, email, telefono FROM clientes WHERE id_cliente=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return Optional.empty();
        }
    }
    public int insertar(Cliente dato) {
        if (dato == null){
            return 0;
        }
        String query = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            int filas = ps.executeUpdate();

            if (filas == 0){
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1):0;
            }
        } catch (SQLException e) {
            registrar("insertar cliente", e);
            return 0;
        }
    }
    public int actualizar(Cliente dato) {
        if (dato == null || dato.getIdCliente() == null )
            throw new IllegalArgumentException("debes tener id");
        String sql = " UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id_cliente = ? ";

        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            ps.setInt(4, dato.getIdCliente());
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
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar cliente", e);
            throw new NullPointerException();
        }
    }

    @Override
    protected Optional mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"),
                    rs.getString("email"), rs.getString("telefono")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
