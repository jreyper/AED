package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Reserva;

import es.ejemplo.biblioteca.orm.mapper.ReservaMapper;
import es.ejemplo.biblioteca.orm.entity.ReservaEntity;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReservaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ReservaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public ReservaDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public ReservaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no pudo terminar: " + ex.getMessage());
    }

    public List<Reserva> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r order by r.idReserva", ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Reserva> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();

        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ReservaEntity.class, id)).map(ReservaMapper::toDomain);
        } catch (Exception e) {
            registrar("listar", e);
            return Optional.empty();
        }
    }

    public int insertar(Reserva dato) {
        if (!validar(dato)) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                validar(dato);
                ReservaEntity entidad = ReservaMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                dato.setIdReserva(entidad.getIdReserva());
                tx.commit();
                return entidad.getIdReserva();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int actualizar(Reserva dato) {
        if (!validar(dato) || dato.getIdReserva() == null) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ReservaEntity a = em.find(ReservaEntity.class, dato.getIdReserva());
                if (a == null) {
                    tx.commit();
                    return 0;
                }
                ReservaEntity ReservaEntityActualizado = ReservaMapper.toEntity(dato);
                a.setIdSocio(ReservaEntityActualizado.getIdSocio());
                a.setIdLibro(ReservaEntityActualizado.getIdLibro());
                a.setFechaReserva(ReservaEntityActualizado.getFechaReserva());
                a.setEstado(ReservaEntityActualizado.getEstado());
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
                ReservaEntity a = em.find(ReservaEntity.class, id);
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

    private boolean validar(Reserva dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null && dato.getFechaReserva() != null && dato.getEstado() != null && (dato.getEstado().equals("PENDIENTE") || dato.getEstado().equals("ATENDIDA") || dato.getEstado().equals("CANCELADA"));
    }

    public List<Reserva> listarPendientes() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r where estado = 'PENDIENTE'", ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Reserva> buscarPorSocio(int idSocio) {
        if (idSocio < 1) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r where idSocio = :idSocioAsociado", ReservaEntity.class)
                    .setParameter("idSocioAsociado", idSocio)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public List<Reserva> listarPorLibro(int idLibro) {
        if (idLibro < 1) return List.of();

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r where idLibro = :idLibroReservados", ReservaEntity.class)
                    .setParameter("idLibroReservados", idLibro)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public long contarReservasPendientes() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select count(r) from ReservaEntity r where estado = 'PENDIENTE'", Long.class)
                    .getSingleResult();
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public List<Object[]> reservasConDetalles() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> prioridadReservas() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }


    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) managerFactory.close();
    }
}