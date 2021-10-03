package org.vaadin.example.ui.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.services.AuthService;

/**
 * Default mock implementation of {@link AccessControlInterface}. This implementation accepts any string as a user if
 * the password is the same string, and considers the user "admin" as the only administrator.
 */
public class BasicAccessControl implements AccessControlInterface {

    private AuthService authService = new AuthService();

    @Override
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        try {
            authService.authenticate(username, password);
        } catch (AuthService.AuthException ex) {
            return false;
        }

        CurrentUser.set(username);
        return true;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {
        if ("ADMIN".equals(role)) {
            // Only the "admin" user is in the "admin" role
            return getPrincipalName().equals("admin");
        }

        // All users are in all non-admin roles
        return true;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get();
    }

    @Override
    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().navigate("");
    }
}
