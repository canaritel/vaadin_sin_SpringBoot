package org.vaadin.example.services;

import org.vaadin.example.entities.Accesos;
import org.vaadin.example.entities.Roles;
import org.vaadin.example.repositories.AccesosJpaRepository;

public class AuthService {

    //private AuthorizeRoute authorizeRoute;
    public AuthService() {
    }

    public static String ROL = "user";

    //creamos nuestra propia excepción
    public class AuthException extends Exception {

    }

    private final AccesosJpaRepository accesoRepository = new AccesosJpaRepository();   //variable de tipo FINAL para hacerlo inamovible y constante

    public void authenticate(String username, String password) throws AuthException {
        Accesos acceso = accesoRepository.getByUsermame(username);
        if (acceso != null && checkPassword(password, acceso.getPasswordSalt(), acceso.getPasswordHash())) {
            createRoutes(acceso.getRol());

        } else {
            throw new AuthException();
        }
    }

    private void createRoutes(Roles rol) {
        getAuthorizedRoutes(rol);
    }

    public void getAuthorizedRoutes(Roles rol) {
        //ArrayList<AuthorizeRoute> listRoutes = new ArrayList<>();
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
