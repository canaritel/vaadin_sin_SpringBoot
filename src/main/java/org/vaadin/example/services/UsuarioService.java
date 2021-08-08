package org.vaadin.example.services;

import com.vaadin.flow.component.notification.Notification;
import java.util.List;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.repositories.UsuarioJpaRepository;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;
import org.vaadin.example.interfaces.CrudInterface;

public class UsuarioService implements CrudInterface<Usuario> {

    private final UsuarioJpaRepository usuarioRepository = new UsuarioJpaRepository(); //varible de tipo FINAL para hacerlo inamovible y constante
    private List<Usuario> usuarioList;
    String respuesta = "";

    @Override
    public List<Usuario> listar(String texto) {
        usuarioList = usuarioRepository.ListUsuarioByFilter(texto);
        return usuarioList;
    }

    @Override
    public void insertar(Usuario obj) {
        try {
            usuarioRepository.create(obj);
            respuesta = "Usuario creado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            respuesta = "(Error crear)" + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public void actualizar(Usuario obj) {
        try {
            obj.setNombre(obj.getNombre().toUpperCase().trim());
            obj.setApellidos(obj.getApellidos().toUpperCase().trim());
            usuarioRepository.edit(obj);
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
            usuarioRepository.destroy(obj.getIdUsuario());
            respuesta = "Usuario eliminado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (IllegalOrphanException | NonexistentEntityException e) {
            respuesta = "(Error eliminar) " + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public int total() {
        return usuarioRepository.getUsuarioCount();
    }

}
