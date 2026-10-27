package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.infra.ConexionDB;
import es.ejemplo.clinica.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class ClienteDao extends AbstractDao<Cliente> implements IClienteDao{
 public ClienteDao(String url) {
    super(url);
 }

 @Override
 public List<Cliente> listar() {
     String sql = "SELECT id,nombre,email,telefono FROM Clientes";
     try (Connection con = obtenerConexion() ;
          PreparedStatement ps = con.prepareStatement(sql)) {
         return consultar(ps);
     } catch (SQLException | RuntimeException e) {
         registrar("listar clientes", e);
         return List.of();
     }
 }

 @Override
 public Optional<Cliente> buscarPorId(Integer id) {
     if (id == null) return Optional.empty();
     String sql = "SELECT id, nombre, email, telefono FROM Clientes WHERE id=?";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.setInt(1, id);
         return consultarUno(ps);
     } catch (SQLException | RuntimeException e) {
         registrar("buscar cliente por id", e);
         return Optional.empty();
     }
 }

 @Override
 public int insertar(Cliente entidad) {
     if (entidad == null){
         throw new IllegalArgumentException("La entidad no puede ser nula");
     }
     String query = "INSERT INTO Clientes (nombre, email, telefono) VALUES (?, ?, ?)";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(query)) {
         ps.setString(1, entidad.getNombre());
         ps.setString(2, entidad.getEmail());
         ps.setString(3, entidad.getTelefono());
         int filas = ps.executeUpdate();

         if (filas == 0){
             return 0;
         }
         try (ResultSet rs = ps.getGeneratedKeys()){
             return rs.next() ? rs.getInt(1):0;
         }
     } catch (SQLException e) {
         registrar("insertar cliente", e);
         throw new DaoException("Error al insertar el cliente", e);
     }
 }

 @Override
 public int actualizar(Cliente entidad) {
     if (entidad == null || entidad.getId() == null ) 
         throw new IllegalArgumentException("debes tener id");
     String sql = " UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ? ";

     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(sql)) {
         ps.setString(1, entidad.getNombre());
         ps.setString(2, entidad.getEmail());
         ps.setString(3, entidad.getTelefono());
         ps.setInt(4, entidad.getId());
         return ejecutar(ps);
     } catch (SQLException e) {
         registrar("actualizar cliente", e);
         throw new IllegalArgumentException("error al actualizar", e);
     }
 }

 @Override
 public int eliminar(Integer id) {
     if (id == null) {
         throw new NullPointerException();
     }
     String sql = "DELETE FROM Clientes WHERE id = ?";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.setInt(1, id);
         return ejecutar(ps);
     } catch (SQLException e) {
         registrar("Eliminar cliente", e);
         throw new NullPointerException();
     }
 }

    @Override
    protected Optional<Cliente> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Cliente(rs.getInt("id"), rs.getString("nombre"),
                    rs.getString("email"), rs.getString("telefono")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
