/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;
import org.vaadin.example.repositories.exceptions.PreexistingEntityException;

/**
 *
 * @author telev
 */
public class DistribuyeJpaController1 implements Serializable {

    public DistribuyeJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

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
            Distribuye persistentDistribuye = em.find(Distribuye.class, distribuye.getIdDistribuidor());
            List<Juego> juegoListOld = persistentDistribuye.getJuegoList();
            List<Juego> juegoListNew = distribuye.getJuegoList();
            List<String> illegalOrphanMessages = null;
            for (Juego juegoListOldJuego : juegoListOld) {
                if (!juegoListNew.contains(juegoListOldJuego)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Juego " + juegoListOldJuego + " since its distribuidor field is not nullable.");
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
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = distribuye.getIdDistribuidor();
                if (findDistribuye(id) == null) {
                    throw new NonexistentEntityException("The distribuye with id " + id + " no longer exists.");
                }
            }
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
                throw new NonexistentEntityException("The distribuye with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Juego> juegoListOrphanCheck = distribuye.getJuegoList();
            for (Juego juegoListOrphanCheckJuego : juegoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Distribuye (" + distribuye + ") cannot be destroyed since the Juego " + juegoListOrphanCheckJuego + " in its juegoList field has a non-nullable distribuidor field.");
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
    
}
