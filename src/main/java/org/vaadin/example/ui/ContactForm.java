package org.vaadin.example.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.vaadin.example.entities.Usuario;

public class ContactForm extends FormLayout {

    private final TextField textNombre = new TextField("Nombre");
    private final TextField textApellidos = new TextField("Apellidos");
    private final TextField textEdad = new TextField("Edad");
    private final TextField textTelefono = new TextField("Teléfono");

    private final ComboBox<String> estadoCombo = new ComboBox("Activo");

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final Usuario usuario = new Usuario();

    private final Binder<Usuario> binder = new Binder<>(Usuario.class);

    public ContactForm() {
        addClassName("contact-form");

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        binder.bind(textNombre, Usuario::getNombre, Usuario::setNombre);
        binder.bind(textApellidos, Usuario::getApellidos, Usuario::setApellidos);
        binder.forField(textEdad).withConverter(Integer::valueOf, String::valueOf).bind(Usuario::getEdad, Usuario::setEdad);
        binder.bind(textTelefono, Usuario::getTelefono, Usuario::setTelefono);
        binder.forField(estadoCombo).withConverter(Boolean::valueOf, String::valueOf).bind(Usuario::getActivo, Usuario::setActivo);

        //creo valores para el comboBox
        estadoCombo.setItems("true", "false");

        //estadoCombo.setValue("SI");
        //añado los componentes a la vista
        add(textNombre, textApellidos, textEdad, textTelefono, estadoCombo);
        add(layout); //añado la línea verticaly así poner los botones debajo
        add(createButtonsLayout());
    }

    public void setContact(Usuario usuario) {
        binder.setBean(usuario);

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

    // Eventos
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {

        private final Usuario usuario;

        public ContactFormEvent(ContactForm source, Usuario usuario) {
            super(source, false);
            this.usuario = usuario;
        }

        public Usuario getContact() {
            return usuario;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactForm source, Usuario usuario) {
            super(source, usuario);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactForm source, Usuario usuario) {
            super(source, usuario);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
