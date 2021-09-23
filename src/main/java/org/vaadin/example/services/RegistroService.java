package org.vaadin.example.services;

import org.vaadin.example.entities.Registros;
import org.vaadin.example.repositories.RegistrosJpaRepository;

public class RegistroService {

    private final RegistrosJpaRepository registroRepository = new RegistrosJpaRepository();   //variable de tipo FINAL para hacerlo inamovible y constante

    public Registros leerRegistro(Integer id) {
        return registroRepository.findRegistros(5); //leemos el REgistro creado
    }

}
