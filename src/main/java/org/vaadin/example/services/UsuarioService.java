package org.vaadin.example.services;

import com.vaadin.flow.component.notification.Notification;
import java.util.List;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.interfaces.CrudDAOInterface;
import org.vaadin.example.repositories.UsuarioJpaController1;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;

public class UsuarioService implements CrudDAOInterface<Usuario> {

    private final UsuarioJpaController1 usuarioJpa = new UsuarioJpaController1(); //varible de tipo FINAL para hacerlo inamovible y constante
    private List<Usuario> usuarioList;
    String respuesta = "";

    @Override
    public List<Usuario> listar(String texto) {
        usuarioList = usuarioJpa.ListUsuarioByFilter(texto);
        return usuarioList;
    }

    @Override
    public void insertar(Usuario obj) {
        try {
            usuarioJpa.create(obj);
            respuesta = "Usuario creado correctamente";
        } catch (Exception e) {
            respuesta = "(Error crear)" + e.getMessage();
        }
    }

    @Override
    public void actualizar(Usuario obj) {
        try {
            obj.setNombre(obj.getNombre().toUpperCase().trim());
            obj.setApellidos(obj.getApellidos().toUpperCase().trim());
            usuarioJpa.edit(obj);
            respuesta = "Usuario editado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            respuesta = "(Error editar) " + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public void eliminar(Usuario obj) {
        try {
            usuarioJpa.destroy(obj.getIdUsuario());
            respuesta = "Usuario eliminado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (IllegalOrphanException | NonexistentEntityException e) {
            respuesta = "(Error eliminar) " + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public boolean existe(Usuario obj) {
        return false;
    }

    @Override
    public int total() {
        return usuarioJpa.getUsuarioCount();
    }

}
