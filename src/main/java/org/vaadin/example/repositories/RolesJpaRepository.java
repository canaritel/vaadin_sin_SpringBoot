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
import org.vaadin.example.entities.Roles;
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
public class RolesJpaRepository implements Serializable {

    public RolesJpaRepository() {
    }

    public RolesJpaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Añadimos nuestro objeto relacionado con la persistencia creada
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia-vaadin-mysql");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Roles roles) {
        if (roles.getAccesosList() == null) {
            roles.setAccesosList(new ArrayList<Accesos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Accesos> attachedAccesosList = new ArrayList<Accesos>();
            for (Accesos accesosListAccesosToAttach : roles.getAccesosList()) {
                accesosListAccesosToAttach = em.getReference(accesosListAccesosToAttach.getClass(), accesosListAccesosToAttach.getIdAcceso());
                attachedAccesosList.add(accesosListAccesosToAttach);
            }
            roles.setAccesosList(attachedAccesosList);
            em.persist(roles);
            for (Accesos accesosListAccesos : roles.getAccesosList()) {
                Roles oldRolOfAccesosListAccesos = accesosListAccesos.getRol();
                accesosListAccesos.setRol(roles);
                accesosListAccesos = em.merge(accesosListAccesos);
                if (oldRolOfAccesosListAccesos != null) {
                    oldRolOfAccesosListAccesos.getAccesosList().remove(accesosListAccesos);
                    oldRolOfAccesosListAccesos = em.merge(oldRolOfAccesosListAccesos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Roles roles) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            //añadimos las columnas de la parte inferior
            Integer id = roles.getIdRol();
            if (findRoles(id) == null) {
                throw new NonexistentEntityException("El rol con id " + id + " no existe.");
            } else { //añadimos y creamos la condición si existe el ID

                Roles persistentRoles = em.find(Roles.class, roles.getIdRol());
                List<Accesos> accesosListOld = persistentRoles.getAccesosList();
                List<Accesos> accesosListNew = roles.getAccesosList();
                List<String> illegalOrphanMessages = null;
                for (Accesos accesosListOldAccesos : accesosListOld) {
                    if (!accesosListNew.contains(accesosListOldAccesos)) {
                        if (illegalOrphanMessages == null) {
                            illegalOrphanMessages = new ArrayList<String>();
                        }
                        illegalOrphanMessages.add("You must retain Accesos " + accesosListOldAccesos + " since its rol field is not nullable.");
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
                roles.setAccesosList(accesosListNew);
                roles = em.merge(roles);
                for (Accesos accesosListNewAccesos : accesosListNew) {
                    if (!accesosListOld.contains(accesosListNewAccesos)) {
                        Roles oldRolOfAccesosListNewAccesos = accesosListNewAccesos.getRol();
                        accesosListNewAccesos.setRol(roles);
                        accesosListNewAccesos = em.merge(accesosListNewAccesos);
                        if (oldRolOfAccesosListNewAccesos != null && !oldRolOfAccesosListNewAccesos.equals(roles)) {
                            oldRolOfAccesosListNewAccesos.getAccesosList().remove(accesosListNewAccesos);
                            oldRolOfAccesosListNewAccesos = em.merge(oldRolOfAccesosListNewAccesos);
                        }
                    }
                }
                em.getTransaction().commit();
            } //cerramos la condición añadida
        } catch (Exception ex) {
            //  String msg = ex.getLocalizedMessage();
            //  if (msg == null || msg.length() == 0) {

            // Estas líneas de abajo las pasamos arriba
            //      Integer id = roles.getIdRol();
            //      if (findRoles(id) == null) {
            //          throw new NonexistentEntityException("The roles with id " + id + " no longer exists.");
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
            Roles roles;
            try {
                roles = em.getReference(Roles.class, id);
                roles.getIdRol();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("El rol con id " + id + " no existe.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Accesos> accesosListOrphanCheck = roles.getAccesosList();
            for (Accesos accesosListOrphanCheckAccesos : accesosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El Rol (" + roles + ") no puede ser eliminado desde Accesos " + accesosListOrphanCheckAccesos + " in its accesosList field has a non-nullable rol field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(roles);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Roles> findRolesEntities() {
        return findRolesEntities(true, -1, -1);
    }

    public List<Roles> findRolesEntities(int maxResults, int firstResult) {
        return findRolesEntities(false, maxResults, firstResult);
    }

    private List<Roles> findRolesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Roles.class));
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

    public Roles findRoles(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Roles.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Roles> rt = cq.from(Roles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
