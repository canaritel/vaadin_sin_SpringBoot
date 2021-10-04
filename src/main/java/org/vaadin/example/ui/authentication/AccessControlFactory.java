package org.vaadin.example.ui.authentication;

import org.vaadin.example.interfaces.AccessControlInterface;

public class AccessControlFactory {
    private static final AccessControlFactory INSTANCE = new AccessControlFactory();
    private final AccessControlInterface accessControl = new BasicAccessControl();

    private AccessControlFactory() {
    }

    public static AccessControlFactory getInstance() {
        return INSTANCE;
    }

    public AccessControlInterface createAccessControl() {
        return accessControl;
    }
}
