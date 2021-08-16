package org.vaadin.example.services;

import com.vaadin.flow.component.notification.Notification;
import java.util.List;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.interfaces.CrudInterface;
import org.vaadin.example.repositories.DistribuyeJpaRepository;
import org.vaadin.example.repositories.exceptions.IllegalOrphanException;
import org.vaadin.example.repositories.exceptions.NonexistentEntityException;

public class DistribuyeService implements CrudInterface<Distribuye> {

    private final DistribuyeJpaRepository distribuyeRepository = new DistribuyeJpaRepository();   //varible de tipo FINAL para hacerlo inamovible y constante
    private List<Distribuye> distribuyeList;
    String respuesta = "";

    @Override
    public List<Distribuye> listar(String texto) {
        distribuyeList = distribuyeRepository.ListDistribuyeByFilter(texto);
        return distribuyeList;
    }

    @Override
    public void insertar(Distribuye obj) {
        try {
            obj.setIdDistribuidor(obj.getIdDistribuidor().toUpperCase().trim());
            obj.setDireccion(obj.getDireccion().toUpperCase().trim());
            obj.setCiudad(obj.getCiudad().toUpperCase().trim());
            obj.setPais(obj.getPais().toUpperCase().trim());
            distribuyeRepository.create(obj);
            respuesta = "Distribuidor creado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            respuesta = "(Error crear)" + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public void actualizar(Distribuye obj) {
        try {
            //obj.setIdDistribuidor(obj.getIdDistribuidor().toUpperCase().trim());
            obj.setDireccion(obj.getDireccion().toUpperCase().trim());
            obj.setCiudad(obj.getCiudad().toUpperCase().trim());
            obj.setPais(obj.getPais().toUpperCase().trim());
            distribuyeRepository.edit(obj);
            respuesta = "Distribuidor editado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            respuesta = "ERROR: Ya existe el ID " + obj.getIdDistribuidor();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public void eliminar(Distribuye obj) {
        try {
            distribuyeRepository.destroy(obj.getIdDistribuidor());
            respuesta = "Distribuidor eliminado correctamente";
            Notification.show(respuesta, 3000, Notification.Position.MIDDLE);
        } catch (IllegalOrphanException | NonexistentEntityException e) {
            respuesta = "(Error eliminar) " + e.getMessage();
            Notification.show(respuesta, 7000, Notification.Position.TOP_STRETCH);
        }
    }

    @Override
    public int total() {
        return distribuyeRepository.getDistribuyeCount();
    }

    //método aañadido no declarado en el interface
    public boolean existe(String id) {
        Distribuye distribuye = new Distribuye();
        distribuye.setIdDistribuidor(id);
        if (distribuyeRepository.findDistribuye(distribuye.getIdDistribuidor()) == null) {
            return false;
        } else {
            return true;
        }
    }

}
