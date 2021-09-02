package org.vaadin.example.repositories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;

/**
 *
 * Para la implementación de estas clase Repositorios nos basamos en este vídeo donde lo explica detallamadamente:
 * https://youtu.be/yYukELtL4mM
 *
 * Para solucionar de que actualice siempre aunque el ID no exista vamos a seguir las indicaciones del siguiente vídeo (minuto 25:35)
 * https://youtu.be/osdl2--KRyc
 *
 */
public class JuegoJpaRepository implements Serializable {

    //Creamos constructor vacío
    public JuegoJpaRepository() {
    }

    public JuegoJpaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Añadimos nuestro objeto relacionado con la persistencia creada
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia-vaadin-mysql");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Juego juego) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Distribuye distribuidor = juego.getDistribuidor();
            if (distribuidor != null) {
                distribuidor = em.getReference(distribuidor.getClass(), distribuidor.getIdDistribuidor());
                juego.setDistribuidor(distribuidor);
            }
            Usuario usuario = juego.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                juego.setUsuario(usuario);
            }
            em.persist(juego);
            if (distribuidor != null) {
                distribuidor.getJuegoList().add(juego);
                distribuidor = em.merge(distribuidor);
            }
            if (usuario != null) {
                usuario.getJuegoList().add(juego);
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Juego juego) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            //añadimos las columnas de la parte inferior
            Integer id = juego.getIdJuego();
            if (findJuego(id) == null) {
                throw new NonexistentEntityException("The juego with id " + id + " no longer exists.");
            } else { //añadimos y creamos la condición si existe el ID
                Juego persistentJuego = em.find(Juego.class, juego.getIdJuego());
                Distribuye distribuidorOld = persistentJuego.getDistribuidor();
                Distribuye distribuidorNew = juego.getDistribuidor();
                Usuario usuarioOld = persistentJuego.getUsuario();
                Usuario usuarioNew = juego.getUsuario();
                if (distribuidorNew != null) {
                    distribuidorNew = em.getReference(distribuidorNew.getClass(), distribuidorNew.getIdDistribuidor());
                    juego.setDistribuidor(distribuidorNew);
                }
                if (usuarioNew != null) {
                    usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                    juego.setUsuario(usuarioNew);
                }
                juego = em.merge(juego);
                if (distribuidorOld != null && !distribuidorOld.equals(distribuidorNew)) {
                    distribuidorOld.getJuegoList().remove(juego);
                    distribuidorOld = em.merge(distribuidorOld);
                }
                if (distribuidorNew != null && !distribuidorNew.equals(distribuidorOld)) {
                    distribuidorNew.getJuegoList().add(juego);
                    distribuidorNew = em.merge(distribuidorNew);
                }
                if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                    usuarioOld.getJuegoList().remove(juego);
                    usuarioOld = em.merge(usuarioOld);
                }
                if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                    usuarioNew.getJuegoList().add(juego);
                    usuarioNew = em.merge(usuarioNew);
                }
                em.getTransaction().commit();
            } //cerramos la condición añadida
        } catch (Exception ex) {
            //  String msg = ex.getLocalizedMessage();
            //  if (msg == null || msg.length() == 0) {
            //
            // Estas líneas de abajo las pasamos arriba
            //      Integer id = juego.getIdJuego();
            //      if (findJuego(id) == null) {
            //          throw new NonexistentEntityException("The juego with id " + id + " no longer exists.");
            //      }
            //  }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Juego juego;
            try {
                juego = em.getReference(Juego.class, id);
                juego.getIdJuego();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The juego with id " + id + " no longer exists.", enfe);
            }
            Distribuye distribuidor = juego.getDistribuidor();
            if (distribuidor != null) {
                distribuidor.getJuegoList().remove(juego);
                distribuidor = em.merge(distribuidor);
            }
            Usuario usuario = juego.getUsuario();
            if (usuario != null) {
                usuario.getJuegoList().remove(juego);
                usuario = em.merge(usuario);
            }
            em.remove(juego);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Juego> findJuegoEntities() {
        return findJuegoEntities(true, -1, -1);
    }

    public List<Juego> findJuegoEntities(int maxResults, int firstResult) {
        return findJuegoEntities(false, maxResults, firstResult);
    }

    private List<Juego> findJuegoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Juego.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Juego findJuego(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Juego.class, id);
        } finally {
            em.close();
        }
    }

    public int getJuegoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Juego> rt = cq.from(Juego.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Juego> ListJuegoByFilter(String filtra) {
        filtra = filtra.toUpperCase();

        String QUERY = "SELECT j FROM Juego j WHERE j.titulo LIKE :titulo OR j.sistemaOperativo LIKE :sistemaOperativo"
                + " OR j.fechaJuego LIKE :fechaJuego OR j.precio LIKE :precio";

        EntityManager em = getEntityManager();
        List<Juego> juegotmp = new ArrayList<>();

        try {

            TypedQuery<Juego> consulta = em.createQuery(QUERY, Juego.class); //preparamos la consulta QUERY a realizar
            consulta.setParameter("titulo", "%" + filtra + "%");    //indico el campo y la cadena a buscar 
            consulta.setParameter("sistemaOperativo", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            consulta.setParameter("fechaJuego", "%" + filtra + "%");     //indico el campo y la cadena a buscar 
            consulta.setParameter("precio", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            juegotmp = consulta.getResultList();  //guardo la consulta realiza en un objeto de tipo Usuario

        } catch (Exception e) {
        } finally {
            em.close();
        }
        return juegotmp;
    }

    public List<Juego> ListJuegoByFilterPagination(String filtra, boolean all, int maxResults, int pagina) {
        int numPagina;
        filtra = filtra.toUpperCase();

        String QUERY = "SELECT j FROM Juego j WHERE j.titulo LIKE :titulo OR j.sistemaOperativo LIKE :sistemaOperativo"
                + " OR j.fechaJuego LIKE :fechaJuego OR j.precio LIKE :precio";

        EntityManager em = getEntityManager();
        List<Juego> juegotmp = new ArrayList<>();

        try {

            TypedQuery<Juego> consulta = em.createQuery(QUERY, Juego.class); //preparamos la consulta QUERY a realizar
            consulta.setParameter("titulo", "%" + filtra + "%");    //indico el campo y la cadena a buscar 
            consulta.setParameter("sistemaOperativo", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            consulta.setParameter("fechaJuego", "%" + filtra + "%");     //indico el campo y la cadena a buscar 
            consulta.setParameter("precio", "%" + filtra + "%"); //indico el campo y la cadena a buscar 

            //calculamos el número de la página, comenzando desde la 0
            numPagina = pagina * maxResults;

            if (!all) {
                consulta.setMaxResults(maxResults);
                consulta.setFirstResult(numPagina);
            }

            juegotmp = consulta.getResultList();  //guardo la consulta realiza en un objeto de tipo Usuario

        } catch (Exception e) {
        } finally {
            em.close();
        }

        return juegotmp;
    }

}
