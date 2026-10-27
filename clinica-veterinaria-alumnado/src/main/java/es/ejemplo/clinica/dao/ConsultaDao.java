package es.ejemplo.clinica.dao;
import es.ejemplo.clinica.modelo.Cliente;
import es.ejemplo.clinica.modelo.Consulta;
import java.sql.*;
import java.util.*;


public final class ConsultaDao extends AbstractDao<Consulta> implements IConsultaDao {
    @Override
    protected Optional<Consulta> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Consulta( rs.getInt("id"),rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
            rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado")
            ));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass consulta", e);
            return Optional.empty();
        }
    }

    public ConsultaDao(String url) {
     super(url);
  }

 @Override
 public List<Consulta> listar() {
     String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado FROM consultas";
     try (Connection con = obtenerConexion() ;
          PreparedStatement ps = con.prepareStatement(sql)) {
         return consultar(ps);
     } catch (SQLException | RuntimeException e) {
         registrar("listar consultas", e);
         return List.of();
     }
 }

 @Override
 public Optional<Consulta> buscarPorId(Integer id) {
     if (id == null) return Optional.empty();
     String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado FROM consultas WHERE id=?";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.setInt(1, id);
         return consultarUno(ps);
     } catch (SQLException | RuntimeException e) {
         registrar("buscar consulta por id", e);
         return Optional.empty();
     }
 }

 @Override
 public int insertar(Consulta entidad) {
     String query = "INSERT INTO consultas (id_mascota,id_veterinario,fecha,horas,estado) VALUES (?, ?, ?, ?, ?)";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, entidad.getIdMascota());
         ps.setInt(2, entidad.getIdVeterinario());
         ps.setString(3, entidad.getFecha());
         ps.setBigDecimal(4, entidad.getHoras());
         ps.setString(5, entidad.getEstado());
         int filas = ps.executeUpdate();

         if (filas == 0){
             return 0;
         }
         try (ResultSet rs = ps.getGeneratedKeys()){
             return rs.next() ? rs.getInt(1):0;
         }
     } catch (SQLException e) {
         registrar("insertar consulta", e);
         throw new DaoException("Error al insertar la consulta", e);
     }
 }

 @Override
 public int actualizar(Consulta entidad) {
     if (entidad == null || entidad.getId() == null ) throw new IllegalArgumentException("debes tener id");
     String sql = " UPDATE consultas SET id_mascota = ?, id_veterinario = ?, fecha = ?, horas = ?, estado = ? WHERE id = ? ";

     try (Connection con = obtenerConexion();
          PreparedStatement ps = con.prepareStatement(sql)) {
         ps.setInt(1, entidad.getIdMascota());
         ps.setInt(2, entidad.getIdVeterinario());
         ps.setString(3, entidad.getFecha());
         ps.setBigDecimal(4, entidad.getHoras());
         ps.setString(5, entidad.getEstado());
         ps.setInt(6, entidad.getId());
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
     String sql = "DELETE FROM consultas WHERE id = ?";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.setInt(1, id);
         return ejecutar(ps);
     } catch (SQLException e) {
         registrar("Eliminar consultas", e);
         throw new NullPointerException();
     }
 }

 @Override
 public List<Consulta> buscarPorMascota(Integer idMascota) {
     if (idMascota == null) {
         return List.of();
     }

     String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado FROM consultas WHERE id_mascota=?";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.setInt(1, idMascota);
         return consultar(ps);
     } catch (SQLException e) {
         registrar("Buscar por id de mascota", e);
         return List.of();
     }
 }

 @Override
 public List<Consulta> buscarPorVeterinario(Integer idVeterinario) {
     if (idVeterinario == null) {
         return List.of();
     }

     String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado FROM consultas WHERE id_veterinario=?";
     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {
         ps.setInt(1, idVeterinario);
         return consultar(ps);
     } catch (SQLException e) {
         registrar("Buscar por id de veterinario", e);
         return List.of();
     }
 }

 @Override
 public List<Consulta> listarProgramadas() {
     String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas WHERE estado = ?";

     try (Connection connection = obtenerConexion();
          PreparedStatement ps = connection.prepareStatement(sql)) {

         ps.setString(1, "PROGRAMADA");
         return consultar(ps);

     } catch (SQLException | RuntimeException e) {
         registrar("listar consultas programadas", e);
         return List.of();
     }
 }
 
}
