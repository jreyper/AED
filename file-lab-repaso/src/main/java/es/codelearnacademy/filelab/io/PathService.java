package es.codelearnacademy.filelab.io;

import java.nio.file.Path;

public class PathService {

    public Path crear(String primero, String... partes) {
        if (primero == null || primero.isBlank()){
            throw new IllegalArgumentException("La primera parte de la ruta no puede ser nula");
        }
        return Path.of(primero, partes);
    }

    public String nombre(Path path) {
        if (path == null){
            throw new IllegalArgumentException("El path no puede ser nulo");
        }
        Path nombre = path.getFileName();
        if (nombre == null){
            throw new IllegalArgumentException();
        }
        return nombre.toString();
    }

    public Path padre(Path path) {
        if (path == null){
            throw new IllegalArgumentException("el path del padre no puede ser nulo");
        }
        return path.getParent();
    }

    public Path absoluto(Path path) {
        if (path == null){
            throw new IllegalArgumentException("No puede ser el path nulo");
        }
        return path.toAbsolutePath();
    }

    public Path normalizar(Path path) {
        if (path == null){
            throw new IllegalArgumentException("El path no puede ser nulo");
        }
        return path.normalize();
    }

    public boolean esAbsoluto(Path path) {
        if (path == null){
            throw new IllegalArgumentException("El path no puede ser nulo");
        }
        return path.isAbsolute();
    }

    public Path resolver(Path base, String otro) {
        if (base == null || otro == null || otro.isBlank()){
            throw new IllegalArgumentException("ni la base ni la ruta puede ser nula");
        }
        return base.resolve(otro);
    }

    public Path relativizar(Path base, Path destino) {
        if (base == null || destino == null){
            throw new IllegalArgumentException("La base y el destino no puede ser nulo");
        }
        return base.relativize(destino);
    }

    public String extension(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("El path no puede ser nulo");
        }
        String documento = nombre(path);
        int punto = documento.lastIndexOf('.');
        if (punto == -1) {
            return "";
        }
        return documento.substring(punto + 1);
    }
}
