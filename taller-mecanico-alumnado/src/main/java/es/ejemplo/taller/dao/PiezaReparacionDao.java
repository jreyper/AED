package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.PiezaReparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class PiezaReparacionDao extends AbstractDao<PiezaReparacion> {
    private final String url;
    public PiezaReparacionDao(String url) { this.url = url; }
    
    public List<PiezaReparacion> listar() {
        String sql = "SELECT id_pieza,id_reparacion,nombre,cantidad,precio_unitario FROM piezas_reparacion";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }
    public Optional<PiezaReparacion> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id_pieza,id_reparacion,nombre,cantidad,precio_unitario FROM piezas_reparacion WHERE id_pieza=?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar cliente por id", e);
            return Optional.empty();
        }
    }
    public int insertar(PiezaReparacion dato) {
        if (dato == null || dato.getCantidad() <= 0 || dato.getPrecioUnitario() == null || dato.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0){
            return  0;
        }
        String query = "INSERT INTO piezas_reparacion (id_reparacion,nombre,cantidad,precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());

            if (ps.executeUpdate() == 0){
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
    public int actualizar(PiezaReparacion dato) {
        if (dato == null || dato.getIdPieza() == null )
            throw new IllegalArgumentException("debes tener id");
        String sql = " UPDATE piezas_reparacion SET id_reparacion = ?, nombre = ?, cantidad = ?, precio_unitario = ? WHERE id_pieza = ? ";

        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            ps.setInt(5, dato.getIdPieza());
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
        String sql = "DELETE FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection connection = ConexionBD.abrir(url);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar cliente", e);
            throw new NullPointerException();
        }
    }
    public List<PiezaReparacion> listarPorReparacion(Integer id) {
        String sql = "SELECT id_pieza, id_reparacion, nombre, cantidad, precio_unitario FROM piezas_reparacion WHERE id_reparacion = ?";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,id);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }
    public BigDecimal calcularCostePiezas(Integer id) {
        String sql = "SELECT SUM(cantidad * precio_unitario) FROM piezas_reparacion WHERE id_reparacion = ?";
        try (Connection con = ConexionBD.abrir(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,id);
            return consultarBigDecimal(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    protected Optional mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new PiezaReparacion(rs.getInt("id_pieza"), rs.getInt("id_reparacion"),
                    rs.getString("nombre"), rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
