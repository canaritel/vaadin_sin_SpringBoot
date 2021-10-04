package org.vaadin.example.interfaces;

import java.io.Serializable;

/**
 * Simple interface for authentication and authorization checks.
 */
public interface AccessControlInterface extends Serializable {

    String ADMIN_ROLE_NAME = "ADMIN";

    boolean signIn(String username, String password);

    boolean isUserSignedIn();

    boolean isUserInRole(String role);

    String getPrincipalName();

    void signOut();
    
    boolean initFast();

}
