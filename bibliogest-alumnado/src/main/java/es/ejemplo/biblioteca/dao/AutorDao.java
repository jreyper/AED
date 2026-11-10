package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Autor;
import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AutorDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(AutorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public AutorDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public AutorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no pudo terminar: " + ex.getMessage());
    }

    public List<Autor> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select a from AutorEntity a order by a.nombre", AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Autor> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();

        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(AutorEntity.class, id)).map(AutorMapper::toDomain);
        } catch (Exception e) {
            registrar("listar", e);
            return Optional.empty();
        }
    }

    public int insertar(Autor dato) {
        if (!validar(dato)) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                validar(dato);
                AutorEntity entidad = AutorMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                dato.setIdAutor(entidad.getIdAutor());
                ;
                tx.commit();
                return entidad.getIdAutor();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int actualizar(Autor dato) {
        if (!validar(dato) || dato.getIdAutor() == null) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity a = em.find(AutorEntity.class, dato.getIdAutor());
                if (a == null) {
                    tx.commit();
                    return 0;
                }
                AutorEntity AutorEntityActualizado = AutorMapper.toEntity(dato);
                a.setNombre(AutorEntityActualizado.getNombre());
                a.setNacionalidad(AutorEntityActualizado.getNacionalidad());
                a.setAnioNacimiento(AutorEntityActualizado.getAnioNacimiento());
                tx.commit();
                return 1;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null || id < 1) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity a = em.find(AutorEntity.class, id);
                if (a == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(a);
                tx.commit();
                return 1;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    private boolean validar(Autor dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank();
    }

    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        if (nacionalidad == null) return new ArrayList<>();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select a from AutorEntity a where a.nacionalidad = :nacionalidad", AutorEntity.class)
                    .setParameter("nacionalidad", nacionalidad)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public long contarLibrosPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            long totalLibros = em.createQuery("select count(l) from LibroEntity l where idAutor = :idAutor", Long.class)
                    .setParameter("idAutor", idAutor)
                    .getFirstResult();
            return totalLibros;
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public List<Object[]> autoresConRecuento() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select a.nombre, count(l) from AutorEntity a left join LibroEntity l on l.idAutor = a.idAutor group by a.idAutor, a.nombre order by a.nombre", Object[].class)
                    .getResultList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Autor> autoresLeidosPorTresSocios() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select a from PrestamoEntity p join p.libro l join l.autor a group by a having count(distinct p.idSocio) >= 3 order by a.nombre", AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) managerFactory.close();
    }
}