package org.vaadin.example.services;

import org.vaadin.example.entities.Roles;
import org.vaadin.example.repositories.RolesJpaRepository;

public class RolesService {

    private final RolesJpaRepository rolesRepository = new RolesJpaRepository();   //variable de tipo FINAL para hacerlo inamovible y constante

    public Roles leerRoles(Integer id) {
        return rolesRepository.findRoles(1); //leemos el Rol tipo USER
    }

}
