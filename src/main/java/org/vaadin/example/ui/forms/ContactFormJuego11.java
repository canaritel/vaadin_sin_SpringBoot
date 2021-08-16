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
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.shared.Registration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.services.DistribuyeService;
import org.vaadin.example.services.UsuarioService;

public class ContactFormJuego11 extends FormLayout {

    private final TextField textTitulo = new TextField("Título");
    private final RadioButtonGroup<String> checkboxSistema = new RadioButtonGroup<>();
    private final DatePicker datePicker = new DatePicker();
    private final BigDecimalField bigDecimalField = new BigDecimalField("Precio de compra");
    //private final TextField textImagen = new TextField("Imagen");
    private final ComboBox<Distribuye> comboDistribuidor = new ComboBox<>();
    private final ComboBox<Usuario> comboUsuario = new ComboBox<>();

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final DistribuyeService distribuyeService = new DistribuyeService();
    private final UsuarioService usuarioService = new UsuarioService();

    private List<Distribuye> listDistribuye = new ArrayList<>();
    private List<Usuario> listUsuario = new ArrayList<>();

    private final Binder<Juego> binder = new Binder<>(Juego.class);

    public ContactFormJuego11() {
        addClassName("contact-form");

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        crearComboSistemaOperativo();
        crearCampoFecha();
        crearCampoPrecio();
        crearComboDistribuidor();
        crearComboUsuario();

        binder.forField(textTitulo)
                .withValidator(name -> name.length() >= 3, "El título debe tener al menos 3 carácteres")
                .bind(Juego::getTitulo, Juego::setTitulo);

        binder.forField(checkboxSistema)
                .bind(Juego::getSistemaOperativo, Juego::setSistemaOperativo);

        binder.forField(datePicker)
                .withValidator((new DateRangeValidator("No es una fecha válida", LocalDate.of(1973, Month.OCTOBER, 16), LocalDate.now())))
                .withConverter(new LocalDateToDateConverter())
                .bind(Juego::getFechaJuego, Juego::setFechaJuego);

        binder.forField(bigDecimalField)
                .withValidator(e -> e.doubleValue() > 0, "Debe ser un número superior a 0")
                .bind(Juego::getPrecio, Juego::setPrecio);

        binder.forField(comboDistribuidor)
                .asRequired("Seleccione el Distribuidor creador del juego")
                .bind(Juego::getDistribuidor, Juego::setDistribuidor);

        binder.forField(comboUsuario)
                .asRequired("Seleccione el Usuario propietario")
                .bind(Juego::getUsuario, Juego::setUsuario);

        //añado los componentes a la vista
        add(textTitulo, checkboxSistema, datePicker, bigDecimalField, comboDistribuidor, comboUsuario);
        add(layout); //añado la línea verticaly así poner los botones debajo
        add(createButtonsLayout());
    }

    public void setContact(Juego juego) {
        binder.setBean(juego);
        //if (juego != null) {
        //    Notification.show(juego.toString());
        //}
    }

    private Component createButtonsLayout() {
        // a los botones le creo personalizaciones visuales
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // relaciono a los botones algunas teclas
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        // El botón guardar llama al validateAndSavemétodo
        save.addClickListener(event -> validateAndSave());
        // El botón de eliminar dispara un evento de eliminación y pasa al contacto activo
        delete.addClickListener(event -> validateAndDelete());
        // El botón cancelar dispara un evento.
        close.addClickListener(event -> validateAndClose());

        // Valida el formulario cada vez que cambia. Si no es válido, desactiva el botón Guardar para evitar envíos no válidos.
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

    private void crearComboSistemaOperativo() {
        checkboxSistema.setLabel("Sistema Operativo");
        checkboxSistema.setItems("Windows 10/7", "Mac OS", "Android", "iOS", "Nintendo Wii", "XBox One", "Playstation 4");
        checkboxSistema.setValue("Windows 10/7");
        //checkboxSistema.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL); //pone los componentes en vertical
    }

    private void crearCampoFecha() {
        datePicker.setLabel("Fecha de compra");

        /*
        new DateFieldFormatter.Builder()
                .datePattern("ddMMyyyy")
                .delimiter("/")
                .dateMin(LocalDate.of(1980, 01, 01))
                .dateMax(LocalDate.now());
         .build().extend(textField);
         */
    }

    private void crearCampoPrecio() {
        bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        bigDecimalField.setPrefixComponent(new Icon(VaadinIcon.EURO));
        bigDecimalField.setValue(new BigDecimal(0).setScale(2));
    }

    private void crearComboDistribuidor() {
        listDistribuye = distribuyeService.listar("");
        // establecemos valores para el comboBox
        comboDistribuidor.setItemLabelGenerator(Distribuye::getIdDistribuidor);
        comboDistribuidor.setItems(listDistribuye);
        comboDistribuidor.setLabel("Distribuidor");
        comboDistribuidor.setPlaceholder("Elija Distribuidor o busque introduciendo el nombre");
    }

    private void crearComboUsuario() {
        listUsuario = usuarioService.listar("");
        // establecemos valores para el comboBox
        comboUsuario.setItemLabelGenerator(Usuario::getNombre);
        comboUsuario.setItems(listUsuario);
        comboUsuario.setLabel("Usuario");
        comboUsuario.setPlaceholder("Elija Usuario o busque introduciendo el nombre");
    }

    /**
     * ****************************************************************************
     * Vaadin viene con un sistema de manejo de eventos para componentes. Usado para escuchar eventos de cambio de valor desde la vista
     * principal. Podremos de esta forma llamar a los métodos desde el método superior DistribuidorView
     * *****************************************************************************
     */
    //ContactFormEventes una superclase común para todos los eventos. Contiene el contact que fue editado o eliminado.
    public static abstract class ContactFormEvent extends ComponentEvent<ContactFormJuego11> {

        private final Juego juego;

        public ContactFormEvent(ContactFormJuego11 source, Juego juego) {
            super(source, false);
            this.juego = juego;
        }

        public Juego getContact() {
            return juego;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactFormJuego11 source, Juego juego) {
            super(source, juego);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactFormJuego11 source, Juego juego) {
            super(source, juego);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactFormJuego11 source) {
            super(source, null);
        }
    }

    // El addListenermétodo utiliza el bus de eventos de Vaadin para registrar los tipos de eventos personalizados.
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
