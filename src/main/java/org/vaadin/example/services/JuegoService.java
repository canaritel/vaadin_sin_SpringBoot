package org.vaadin.example.services;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.repositories.JuegoJpaRepository;
import org.vaadin.example.interfaces.CrudInterface;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;

public class JuegoService implements CrudInterface<Juego> {

    private final JuegoJpaRepository juegoRepository = new JuegoJpaRepository();
    private List<Juego> juegoList;
    String respuesta = "";

    @Override
    public List<Juego> listar(String texto) {
        juegoList = juegoRepository.ListJuegoByFilter(texto);
        return juegoList;
    }

    @Override
    public void insertar(Juego obj) {
        try {
            obj.setTitulo(obj.getTitulo().toUpperCase().trim());
            obj.setPrecio(obj.getPrecio().setScale(2, RoundingMode.HALF_UP));
            juegoRepository.create(obj);
            respuesta = "Juego creado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            respuesta = "(Error al crear)" + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    public void actualizar(Juego obj) {
        try {
            obj.setTitulo(obj.getTitulo().toUpperCase().trim());
            obj.setPrecio(obj.getPrecio().setScale(2, RoundingMode.HALF_UP));
            juegoRepository.edit(obj);
            respuesta = "Juego editado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            respuesta = "(Error al editar)" + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    public void eliminar(Juego obj) {
        try {
            juegoRepository.destroy(obj.getIdJuego());
            respuesta = "Juego eliminado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (NonexistentEntityException e) {
            respuesta = "(Error al eliminar)" + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    public int total() {
        return juegoRepository.getJuegoCount();
    }

    public Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        this.listar("").forEach(juego
                -> stats.put(juego.getTitulo(), juego.getPrecio().intValue()));
        return stats;
    }

}
