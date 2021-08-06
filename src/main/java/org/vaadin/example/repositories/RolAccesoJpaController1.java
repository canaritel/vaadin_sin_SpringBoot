package org.vaadin.example.repositories;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vaadin.example.entities.RolAcceso;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;

/**
 *
 * @author telev
 */
public class RolAccesoJpaController1 implements Serializable {

    public RolAccesoJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RolAcceso rolAcceso) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(rolAcceso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RolAcceso rolAcceso) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            rolAcceso = em.merge(rolAcceso);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rolAcceso.getIdAcceso();
                if (findRolAcceso(id) == null) {
                    throw new NonexistentEntityException("The rolAcceso with id " + id + " no longer exists.");
                }
            }
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
            RolAcceso rolAcceso;
            try {
                rolAcceso = em.getReference(RolAcceso.class, id);
                rolAcceso.getIdAcceso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rolAcceso with id " + id + " no longer exists.", enfe);
            }
            em.remove(rolAcceso);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RolAcceso> findRolAccesoEntities() {
        return findRolAccesoEntities(true, -1, -1);
    }

    public List<RolAcceso> findRolAccesoEntities(int maxResults, int firstResult) {
        return findRolAccesoEntities(false, maxResults, firstResult);
    }

    private List<RolAcceso> findRolAccesoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RolAcceso.class));
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

    public RolAcceso findRolAcceso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RolAcceso.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolAccesoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RolAcceso> rt = cq.from(RolAcceso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
