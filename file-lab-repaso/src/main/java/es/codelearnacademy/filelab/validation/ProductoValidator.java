package es.codelearnacademy.filelab.validation;

import es.codelearnacademy.filelab.model.Producto;

public final class ProductoValidator {

    private ProductoValidator() {
    }

    public static void validar(Producto producto) {
        if (producto == null){
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (producto.id() <= 0){
            throw new IllegalArgumentException("El id no puede ser menor o igual que 0");
        }
        if (producto.nombre() == null || producto.nombre().isBlank()){
            throw new IllegalArgumentException("El nombre no puede estar vacio ni ser nulo");
        }
        if (producto.precio() < 0){
            throw new IllegalArgumentException("El precio no puede ser menor que 0");
        }
        if (producto.stock() < 0){
            throw new IllegalArgumentException("El stock no puerde ser menor que 0");
        }
    }
}
