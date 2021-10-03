package org.vaadin.example.ui;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.vaadin.example.ui.authentication.AccessControlFactory;
import org.vaadin.example.ui.authentication.AccessControlInterface;
import org.vaadin.example.ui.views.LoginView;

/**
 * This class is used to listen to BeforeEnter event of all UIs in order to check whether a user is signed in or not
 * before allowing entering any page. It is registered in a file named com.vaadin.flow.server.VaadinServiceInitListener
 * in META-INF/services.
 */
/*
Revisar esta documentación para implementarlo correctamente "Como implementar un SPI"
https://vaadin.com/docs/v14/flow/advanced/tutorial-service-init-listener
https://programmerclick.com/article/85451408396/
 */
//Este método hará que se ejecute primero la clase LoginView
public class VaadinInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent initEvent) {

        final AccessControlInterface accessControl = AccessControlFactory.getInstance()
                .createAccessControl();

        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {

                if (!accessControl.isUserSignedIn() && !LoginView.class
                        .equals(enterEvent.getNavigationTarget())) {
                    enterEvent.rerouteTo(LoginView.class);
                }
            });
        });

    }
}
