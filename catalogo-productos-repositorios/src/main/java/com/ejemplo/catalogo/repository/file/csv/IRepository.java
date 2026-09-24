package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IRepository {
    /**
     * Funcion que obtiene todos los productos
     * @return lista productos
     */
    List<Producto> findAll();

    /**
     * Funcion que obtiene el elemento
     * @param id identificador del producto
     * @return optional del producto
     */
    Optional<Producto> findById(long id);

    /**
     * Crea un producto
     * @param entity
     */
    void create(Producto entity);

    /**
     * actualiza un producto
     * @param entity
     */
    boolean update(Producto entity);

    /**
     * elimina un producto
     * @param id
     */
    boolean delete(long id);
}
