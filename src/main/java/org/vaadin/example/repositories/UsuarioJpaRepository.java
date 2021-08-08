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
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
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
public class UsuarioJpaRepository implements Serializable {

    //Creamos constructor vacío
    public UsuarioJpaRepository() {
    }

    public UsuarioJpaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Añadimos nuestro objeto relacionado con la persistencia creada
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia-vaadin-mysql");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getJuegoList() == null) {
            usuario.setJuegoList(new ArrayList<Juego>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Juego> attachedJuegoList = new ArrayList<Juego>();
            for (Juego juegoListJuegoToAttach : usuario.getJuegoList()) {
                juegoListJuegoToAttach = em.getReference(juegoListJuegoToAttach.getClass(), juegoListJuegoToAttach.getIdJuego());
                attachedJuegoList.add(juegoListJuegoToAttach);
            }
            usuario.setJuegoList(attachedJuegoList);
            em.persist(usuario);
            for (Juego juegoListJuego : usuario.getJuegoList()) {
                Usuario oldUsuarioOfJuegoListJuego = juegoListJuego.getUsuario();
                juegoListJuego.setUsuario(usuario);
                juegoListJuego = em.merge(juegoListJuego);
                if (oldUsuarioOfJuegoListJuego != null) {
                    oldUsuarioOfJuegoListJuego.getJuegoList().remove(juegoListJuego);
                    oldUsuarioOfJuegoListJuego = em.merge(oldUsuarioOfJuegoListJuego);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            //añadimos las columnas de la parte inferior
            Integer id = usuario.getIdUsuario();
            if (findUsuario(id) == null) {
                throw new NonexistentEntityException("El id " + id + " no existe.");
            } else { //añadimos y creamos la condición si existe el ID

                Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
                List<Juego> juegoListOld = persistentUsuario.getJuegoList();
                List<Juego> juegoListNew = usuario.getJuegoList();
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
                usuario.setJuegoList(juegoListNew);
                usuario = em.merge(usuario);
                for (Juego juegoListNewJuego : juegoListNew) {
                    if (!juegoListOld.contains(juegoListNewJuego)) {
                        Usuario oldUsuarioOfJuegoListNewJuego = juegoListNewJuego.getUsuario();
                        juegoListNewJuego.setUsuario(usuario);
                        juegoListNewJuego = em.merge(juegoListNewJuego);
                        if (oldUsuarioOfJuegoListNewJuego != null && !oldUsuarioOfJuegoListNewJuego.equals(usuario)) {
                            oldUsuarioOfJuegoListNewJuego.getJuegoList().remove(juegoListNewJuego);
                            oldUsuarioOfJuegoListNewJuego = em.merge(oldUsuarioOfJuegoListNewJuego);
                        }
                    }
                }
                em.getTransaction().commit();
            } //cerramos la condición añadida
        } catch (Exception ex) {
            // String msg = ex.getLocalizedMessage();
            // if (msg == null || msg.length() == 0) {
            /*  
            // Estas líneas de abajo las pasamos arriba
            Integer id = usuario.getIdUsuario();
            if (findUsuario(id) == null) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
            } */

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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class,
                                          id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("El usuario con id " + id + " no existe.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Juego> juegoListOrphanCheck = usuario.getJuegoList();
            for (Juego juegoListOrphanCheckJuego : juegoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("El Usuario con id " + id + " no puede ser eliminado dado que está siendo referenciado en la tabla Juego " + juegoListOrphanCheckJuego);
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class
            ));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Usuario> ListUsuarioByFilter(String filtra) {
        filtra = filtra.toUpperCase();

        String QUERY = "SELECT u FROM Usuario u WHERE u.nombre LIKE :nombre OR u.apellidos LIKE :apellidos"
                + " OR u.telefono LIKE :telefono OR u.edad LIKE :edad";

        EntityManager em = getEntityManager();
        List<Usuario> usertmp = new ArrayList<>();
        //boolean respuesta = false;
        try {
            //TypedQuery<Usuario> consultaAlumnos = em.createNamedQuery("Usuario.findByNombre", Usuario.class); //Cuando uso las QUERY creadas en la entidad
            TypedQuery<Usuario> consulta = em.createQuery(QUERY, Usuario.class); //preparamos la consulta QUERY a realizar
            consulta.setParameter("nombre", "%" + filtra + "%");    //indico el campo y la cadena a buscar 
            consulta.setParameter("apellidos", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            consulta.setParameter("edad", "%" + filtra + "%");     //indico el campo y la cadena a buscar 
            consulta.setParameter("telefono", "%" + filtra + "%"); //indico el campo y la cadena a buscar 
            usertmp = consulta.getResultList();  //guardo la consulta realiza en un objeto de tipo Usuario

            //if (!usertmp.isEmpty()) {   //si el resultado es distinto de nulo es que no existe
            //    respuesta = true;
            //}
        } catch (Exception e) {
        } finally {
            em.close();
        }
        return usertmp;
    }

}
