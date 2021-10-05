package org.vaadin.example.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.utils.ConfirmDialog;
// importamos una nueva función para comprobaciones numéricas en campos de texto
import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

public class ContactFormUser extends FormLayout {

    private final TextField textNombre = new TextField("Nombre");
    private final TextField textApellidos = new TextField("Apellidos");
    private final IntegerField numberEdad = new IntegerField("Edad");
    private final TextField textTelefono = new TextField("Teléfono");
    //private final ComboBox<String> estadoCombo = new ComboBox("Activo");
    private final RadioButtonGroup<String> estadoCombo = new RadioButtonGroup<>();

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final Binder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);

    public ContactFormUser() {
        addClassName("contact-form");

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        // establecemos reglas para el campo numberEdad
        numberEdad.setHasControls(true);
        //numberEdad.setMin(5);
        //numberEdad.setMax(99);

        // establecemos reglas y formato para el campo textTelefono.  Más info: https://vaadin.com/directory/component/textfield-formatter/overview
        new PhoneI18nFieldFormatter(PhoneI18nFieldFormatter.REGION_ES).extend(textTelefono);

        // establecemos valores para el comboBox
        estadoCombo.setItems("true", "false");
        estadoCombo.setHelperText("Seleccione true para Activo");
        estadoCombo.setLabel("Activo");

        //IMPORTANTE activar asRequired para evitar errores o warning en la ejecución
        binder.forField(textNombre).asRequired()
                .withValidator(name -> name.length() >= 3, "El nombre debe tener al menos 3 carácteres")
                .withValidator(name -> name.length() <= 60, "El nombre debe tener menos de 60 carácteres")
                .bind(Usuario::getNombre, Usuario::setNombre);
        //binder.bind(textApellidos, Usuario::getApellidos, Usuario::setApellidos);
        binder.forField(textApellidos).asRequired()
                .withValidator(surname -> surname.length() >= 5, "Los apellidos deben tener al menos 5 carácteres")
                .withValidator(surname -> surname.length() <= 60, "Los apellidos deben tener menos de 60 carácteres")
                .bind(Usuario::getApellidos, Usuario::setApellidos);
        //binder.forField(textEdad).withConverter(Integer::valueOf, String::valueOf).bind(Usuario::getEdad, Usuario::setEdad);
        binder.forField(numberEdad).asRequired()
                .withConverter(Integer::valueOf, Integer::valueOf)
                .withValidator(number -> number >= 5, "Debe tener al menos 5 años")
                .withValidator(number -> number <= 99, "Debe tener una edad menor a 100 años")
                .bind(Usuario::getEdad, Usuario::setEdad);
        //binder.bind(textTelefono, Usuario::getTelefono, Usuario::setTelefono);
        binder.forField(textTelefono).asRequired()
                .withValidator(phone -> (phone.length() >= 9 && phone.length() <= 13), "El teléfono debe contener al menos 9 dígitos y un máximo de 11")
                .bind(Usuario::getTelefono, Usuario::setTelefono);
        binder.forField(estadoCombo).asRequired()
                .withConverter(Boolean::valueOf, String::valueOf).bind(Usuario::getActivo, Usuario::setActivo);

        //añado los componentes a la vista
        add(textNombre, textApellidos, numberEdad, textTelefono, estadoCombo);
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
        delete.addClickListener(event -> validateAndDelete());
        close.addClickListener(event -> validateAndClose());
        //close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        } else {
            Notification.show("Error en la validación Bean").addThemeVariants(NotificationVariant.LUMO_ERROR);
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
        //if (!binder.equals(null)) {
        // Activamos una ventana de confirmación al eliminar
        ConfirmDialog dialog = new ConfirmDialog(
                "Por favor confirme",
                "¿Está seguro de eliminar este registro?.",
                "Eliminar", () -> {
                    fireEvent(new DeleteEvent(this, binder.getBean()));
                });

        dialog.open();
    }

    // Eventos declarados en la misma clase
    // Podremos de esta forma llamar a los métodos desde el método superior UsuarioView
    public static abstract class ContactFormEvent extends ComponentEvent<ContactFormUser> {

        private final Usuario usuario;

        public ContactFormEvent(ContactFormUser source, Usuario usuario) {
            super(source, false);
            this.usuario = usuario;
        }

        public Usuario getContact() {
            return usuario;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactFormUser source, Usuario usuario) {
            super(source, usuario);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactFormUser source, Usuario usuario) {
            super(source, usuario);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactFormUser source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
