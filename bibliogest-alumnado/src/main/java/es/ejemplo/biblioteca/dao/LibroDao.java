package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Libro;

import es.ejemplo.biblioteca.orm.mapper.LibroMapper;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LibroDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(LibroDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public LibroDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public LibroDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no pudo terminar: " + ex.getMessage());
    }

    public List<Libro> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l order by l.titulo", LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Libro> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();

        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(LibroEntity.class, id)).map(LibroMapper::toDomain);
        } catch (Exception e) {
            registrar("listar", e);
            return Optional.empty();
        }
    }

    public int insertar(Libro dato) {
        if (!validar(dato)) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                validar(dato);
                LibroEntity entidad = LibroMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                dato.setIdLibro(entidad.getIdLibro());
                ;
                tx.commit();
                return entidad.getIdLibro();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int actualizar(Libro dato) {
        if (!validar(dato) || dato.getIdLibro() == null) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity a = em.find(LibroEntity.class, dato.getIdLibro());
                if (a == null) {
                    tx.commit();
                    return 0;
                }
                LibroEntity LibroEntityActualizado = LibroMapper.toEntity(dato);
                a.setTitulo(LibroEntityActualizado.getTitulo());
                a.setIsbn(LibroEntityActualizado.getIsbn());
                a.setGenero(LibroEntityActualizado.getGenero());
                a.setAnioPublicacion(LibroEntityActualizado.getAnioPublicacion());
                a.setEjemplares(LibroEntityActualizado.getEjemplares());
                a.setIdAutor(LibroEntityActualizado.getIdAutor());
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
                LibroEntity a = em.find(LibroEntity.class, id);
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

    private boolean validar(Libro dato) {
        return dato != null && dato.getTitulo() != null && !dato.getTitulo().isBlank() && dato.getIsbn() != null && !dato.getIsbn().isBlank() && dato.getGenero() != null && dato.getIdAutor() != null && dato.getEjemplares() != null && dato.getEjemplares() >= 0;
    }

    public List<Libro> buscarPorGenero(String genero) {
        if (genero == null || genero.isBlank()) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where genero = :genero", LibroEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Libro> buscarPorAutor(int idAutor) {
        if (idAutor < 0) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where l.idAutor = :idAutor", LibroEntity.class)
                    .setParameter("idAutor", idAutor)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Libro> buscarPorTitulo(String texto) {
        if (texto == null || texto.isBlank()) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where titulo like concat('%', :textoLike, '%')", LibroEntity.class)
                    .setParameter("textoLike", texto)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Libro> listarDisponibles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT l FROM LibroEntity l",
                            LibroEntity.class
                    ).getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();

        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Libro> librosDesdeAnio(int anio, String genero) {
        if (genero == null || genero.isBlank()) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where anioPublicacion >= :anio and genero = :genero", LibroEntity.class)
                    .setParameter("anio", anio)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<String> generos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select distinct(l.genero) from LibroEntity l", String.class)
                    .getResultStream()
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Libro> nuncaPrestados() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Libro> masPrestadosQueMedia() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Libro> reservadosSinDisponibilidad() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> librosConAutor() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                    "select l.titulo, l.idAutor from LibroEntity l order by l.titulo",
                    Object[].class
            ).getResultList();

        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Object[]> contarPorGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select count(distinct(l.genero)) from LibroEntity l group by l.genero", Object[].class)
                    .getResultStream()
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Object[]> prestamosPorGeneroMinimo(long minimo) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> disponibilidadPorLibro() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                    "select l.titulo, l.ejemplares from LibroEntity l order by l.titulo",
                    Object[].class
            ).getResultList();

        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Object[]> estadisticasGenero() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) managerFactory.close();
    }
}