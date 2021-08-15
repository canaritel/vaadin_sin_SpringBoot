package org.vaadin.example.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.entities.Usuario;

public class ContactFormJuego extends FormLayout {

    private final TextField textTitulo = new TextField("Título");
    private final RadioButtonGroup<String> checkboxSistema = new RadioButtonGroup<>();
    private final DatePicker datePicker = new DatePicker();
    private final TextField dobValue = new TextField();

    //private final TextField textFecha = new TextField("Fecha de compra");
    private final BigDecimalField bigDecimalField = new BigDecimalField("Precio de compra");
    //private final TextField textImagen = new TextField("Imagen");
    private final ComboBox<Distribuye> comboDistribuidor = new ComboBox<>();
    private final ComboBox<Usuario> comboUsuario = new ComboBox<>();

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final Binder<Juego> binder = new Binder<>(Juego.class);

    public ContactFormJuego() {
        addClassName("contact-form");

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        crearComboSistemaOperativo();
        crearCampoFecha();
        crearCampoPrecio();

        binder.forField(textTitulo)
                .withValidator(name -> name.length() >= 3, "El título debe tener al menos 3 carácteres")
                .bind(Juego::getTitulo, Juego::setTitulo);

        binder.bind(checkboxSistema, Juego::getSistemaOperativo, Juego::setSistemaOperativo);
        //        .withConverter(String::valueOf, String::valueOf).bind(Juego::getDistribuidor, Juego::setDistribuidor);
        // binder.bind(textSistema, Juego::getSistemaOperativo, Juego::setSistemaOperativo);
        binder.forField(datePicker).bind("datePicker"); //este falla
        //  binder.forField(datePicker).withConverter(DatePicker::valueOf, String::valueOf)
        //           .bind(Juego::getFechaJuego, Juego::setFechaJuego);
        // binder.forField(textFecha)
        //         .bind(Juego::getFechaJuego, Juego::setFechaJuego);
        //   binder.forField(textPrecio)
        //           .withConverter(String::valueOf, String::valueOf)
        //           .bind(Juego::getPrecio, Juego::setPrecio);
        //binder.forField(comboDistribuidor)
        //        .bind(Juego::getDistribuidor, Juego::setDistribuidor);
        //binder.forField(comboUsuario)
        //        .bind(Juego::getUsuario, Juego::setUsuario);
        // binder.forField(comboBox)
        //      .withConverter(String::valueOf, String::valueOf).bind(Distribuye::getPais, Distribuye::setPais);
        //
        //añado los componentes a la vista
        add(textTitulo, checkboxSistema, datePicker, bigDecimalField, comboDistribuidor, comboUsuario);
        add(layout); //añado la línea verticaly así poner los botones debajo
        add(createButtonsLayout());
    }

    public void setContact(Juego juego) {
        binder.setBean(juego);
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
        Notification.show("llega aquí");
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

    private void crearComboSistemaOperativo() {
        checkboxSistema.setLabel("Sistema Operativo");
        checkboxSistema.setItems("Windows 10/7", "Mac OS", "Android", "iOS", "Nintendo Wii", "XBox One", "Playstation 4");
        checkboxSistema.setValue("Windows 10/7");
        //checkboxSistema.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL); //pone los componentes en vertical
    }

    private void crearCampoFecha() {
        datePicker.setLabel("Fecha de compra");
        //datePicker.setPlaceholder("Fecha de compra");
        datePicker.setLocale(Locale.US);
        /*
        new DateFieldFormatter.Builder()
                .datePattern("ddMMyyyy")
                .delimiter("-")
                .dateMin(LocalDate.of(1980, 01, 01))
                .dateMax(LocalDate.now())
                .build().extend(textFecha);
        textFecha.setPlaceholder("Fecha tipo dd-mm-yyyy");
         */
        dobValue.setVisible(false);
        datePicker.addValueChangeListener(evt -> {
            LocalDate dob = evt.getValue();
            dobValue.setValue(dob.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US)));
        });
    }

    private void crearCampoPrecio() {
        bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        bigDecimalField.setPrefixComponent(new Icon(VaadinIcon.EURO));
        bigDecimalField.setValue(new BigDecimal(0).setScale(2));
    }

    // Eventos declarados en la misma clase
    // Podremos de esta forma llamar a los métodos desde el método superior DistribuidorView
    public static abstract class ContactFormEvent extends ComponentEvent<ContactFormJuego> {

        private final Juego juego;

        public ContactFormEvent(ContactFormJuego source, Juego juego) {
            super(source, false);
            this.juego = juego;
        }

        public Juego getContact() {
            return juego;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactFormJuego source, Juego juego) {
            super(source, juego);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactFormJuego source, Juego juego) {
            super(source, juego);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactFormJuego source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
