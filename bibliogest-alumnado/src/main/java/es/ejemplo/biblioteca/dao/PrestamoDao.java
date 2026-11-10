package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Prestamo;

import es.ejemplo.biblioteca.orm.mapper.PrestamoMapper;
import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PrestamoDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(PrestamoDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public PrestamoDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public PrestamoDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no pudo terminar: " + ex.getMessage());
    }

    public List<Prestamo> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p from PrestamoEntity p order by p.idPrestamo", PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Prestamo> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();

        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(PrestamoEntity.class, id)).map(PrestamoMapper::toDomain);
        } catch (Exception e) {
            registrar("listar", e);
            return Optional.empty();
        }
    }

    public int insertar(Prestamo dato) {
        if (!validar(dato)) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                validar(dato);
                PrestamoEntity entidad = PrestamoMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                dato.setIdPrestamo(entidad.getIdPrestamo());
                ;
                tx.commit();
                return entidad.getIdPrestamo();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int actualizar(Prestamo dato) {
        if (!validar(dato) || dato.getIdPrestamo() == null) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PrestamoEntity a = em.find(PrestamoEntity.class, dato.getIdPrestamo());
                if (a == null) {
                    tx.commit();
                    return 0;
                }
                PrestamoEntity PrestamoEntityActualizado = PrestamoMapper.toEntity(dato);
                a.setIdSocio(PrestamoEntityActualizado.getIdSocio());
                a.setIdLibro(PrestamoEntityActualizado.getIdLibro());
                a.setFechaPrestamo(PrestamoEntityActualizado.getFechaPrestamo());
                a.setFechaLimite(PrestamoEntityActualizado.getFechaLimite());
                a.setFechaDevolucion(PrestamoEntityActualizado.getFechaDevolucion());
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
                PrestamoEntity a = em.find(PrestamoEntity.class, id);
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

    private boolean validar(Prestamo dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null && dato.getFechaPrestamo() != null && dato.getFechaLimite() != null && dato.getFechaLimite().compareTo(dato.getFechaPrestamo()) >= 0 && (dato.getFechaDevolucion() == null || dato.getFechaDevolucion().compareTo(dato.getFechaPrestamo()) >= 0);
    }

    public List<Prestamo> listarPorSocio(int idSocio) {
        if (idSocio < 1) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PrestamoEntity p where p.idSocio = :idSocio order by p.idPrestamo",
                            PrestamoEntity.class
                    )
                    .setParameter("idSocio", idSocio)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Prestamo> listarActivos() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Prestamo> listarVencidos(String fecha) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public long contarPrestamosPorLibro(int idLibro) {
        if (idLibro < 1) return 0;

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select count(p) from prestamos p where p.idLibro = :idLibro", Long.class)
                    .setParameter("idLibro", idLibro)
                    .getSingleResult();
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public List<Object[]> prestamosConSocioYLibro() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                    "select p, s, l from PrestamoEntity p, SocioEntity s, LibroEntity l where p.idSocio = s.idSocio and p.idLibro = l.idLibro order by p.idPrestamo",
                    Object[].class
            ).getResultList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Object[]> rankingMensual(String anioMes, int limite) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }


    public int registrarPrestamo(int idSocio, int idLibro, String inicio, String limite) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }

    public int devolver(int id, String fecha) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return 0;
    }


    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) managerFactory.close();
    }
}