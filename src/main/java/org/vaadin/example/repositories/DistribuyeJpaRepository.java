package org.vaadin.example.repositories;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vaadin.example.entities.Juego;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;
import org.vaadin.example.repositories.exceptions.PreexistingEntityException;

/**
 *
 * Para la implementación de estas clase Repositorios nos basamos en este vídeo donde lo explica detallamadamente:
 * https://youtu.be/yYukELtL4mM
 *
 * Para solucionar de que actualice siempre aunque el ID no exista vamos a seguir las indicaciones del siguiente vídeo (minuto 25:35)
 * https://youtu.be/osdl2--KRyc
 *
 */
public class DistribuyeJpaRepository implements Serializable {

    //Creamos constructor vacío
    public DistribuyeJpaRepository() {
    }

    public DistribuyeJpaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Añadimos nuestro objeto relacionado con la persistencia creada
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia-vaadin-mysql");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Distribuye distribuye) throws PreexistingEntityException, Exception {
        if (distribuye.getJuegoList() == null) {
            distribuye.setJuegoList(new ArrayList<Juego>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Juego> attachedJuegoList = new ArrayList<Juego>();
            for (Juego juegoListJuegoToAttach : distribuye.getJuegoList()) {
                juegoListJuegoToAttach = em.getReference(juegoListJuegoToAttach.getClass(), juegoListJuegoToAttach.getIdJuego());
                attachedJuegoList.add(juegoListJuegoToAttach);
            }
            distribuye.setJuegoList(attachedJuegoList);
            em.persist(distribuye);
            for (Juego juegoListJuego : distribuye.getJuegoList()) {
                Distribuye oldDistribuidorOfJuegoListJuego = juegoListJuego.getDistribuidor();
                juegoListJuego.setDistribuidor(distribuye);
                juegoListJuego = em.merge(juegoListJuego);
                if (oldDistribuidorOfJuegoListJuego != null) {
                    oldDistribuidorOfJuegoListJuego.getJuegoList().remove(juegoListJuego);
                    oldDistribuidorOfJuegoListJuego = em.merge(oldDistribuidorOfJuegoListJuego);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDistribuye(distribuye.getIdDistribuidor()) != null) {
                throw new PreexistingEntityException("Distribuye " + distribuye + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Distribuye distribuye) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            //añadimos las columnas de la parte inferior
            String id = distribuye.getIdDistribuidor();
            if (findDistribuye(id) == null) {
                throw new NonexistentEntityException("El id " + id + " no existe.");
            } else { //añadimos y creamos la condición si existe el ID

                Distribuye persistentDistribuye = em.find(Distribuye.class, distribuye.getIdDistribuidor());
                List<Juego> juegoListOld = persistentDistribuye.getJuegoList();
                List<Juego> juegoListNew = distribuye.getJuegoList();
                List<String> illegalOrphanMessages = null;
                for (Juego juegoListOldJuego : juegoListOld) {
                    if (!juegoListNew.contains(juegoListOldJuego)) {
                        if (illegalOrphanMessages == null) {
                            illegalOrphanMessages = new ArrayList<String>();
                        }
                        illegalOrphanMessages.add("Existe un conflicto con la tabla Juego " + juegoListOldJuego + " que está haciendo referencia a este registro.");
                    }
                }
                if (illegalOrphanMessages != null) {
                    throw new IllegalOrphanException(illegalOrphanMessages);
                }
                List<Juego> attachedJuegoListNew = new ArrayList<Juego>();
                for (Juego juegoListNewJuegoToAttach : juegoListNew) {
                    juegoListNewJuegoToAttach = em.getReference(juegoListNewJuegoToAttach.getClass(), juegoListNewJuegoToAttach.getIdJuego());
                    attachedJuegoListNew.add(juegoListNewJuegoToAttach);
                }
                juegoListNew = attachedJuegoListNew;
                distribuye.setJuegoList(juegoListNew);
                distribuye = em.merge(distribuye);
                for (Juego juegoListNewJuego : juegoListNew) {
                    if (!juegoListOld.contains(juegoListNewJuego)) {
                        Distribuye oldDistribuidorOfJuegoListNewJuego = juegoListNewJuego.getDistribuidor();
                        juegoListNewJuego.setDistribuidor(distribuye);
                        juegoListNewJuego = em.merge(juegoListNewJuego);
                        if (oldDistribuidorOfJuegoListNewJuego != null && !oldDistribuidorOfJuegoListNewJuego.equals(distribuye)) {
                            oldDistribuidorOfJuegoListNewJuego.getJuegoList().remove(juegoListNewJuego);
                            oldDistribuidorOfJuegoListNewJuego = em.merge(oldDistribuidorOfJuegoListNewJuego);
                        }
                    }
                }
                em.getTransaction().commit();
            } //cerramos la condición añadida
        } catch (Exception ex) {
            // String msg = ex.getLocalizedMessage();
            // if (msg == null || msg.length() == 0) {

            // Estas líneas de abajo las pasamos arriba
            //    String id = distribuye.getIdDistribuidor();
            //    if (findDistribuye(id) == null) {
            //        throw new NonexistentEntityException("The distribuye with id " + id + " no longer exists.");
            //    }
            // }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Distribuye distribuye;
            try {
                distribuye = em.getReference(Distribuye.class, id);
                distribuye.getIdDistribuidor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("El distribuidor con id " + id + " no existe.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Juego> juegoListOrphanCheck = distribuye.getJuegoList();
            for (Juego juegoListOrphanCheckJuego : juegoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El Distribuidor con id " + id + " no puede ser eliminado dado que está siendo referenciado en la tabla Juego " + juegoListOrphanCheckJuego);
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(distribuye);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Distribuye> findDistribuyeEntities() {
        return findDistribuyeEntities(true, -1, -1);
    }

    public List<Distribuye> findDistribuyeEntities(int maxResults, int firstResult) {
        return findDistribuyeEntities(false, maxResults, firstResult);
    }

    private List<Distribuye> findDistribuyeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Distribuye.class));
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

    public Distribuye findDistribuye(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Distribuye.class, id);
        } finally {
            em.close();
        }
    }

    public int getDistribuyeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Distribuye> rt = cq.from(Distribuye.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Distribuye> ListDistribuyeByFilter(String filtra) {
        filtra = filtra.toUpperCase();

        String QUERY = "SELECT d FROM Distribuye d WHERE d.idDistribuidor LIKE :idDistribuidor OR d.direccion LIKE :direccion"
                + " OR d.ciudad LIKE :ciudad OR d.pais LIKE :pais";

        EntityManager em = getEntityManager();
        List<Distribuye> distribuyetmp = new ArrayList<>();

        try {
            TypedQuery<Distribuye> consulta = em.createQuery(QUERY, Distribuye.class); //preparamos la consulta QUERY a realizar
            consulta.setParameter("idDistribuidor", "%" + filtra + "%");    //indico el campo y la cadena a buscar 
            consulta.setParameter("direccion", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            consulta.setParameter("ciudad", "%" + filtra + "%");     //indico el campo y la cadena a buscar 
            consulta.setParameter("pais", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            distribuyetmp = consulta.getResultList();  //guardo la consulta realiza en un objeto de tipo Usuario

        } catch (Exception e) {
        } finally {
            em.close();
        }
        return distribuyetmp;
    }

    public List<Distribuye> ListDistribuyeByNombre() {
        String QUERY = "SELECT d FROM Distribuye d WHERE d.idDistribuidor LIKE :idDistribuidor";

        EntityManager em = getEntityManager();
        List<Distribuye> distribuyetmp = new ArrayList<>();

        try {
            TypedQuery<Distribuye> consulta = em.createQuery(QUERY, Distribuye.class); //preparamos la consulta QUERY a realizar
            consulta.setParameter("idDistribuidor", "%" + "%");    //indico el campo y la cadena a buscar 

            distribuyetmp = consulta.getResultList();  //guardo la consulta realiza en un objeto de tipo Usuario

        } catch (Exception e) {
        } finally {
            em.close();
        }
        return distribuyetmp;
    }

}
