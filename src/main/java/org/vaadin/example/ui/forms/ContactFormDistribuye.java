package org.vaadin.example.ui.forms;

import org.vaadin.example.utils.CountryList;
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
import java.util.List;
import org.vaadin.example.entities.Distribuye;

public class ContactFormDistribuye extends FormLayout {

    private final TextField textNombre = new TextField("Nombre");
    private final TextField textDireccion = new TextField("Dirección");
    private final TextField textCiudad = new TextField("Ciudad");
    //private final TextField textPais = new TextField("País");
    private final ComboBox<String> comboBox = new ComboBox<>();

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final Binder<Distribuye> binder = new Binder<>(Distribuye.class);

    public ContactFormDistribuye() {
        addClassName("contact-form");
        // cargamos en paisesList el listado de países
        List<String> paisestList = CountryList.listadoPaises();

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        // establecemos valores para el comboBox
        comboBox.setItems(paisestList);
        comboBox.setLabel("País");
        comboBox.setPlaceholder("Elija país o busque introduciendo el nombre del país");

        //binder.bind(textNombre, Distribuye::getIdDistribuidor, Distribuye::setIdDistribuidor);
        //binder.bind(textDireccion, Distribuye::getDireccion, Distribuye::setDireccion);
        //binder.bind(textCiudad, Distribuye::getCiudad, Distribuye::setCiudad);
        //binder.bind(comboBox, Distribuye::getPais, Distribuye::setPais);
        binder.forField(textNombre)
                .withValidator(name -> name.length() >= 5, "El nombre debe tener al menos 5 carácteres")
                .bind(Distribuye::getIdDistribuidor, Distribuye::setIdDistribuidor);

        binder.forField(textDireccion)
                .withValidator(name -> name.length() >= 5, "La dirección debe tener al menos 5 carácteres")
                .bind(Distribuye::getDireccion, Distribuye::setDireccion);

        binder.forField(textCiudad)
                .withValidator(name -> name.length() >= 3, "La ciudad debe tener al menos 3 carácteres")
                .bind(Distribuye::getCiudad, Distribuye::setCiudad);

        binder.forField(comboBox)
                .withValidator(name -> name.length() >= 3, "El país debe tener al menos 3 carácteres")
                .bind(Distribuye::getPais, Distribuye::setPais);
        //binder.forField(comboBox)
        //        .withConverter(String::valueOf, String::valueOf).bind(Distribuye::getPais, Distribuye::setPais);
        //Por defecto el campo Nombre lo desactivamos
        //textNombre.setEnabled(false);
        //
        //añado los componentes a la vista
        add(textNombre, textDireccion, textCiudad, comboBox);
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
        delete.addClickListener(event -> validateAndDelete());
        close.addClickListener(event -> validateAndClose());

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

    private void validateAndClose() {
        //no es necesario validar los campos para Cerrar
        //if (binder.isValid()) {
        fireEvent(new CloseEvent(this));
        // }
    }

    private void validateAndDelete() {
        //no es necesario validar los campos para Eliminar
        //if (binder.isValid()) {
        fireEvent(new DeleteEvent(this, binder.getBean()));
        // } 
    }

    public void EncenderCampo(boolean valor) {
        textNombre.setEnabled(valor);
    }

    // Eventos declarados en la misma clase
    // Podremos de esta forma llamar a los métodos desde el método superior DistribuidorView
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

}
