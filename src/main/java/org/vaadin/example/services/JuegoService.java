package org.vaadin.example.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.repositories.JuegoJpaRepository;
import org.vaadin.example.interfaces.CrudInterface;

public class JuegoService implements CrudInterface<Juego> {

    private final JuegoJpaRepository juegoRepository = new JuegoJpaRepository();
    private List<Juego> juegoList;
    String respuesta = "";

    @Override
    public List<Juego> listar(String texto) {
        juegoList = juegoRepository.findJuegoEntities();
        return juegoList;
    }

    @Override
    public void insertar(Juego obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actualizar(Juego obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void eliminar(Juego obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int total() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        this.listar("").forEach(juego
                -> stats.put(juego.getTitulo(), juego.getPrecio().intValue()));
        return stats;
    }

}
