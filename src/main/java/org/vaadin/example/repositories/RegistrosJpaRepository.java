package org.vaadin.example.repositories;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vaadin.example.entities.Accesos;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.vaadin.example.entities.Registros;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;

/**
 *
 * Para la implementación de estas clase Repositorios nos basamos en este vídeo donde lo explica detallamadamente:
 * https://youtu.be/yYukELtL4mM
 *
 * Para solucionar de que actualice siempre aunque el ID no exista vamos a seguir las indicaciones del siguiente vídeo
 * (minuto 25:35) https://youtu.be/osdl2--KRyc
 *
 */
public class RegistrosJpaRepository implements Serializable {

    public RegistrosJpaRepository() {
    }

    public RegistrosJpaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Añadimos nuestro objeto relacionado con la persistencia creada
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia-vaadin-mysql");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Registros registros) {
        if (registros.getAccesosList() == null) {
            registros.setAccesosList(new ArrayList<Accesos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Accesos> attachedAccesosList = new ArrayList<Accesos>();
            for (Accesos accesosListAccesosToAttach : registros.getAccesosList()) {
                accesosListAccesosToAttach = em.getReference(accesosListAccesosToAttach.getClass(), accesosListAccesosToAttach.getIdAcceso());
                attachedAccesosList.add(accesosListAccesosToAttach);
            }
            registros.setAccesosList(attachedAccesosList);
            em.persist(registros);
            for (Accesos accesosListAccesos : registros.getAccesosList()) {
                Registros oldRegistroOfAccesosListAccesos = accesosListAccesos.getRegistro();
                accesosListAccesos.setRegistro(registros);
                accesosListAccesos = em.merge(accesosListAccesos);
                if (oldRegistroOfAccesosListAccesos != null) {
                    oldRegistroOfAccesosListAccesos.getAccesosList().remove(accesosListAccesos);
                    oldRegistroOfAccesosListAccesos = em.merge(oldRegistroOfAccesosListAccesos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Registros registros) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            //añadimos las columnas de la parte inferior
            Integer id = registros.getIdRegistro();
            if (findRegistros(id) == null) {
                throw new NonexistentEntityException("El registro id " + id + " no existe.");
            } else { //añadimos y creamos la condición si existe el ID

                Registros persistentRegistros = em.find(Registros.class, registros.getIdRegistro());
                List<Accesos> accesosListOld = persistentRegistros.getAccesosList();
                List<Accesos> accesosListNew = registros.getAccesosList();
                List<String> illegalOrphanMessages = null;
                for (Accesos accesosListOldAccesos : accesosListOld) {
                    if (!accesosListNew.contains(accesosListOldAccesos)) {
                        if (illegalOrphanMessages == null) {
                            illegalOrphanMessages = new ArrayList<String>();
                        }
                        illegalOrphanMessages.add("You must retain Accesos " + accesosListOldAccesos + " since its registro field is not nullable.");
                    }
                }
                if (illegalOrphanMessages != null) {
                    throw new IllegalOrphanException(illegalOrphanMessages);
                }
                List<Accesos> attachedAccesosListNew = new ArrayList<Accesos>();
                for (Accesos accesosListNewAccesosToAttach : accesosListNew) {
                    accesosListNewAccesosToAttach = em.getReference(accesosListNewAccesosToAttach.getClass(), accesosListNewAccesosToAttach.getIdAcceso());
                    attachedAccesosListNew.add(accesosListNewAccesosToAttach);
                }
                accesosListNew = attachedAccesosListNew;
                registros.setAccesosList(accesosListNew);
                registros = em.merge(registros);
                for (Accesos accesosListNewAccesos : accesosListNew) {
                    if (!accesosListOld.contains(accesosListNewAccesos)) {
                        Registros oldRegistroOfAccesosListNewAccesos = accesosListNewAccesos.getRegistro();
                        accesosListNewAccesos.setRegistro(registros);
                        accesosListNewAccesos = em.merge(accesosListNewAccesos);
                        if (oldRegistroOfAccesosListNewAccesos != null && !oldRegistroOfAccesosListNewAccesos.equals(registros)) {
                            oldRegistroOfAccesosListNewAccesos.getAccesosList().remove(accesosListNewAccesos);
                            oldRegistroOfAccesosListNewAccesos = em.merge(oldRegistroOfAccesosListNewAccesos);
                        }
                    }
                }
            } //cerramos la condición añadida
            em.getTransaction().commit();
        } catch (Exception ex) {
            // String msg = ex.getLocalizedMessage();
            //  if (msg == null || msg.length() == 0) {

            // Estas líneas de abajo las pasamos arriba
            //      Integer id = registros.getIdRegistro();
            //      if (findRegistros(id) == null) {
            //          throw new NonexistentEntityException("The registros with id " + id + " no longer exists.");
            //      }
            //  }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registros registros;
            try {
                registros = em.getReference(Registros.class, id);
                registros.getIdRegistro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registros with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Accesos> accesosListOrphanCheck = registros.getAccesosList();
            for (Accesos accesosListOrphanCheckAccesos : accesosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Registros (" + registros + ") cannot be destroyed since the Accesos " + accesosListOrphanCheckAccesos + " in its accesosList field has a non-nullable registro field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(registros);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Registros> findRegistrosEntities() {
        return findRegistrosEntities(true, -1, -1);
    }

    public List<Registros> findRegistrosEntities(int maxResults, int firstResult) {
        return findRegistrosEntities(false, maxResults, firstResult);
    }

    private List<Registros> findRegistrosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Registros.class));
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

    public Registros findRegistros(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Registros.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistrosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Registros> rt = cq.from(Registros.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
