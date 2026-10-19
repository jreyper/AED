package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import es.ejemplo.gestion.model.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoDao extends AbstractDao<Vehiculo>{
    private  ConexionBD conexionBD;

    public VehiculoDao(String url) {
        super(url);
    }

    @Override
    Vehiculo mapToClass(ResultSet rs) {
        try {
            return new Vehiculo(rs.getString(1), rs.getString(2),rs.getString(3), rs.getInt(4));
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }

    public List<Vehiculo> listar() {
        String query = "select matricula, marca, modelo, anio from Vehiculos ORDER BY matricula";
        return consultar(query);
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, matricula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapeoVehiculo(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new AplicationException("Error al buscar el producto");
        }
    }

    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMatricula());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMarca());
            ps.setString(2, vehiculo.getModelo());
            ps.setInt(3, vehiculo.getAnio());
            ps.setString(4, vehiculo.getMatricula());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto");
        }
    }

    public boolean eliminar(String matricula) {
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto");
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE LOWER(marca) LIKE LOWER(?) ORDER BY matricula DESC";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%"+texto+"%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapeoVehiculo(rs));
                }
            }

            return vehiculos;

        } catch (SQLException e) {
            throw new AplicationException("Error al buscar por nombre");
        }
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        List<Vehiculo> vehiculos = new ArrayList<>();

        if (desde > hasta) {
            return vehiculos;
        }

        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE anio BETWEEN ? AND ? ORDER BY matricula";

        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, desde);
            ps.setInt(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapeoVehiculo(rs));
                }
            }
            return vehiculos;

        } catch (SQLException e) {
            throw new AplicationException("Error al buscar por precio");
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Vehiculos";

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

    public Map<String, Integer> contarPorMarca() {
        Map<String, Integer> contador = new LinkedHashMap<>();
        String sql = "SELECT marca, COUNT(*) AS cantidad FROM Vehiculos WHERE marca LIKE ? GROUP BY marca ORDER BY marca";

        try (Connection connection = this.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contador.put(rs.getString("marca"), rs.getInt("cantidad"));
                }
            }

        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return contador;
    }


    private Vehiculo mapeoVehiculo(ResultSet rs) {

        try {
            return new Vehiculo(rs.getString(1), rs.getString(2),rs.getString(3), rs.getInt(4));
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
