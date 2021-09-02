package org.vaadin.example.interfaces;

import java.util.List;

public interface CrudInterface<T> {

    public void insertar(T obj);

    public void actualizar(T obj);

    public void eliminar(T obj);

    public int total();

    public List<T> listar(String texto);

    public List<T> listarPagination(String texto, boolean all, int maxResults, int firsResult);

}
