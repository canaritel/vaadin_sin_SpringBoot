package org.vaadin.example.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.vaadin.example.entities.Distribuye;

public class ContactFormDistribuye extends FormLayout {

    private final TextField textNombre = new TextField("Nombre");
    private final TextField textDireccion = new TextField("Dirección");
    private final TextField textCiudad = new TextField("Ciudad");
    private final TextField textPais = new TextField("País");

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");
    
    private final Binder<Distribuye> binder = new Binder<>(Distribuye.class);

    public ContactFormDistribuye() {
        addClassName("contact-form");

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        binder.bind(textNombre, Distribuye::getIdDistribuidor, Distribuye::setIdDistribuidor);
        binder.bind(textDireccion, Distribuye::getDireccion, Distribuye::setDireccion);
        binder.bind(textCiudad, Distribuye::getCiudad, Distribuye::setCiudad);
        binder.bind(textPais, Distribuye::getPais, Distribuye::setPais);

        textNombre.setEnabled(false);
        //añado los componentes a la vista
        add(textNombre, textDireccion, textCiudad, textPais);
        add(layout); //añado la línea verticaly así poner los botones debajo
        add(createButtonsLayout());
    }

    public void setContact(Distribuye distribuye) {
        binder.setBean(distribuye);

    }

    private Component createButtonsLayout() {
        //a los botones le creo personalizaciones visuales
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        //relaciono a los botones algunas teclas
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        } else {
            Notification.show("Error en la validación");
        }
    }

    // Eventos declarados en la misma clase
    public static abstract class ContactFormEvent extends ComponentEvent<ContactFormDistribuye> {

        private final Distribuye distribuye;

        public ContactFormEvent(ContactFormDistribuye source, Distribuye distribuye) {
            super(source, false);
            this.distribuye = distribuye;
        }

        public Distribuye getContact() {
            return distribuye;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactFormDistribuye source, Distribuye distribuye) {
            super(source, distribuye);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactFormDistribuye source, Distribuye distribuye) {
            super(source, distribuye);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactFormDistribuye source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void EncenderCampo (boolean valor){
        textNombre.setEnabled(valor);
    }
}
