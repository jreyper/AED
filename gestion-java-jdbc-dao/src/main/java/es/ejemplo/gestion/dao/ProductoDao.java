package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductoDao extends AbstractDao{
    //private  ConexionBD conexionBD;

    public ProductoDao(String url) {
        super(url);
    }

    @Override
    Producto mapToClass(ResultSet rs) {
        try {
            return new Producto(rs.getInt(1), rs.getString(2),rs.getDouble(3) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }


    public List<Producto> listar() {
        String query = "select id,nombre,precio from productos";
        return this.consultar(query);
    }

    public Optional<Producto> buscarPorId(int id) {
        String sql = "SELECT id, nombre, precio FROM Productos WHERE id = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapeoProducto(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new AplicationException("Error al buscar el producto");
        }
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto");
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Productos WHERE id = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto");
        }
    }

    public List<Producto> buscarPorNombre(String texto){
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM Productos WHERE LOWER(nombre) LIKE LOWER(?) ORDER BY id DESC";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%"+texto+"%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapeoProducto(rs));
                }
            }

            return productos;

        } catch (SQLException e) {
            throw new AplicationException("Error al buscar por nombre");
        }
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        List<Producto> productos = new ArrayList<>();

        if (minimo > maximo) {
            return productos;
        }

        String sql = "SELECT id, nombre, precio FROM Productos WHERE precio BETWEEN ? AND ? ORDER BY id";

        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, minimo);
            ps.setDouble(2, maximo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapeoProducto(rs));
                }
            }
            return productos;

        } catch (SQLException e) {
            throw new AplicationException("Error al buscar por precio");
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Productos";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            if (rs.next()){
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto");
        }
    }

    public double precioMedio() {
        String sql = "SELECT AVG(precio) FROM Productos";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){

            if (rs.next()){
                return rs.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto");
        }
    }

    private Producto mapeoProducto(ResultSet rs) {

        try {
            return new Producto(rs.getInt(1), rs.getString(2),rs.getDouble(3) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
