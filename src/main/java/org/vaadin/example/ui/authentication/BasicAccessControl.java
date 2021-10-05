package org.vaadin.example.ui.authentication;

import org.vaadin.example.interfaces.AccessControlInterface;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.services.AuthService;
import static org.vaadin.example.services.AuthService.ROL;

/**
 * Default mock implementation of {@link AccessControlInterface}. This implementation accepts any string as a user if
 * the password is the same string, and considers the user "admin" as the only administrator.
 */
public class BasicAccessControl implements AccessControlInterface {

    private final AuthService authService = new AuthService();

    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        if (authService.authenticate(username, password)) {
            CurrentUser.set(username);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    //Este método devuelve true si el ROL del usuario que accede es de tipo admin
    @Override
    public boolean isUserInRole() {
        if ("ADMIN".equals(ROL)) {
            return true;
        }
        // All users are in all non-admin roles
        return false;
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("");
    }

    //La única misión de este método es iniciar la conexión hacia la BD, de esta forma mejoramos la velocidad en el proceso de logeo
    @Override
    public boolean initFast() {
        return (authService.authenticate("pepe", "123456"));

    }

}
