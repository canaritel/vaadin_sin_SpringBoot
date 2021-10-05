package org.vaadin.example.interfaces;

import java.io.Serializable;

/**
 * Simple interface for authentication and authorization checks.
 */
public interface AccessControlInterface extends Serializable {

    boolean signIn(String username, String password);

    boolean isUserSignedIn();

    boolean isUserInRole();

    void signOut();

    boolean initFast();

}
