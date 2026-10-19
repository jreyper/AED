package com.ejemplo.catalogo.repository.database;

import com.ejemplo.catalogo.model.Producto;
import com.ejemplo.catalogo.repository.file.csv.IRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbstractJdbcRepository <T, ID> extends DatabaseInitializer implements IRepository<T, ID> {
    
    public AbstractJdbcRepository(String url){
        super(url);
    }
    
    @Override
    public List<Producto> findAll2(){
        String sql = "SELECT id, nombre, precio FROM producto ORDER BY id";
        List<Producto> productos = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productos;
    }


    public boolean create2(Producto producto){
        String sql = "INSERT INTO producto(id, nombre, precio) VALUES (?, ?, ?)";

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, producto.id());
            ps.setString(2, producto.nombre());
            ps.setDouble(3, producto.precio());
            return ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean update(Producto producto){
        String sql = "UPDATE producto SET nombre = ?, precio = ? WHERE id = ?";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, producto.nombre());
            ps.setDouble(2, producto.precio());
            ps.setLong(3, producto.id());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Producto producto){
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, producto.id());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<T> readById(ID id){
        String sql = "SELECT id, nombre, precio FROM producto WHERE id = ?";
        List<Producto> productos = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setLong(1, producto.id());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        /**
         if (productos.size() > 0){
         return productos.getFirst();
         }
         return null;
         **/
        //Esto es lo mismo que el ultimo if pero tiene q ser optional no producto
        return productos.stream().findFirst();


    }
}
