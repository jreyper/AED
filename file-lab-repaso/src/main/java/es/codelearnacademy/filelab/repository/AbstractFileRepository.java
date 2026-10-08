package es.codelearnacademy.filelab.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository<T, ID> implements IRepository<T, ID> {


    protected abstract ID getId(T entity);

    protected abstract List<T> readAll() throws IOException;

    protected abstract void writeAll(List<T> entities) throws IOException;

    @Override
    public List<T> findAll() {
        try {
            return readAll();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null){
            return Optional.empty();
        }
        try {
            List<T> entidades = readAll();
            for (T entity : entidades){
                if (getId(entity).equals(id)){
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean create(T entity) {
        if (entity == null){
            return false;
        }
        try {
            List<T> entidades = new ArrayList<>(readAll());
            for (T entidad : entidades){
                if (getId(entidad).equals(getId(entity))){
                    return false;
                }
            }
            entidades.add(entity);
            writeAll(entidades);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(T entity) {
        if (entity == null){
            return false;
        }
        try {
            List<T> entidades = new ArrayList<>(readAll());
            for (int i = 0; i < entidades.size(); i++) {
                if (getId(entidades.get(i)).equals(getId(entity))){
                    entidades.set(i,entity);
                    writeAll(entidades);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(ID id) {
        if (id == null){
            return false;
        }
        try {
            List<T> entidades = new ArrayList<>(readAll());
            for (int i = 0; i < entidades.size(); i++) {
                if (getId(entidades.get(i)).equals(id)){
                    entidades.remove(i);
                    writeAll(entidades);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
