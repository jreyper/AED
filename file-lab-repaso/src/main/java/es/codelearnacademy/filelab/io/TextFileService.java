package es.codelearnacademy.filelab.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TextFileService {

    public boolean escribir(Path path, String contenido) {
        if (path == null || contenido == null || contenido.isBlank()){
            throw new NullPointerException("El path y el contenido no pueden ser nulos");
        }
        try {
            Files.writeString(path,contenido, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String leer(Path path) {
        if (path == null){
            throw new NullPointerException("El path no puede ser nulo");
        }
        try {
            return Files.readString(path,StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public boolean escribirLineas(Path path, List<String> lineas) {
        if (path == null || lineas == null){
            throw new NullPointerException("El path y la lista no pueden ser nulos");
        }
        try {
            Files.write(path, lineas, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> leerLineas(Path path) {
        if (path == null) {
            throw new NullPointerException("El path no puede ser nulo");
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return List.of();
        }
    }

    public boolean anexar(Path path, String contenido) {
        if (path == null || contenido == null || contenido.isBlank()){
            throw new NullPointerException("El path no puede ser nulo");
        }
        try {
            Files.writeString(path, contenido, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
