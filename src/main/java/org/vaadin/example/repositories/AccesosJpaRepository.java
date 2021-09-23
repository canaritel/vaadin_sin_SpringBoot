package org.vaadin.example.repositories;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.vaadin.example.entities.Accesos;
import org.vaadin.example.entities.Registros;
import org.vaadin.example.entities.Roles;
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
public class AccesosJpaRepository implements Serializable {

    //Creamos constructor vacío
    public AccesosJpaRepository() {
    }

    public AccesosJpaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Añadimos nuestro objeto relacionado con la persistencia creada
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia-vaadin-mysql");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Accesos accesos) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registros registro = accesos.getRegistro();
            if (registro != null) {
                registro = em.getReference(registro.getClass(), registro.getIdRegistro());
                accesos.setRegistro(registro);
            }
            Roles rol = accesos.getRol();
            if (rol != null) {
                rol = em.getReference(rol.getClass(), rol.getIdRol());
                accesos.setRol(rol);
            }
            em.persist(accesos);
            if (registro != null) {
                registro.getAccesosList().add(accesos);
                registro = em.merge(registro);
            }
            if (rol != null) {
                rol.getAccesosList().add(accesos);
                rol = em.merge(rol);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Accesos accesos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            //añadimos las columnas de la parte inferior
            Integer id = accesos.getIdAcceso();
            if (findAccesos(id) == null) {
                throw new NonexistentEntityException("El id " + id + " no existe.");
            } else { //añadimos y creamos la condición si existe el ID

                Accesos persistentAccesos = em.find(Accesos.class, accesos.getIdAcceso());
                Registros registroOld = persistentAccesos.getRegistro();
                Registros registroNew = accesos.getRegistro();
                Roles rolOld = persistentAccesos.getRol();
                Roles rolNew = accesos.getRol();
                if (registroNew != null) {
                    registroNew = em.getReference(registroNew.getClass(), registroNew.getIdRegistro());
                    accesos.setRegistro(registroNew);
                }
                if (rolNew != null) {
                    rolNew = em.getReference(rolNew.getClass(), rolNew.getIdRol());
                    accesos.setRol(rolNew);
                }
                accesos = em.merge(accesos);
                if (registroOld != null && !registroOld.equals(registroNew)) {
                    registroOld.getAccesosList().remove(accesos);
                    registroOld = em.merge(registroOld);
                }
                if (registroNew != null && !registroNew.equals(registroOld)) {
                    registroNew.getAccesosList().add(accesos);
                    registroNew = em.merge(registroNew);
                }
                if (rolOld != null && !rolOld.equals(rolNew)) {
                    rolOld.getAccesosList().remove(accesos);
                    rolOld = em.merge(rolOld);
                }
                if (rolNew != null && !rolNew.equals(rolOld)) {
                    rolNew.getAccesosList().add(accesos);
                    rolNew = em.merge(rolNew);
                }
                em.getTransaction().commit();
            } //cerramos la condición añadida
        } catch (Exception ex) {
            //   String msg = ex.getLocalizedMessage();
            //   if (msg == null || msg.length() == 0) {

            // Estas líneas de abajo las pasamos arriba
            //       Integer id = accesos.getIdAcceso();
            //       if (findAccesos(id) == null) {
            //           throw new NonexistentEntityException("The accesos with id " + id + " no longer exists.");
            //       }
            //   }
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
            Accesos accesos;
            try {
                accesos = em.getReference(Accesos.class, id);
                accesos.getIdAcceso();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("El accesos con id " + id + " no existe.", enfe);
            }
            Registros registro = accesos.getRegistro();
            if (registro != null) {
                registro.getAccesosList().remove(accesos);
                registro = em.merge(registro);
            }
            Roles rol = accesos.getRol();
            if (rol != null) {
                rol.getAccesosList().remove(accesos);
                rol = em.merge(rol);
            }
            em.remove(accesos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Accesos> findAccesosEntities() {
        return findAccesosEntities(true, -1, -1);
    }

    public List<Accesos> findAccesosEntities(int maxResults, int firstResult) {
        return findAccesosEntities(false, maxResults, firstResult);
    }

    private List<Accesos> findAccesosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Accesos.class));
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

    public Accesos findAccesos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Accesos.class, id);
        } finally {
            em.close();
        }
    }

    public int getAccesosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Accesos> rt = cq.from(Accesos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Accesos getByUsermame(String username) {
        EntityManager em = getEntityManager();
        Accesos accesotmp = new Accesos();

        try {
            TypedQuery<Accesos> consulta = em.createNamedQuery("Accesos.findByUsername", Accesos.class); //Cuando uso las QUERY creadas en la entidad
            consulta.setParameter("username", username);    //indico el campo y la cadena a buscar 
            accesotmp = consulta.getSingleResult();  //guardo la consulta realiza en un objeto

        } catch (Exception e) {
        } finally {
            em.close();
        }
        return accesotmp;
    }

}
