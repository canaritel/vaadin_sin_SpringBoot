package org.vaadin.example.services;

import org.vaadin.example.entities.Accesos;
import org.vaadin.example.entities.Roles;
import org.vaadin.example.repositories.AccesosJpaRepository;

public class AuthService {

    public AuthService() {
    }

    public static String ROL = "user";
    private final AccesosJpaRepository accesoRepository = new AccesosJpaRepository();   //variable de tipo FINAL para hacerlo inamovible y constante

    public boolean authenticate(String username, String password) {
        Accesos acceso = accesoRepository.getByUsermame(username);
        if (acceso != null && checkPassword(password, acceso.getPasswordSalt(), acceso.getPasswordHash())) {
            createRoutes(acceso.getRol());
            return true;
        } else {
            return false;
        }
    }

    private void createRoutes(Roles rol) {
        getAuthorizedRoutes(rol);
    }

    public void getAuthorizedRoutes(Roles rol) {
        if (rol.getNombreRol().equals("USER")) {
            ROL = "USER";
        } else if (rol.getNombreRol().equals("ADMIN")) {
            ROL = "ADMIN";
        }
    }

    public void grabarAcceso(Accesos acceso) {
        accesoRepository.create(acceso);
    }

    //Creamos un nuevo método para comprobar la clave
    private boolean checkPassword(String password, String passwordSalt, String passwordHash) {
        //return DigestUtils.sha1Hex(password + passwordSalt).equals(passwordHash);
        return password.equals(passwordHash);
    }

}
