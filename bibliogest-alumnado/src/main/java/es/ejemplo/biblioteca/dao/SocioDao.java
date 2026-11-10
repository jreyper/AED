package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Socio;

import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;
import es.ejemplo.biblioteca.orm.mapper.SocioMapper;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SocioDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(SocioDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SocioDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}

    public SocioDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no pudo terminar: "+ex.getMessage());}

    public List<Socio> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s order by s.nombre", SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Socio> buscarPorId(Integer id) {
        if(id==null) return Optional.empty();

        try (var em = managerFactory.createEntityManager()){
            return Optional.ofNullable(em.find(SocioEntity.class, id)).map(SocioMapper::toDomain);
        } catch (Exception e) {
            registrar("listar", e);
            return Optional.empty();
        }
    }

    public int insertar(Socio dato) {
        if (!validar(dato)) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                validar(dato);
                SocioEntity entidad = SocioMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                dato.setIdSocio(entidad.getIdSocio());;
                tx.commit();
                return entidad.getIdSocio();
            } catch (Exception e) {
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int actualizar(Socio dato) {
        if (!validar(dato) || dato.getIdSocio() == null) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity a = em.find(SocioEntity.class, dato.getIdSocio());
                if(a==null) {
                    tx.commit();
                    return 0;
                }
                SocioEntity SocioEntityActualizado = SocioMapper.toEntity(dato);
                a.setNombre(SocioEntityActualizado.getNombre());
                a.setEmail(SocioEntityActualizado.getEmail());
                a.setTelefono(SocioEntityActualizado.getTelefono());
                a.setFechaAlta(SocioEntityActualizado.getFechaAlta());
                a.setActivo(SocioEntityActualizado.getActivo());
                tx.commit();
                return 1;
            } catch (Exception e) {
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if(id==null||id<1) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity a = em.find(SocioEntity.class, id);
                if (a == null) {
                    tx.rollback();
                    return 0;
                }
                PrestamoEntity presEnt = em.createQuery("select p from PrestamoEntity p where p.idSocio = :id", PrestamoEntity.class)
                        .setParameter("id", id)
                        .getSingleResult();
                if(presEnt!=null) {
                    return 0;
                }
                em.remove(a);
                tx.commit();
                return 1;
            } catch (Exception e) {
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            registrar("listar", e);
            return 0;
        }
    }
    private boolean validar(Socio dato) {return dato != null && dato.getNombre()!=null && !dato.getNombre().isBlank() && dato.getEmail()!=null && !dato.getEmail().isBlank() && dato.getFechaAlta()!=null && dato.getActivo()!=null;}

    public List<Socio> listarActivos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s where s.activo = true order by s.fechaAlta desc", SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Socio> buscarPorEmail(String email) {
        if(email==null || email.isEmpty()) return Optional.empty();

        try (var em = managerFactory.createEntityManager()){
            return Optional.ofNullable(em.createQuery("select s from SocioEntity s where email = :emailBuscar", SocioEntity.class)
                            .setParameter("emailBuscar", email)
                            .getSingleResult())
                    .map(SocioMapper::toDomain);
        } catch (Exception e) {
            registrar("listar", e);
            return Optional.empty();
        }
    }

    public List<Socio> listarConPrestamosVencidos(String fecha) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Socio> sociosSinPrestamos() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> rankingSocios(int limite) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    public List<Object[]> historialSocio(int idSocio) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }



    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}