package org.vaadin.example.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.interfaces.CrudDAOInterface;
import org.vaadin.example.repositories.JuegoJpaController1;

public class JuegoService implements CrudDAOInterface<Juego> {

    private final JuegoJpaController1 juegoJpa = new JuegoJpaController1();
    private List<Juego> juegoList;

    @Override
    public List<Juego> listar(String texto) {
        juegoList = juegoJpa.findJuegoEntities();
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
    public boolean existe(Juego obj) {
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
