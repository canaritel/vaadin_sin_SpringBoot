package org.vaadin.example.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.validator.BigDecimalRangeValidator;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.shared.Registration;
import java.io.IOException;
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
import org.vaadin.example.utils.ConvertToImage;

public class ContactFormJuego extends FormLayout {

    private final TextField textTitulo = new TextField("Título");
    private final RadioButtonGroup<String> checkboxSistema = new RadioButtonGroup<>();
    private final DatePicker datePicker = new DatePicker();
    private final BigDecimalField bigDecimalField = new BigDecimalField("Precio de compra");
    private final ComboBox<Distribuye> comboDistribuidor = new ComboBox<>();
    private final ComboBox<Usuario> comboUsuario = new ComboBox<>();
    private final Label labelImage = new Label();
    private byte[] imageByte = new byte[65535];

    private Image imagePreview = new Image();
    private Upload uploadImage; //recomendado no inicializarlo aquí

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final DistribuyeService distribuyeService = new DistribuyeService();
    private final UsuarioService usuarioService = new UsuarioService();

    private List<Distribuye> listDistribuye = new ArrayList<>();
    private List<Usuario> listUsuario = new ArrayList<>();

    private Component component, component2;

    private final Binder<Juego> binder = new BeanValidationBinder<>(Juego.class);

    public ContactFormJuego() {
        addClassName("contact-form");

        VerticalLayout layout = new VerticalLayout(); //creo componente de línea vertical

        crearComboSistemaOperativo();
        crearCampoFecha();
        crearCampoPrecio();
        crearComboDistribuidor();
        crearComboUsuario();
        crearFileImagen();

        binder.forField(textTitulo)
                .withValidator(name -> name.length() >= 3, "El título debe tener al menos 3 carácteres")
                .withValidator(name -> name.length() < 50, "El título debe tener menos de 50 carácteres")
                .bind(Juego::getTitulo, Juego::setTitulo);

        binder.forField(checkboxSistema)
                .bind(Juego::getSistemaOperativo, Juego::setSistemaOperativo);

        binder.forField(datePicker)
                .withValidator(date -> date != null, "No es una fecha válida")
                .withValidator((new DateRangeValidator("No es una fecha válida", LocalDate.of(1973, Month.OCTOBER, 16), LocalDate.now())))
                .withConverter(new LocalDateToDateConverter())
                .bind(Juego::getFechaJuego, Juego::setFechaJuego);

        binder.forField(bigDecimalField)
                .withValidator(new BigDecimalRangeValidator("Precio válido desde 0 hasta 9999", BigDecimal.ZERO, BigDecimal.valueOf(9999)))
                //.withValidator(e -> (e.doubleValue() > 0), "Debe ser un número superior a 0")
                //.withValidator(e -> (e.doubleValue() <= 9999), "Debe ser un número inferior o igual a 9999")
                .bind(Juego::getPrecio, Juego::setPrecio);

        binder.forField(comboDistribuidor)
                .asRequired("Seleccione el Distribuidor creador del juego")
                .bind(Juego::getDistribuidor, Juego::setDistribuidor);

        binder.forField(comboUsuario)
                .asRequired("Seleccione el Usuario propietario")
                .bind(Juego::getUsuario, Juego::setUsuario);

        //binder.bindInstanceFields(this);
        //binder.bindInstanceFields(comboUsuario);
        //binder.bindInstanceFields(comboDistribuidor);
        //añado los componentes a la vista
        //add(textTitulo, checkboxSistema, datePicker, bigDecimalField, comboDistribuidor, comboUsuario, labelImage);
        add(textTitulo, checkboxSistema);
        final HorizontalLayout horizontalLayout = new HorizontalLayout(datePicker, bigDecimalField);
        final HorizontalLayout horizontalLayout2 = new HorizontalLayout(comboDistribuidor, comboUsuario);
        add(horizontalLayout, horizontalLayout2, labelImage);

        add(layout); //añado el componente vertical para poner los botones debajo
        add(createButtonsLayout());
    }

    public void setContact(Juego juego) {
        binder.setBean(juego);

        if (juego != null) {
            imageByte = juego.getImagen();
            convertirImagen();
            mostrarImagenUpload();
        }
    }

    private Component createButtonsLayout() {
        // a los botones le creo personalizaciones visuales
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // relaciono a los botones algunas teclas
        save.addClickShortcut(Key.ENTER, KeyModifier.CONTROL);
        close.addClickShortcut(Key.ESCAPE);

        // El botón guardar llama al validateAndSavemétodo
        save.addClickListener(event -> validateAndSave());
        // El botón de eliminar dispara un evento de eliminación y pasa al contacto activo
        delete.addClickListener(event -> validateAndDelete());
        // El botón cancelar dispara un evento.
        close.addClickListener(event -> validateAndClose());

        // Valida el formulario cada vez que cambia. Si no es válido, desactiva el botón Guardar para evitar envíos no válidos.
        binder.addStatusChangeListener(evt -> {
            boolean hasChanges = evt.getBinder().hasChanges();
            save.setEnabled(hasChanges && binder.isValid());
            Notification.show(String.valueOf(hasChanges));
        });

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            binder.getBean().setImagen(imageByte);
            crearFileImagen();
            fireEvent(new SaveEvent(this, binder.getBean()));
        } else {
            Notification.show("Error en la validación Binder").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void validateAndClose() {
        //no es necesario validar los campos para Cerrar
        //if (binder.isValid()) {
        crearFileImagen();
        fireEvent(new CloseEvent(this));
    }

    private void validateAndDelete() {
        //no es necesario validar los campos para Eliminar
        //if (binder.isValid()) {
        crearFileImagen();
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
        datePicker.setWidth("100%");
    }

    private void crearCampoPrecio() {
        //bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        bigDecimalField.setPrefixComponent(new Icon(VaadinIcon.EURO));
        bigDecimalField.setValue(new BigDecimal(0).setScale(2));
        bigDecimalField.setWidth("100%");
    }

    private void crearComboDistribuidor() {
        listDistribuye = distribuyeService.listar("");
        // establecemos valores para el comboBox
        comboDistribuidor.setItemLabelGenerator(Distribuye::getIdDistribuidor);
        comboDistribuidor.setItems(listDistribuye);
        comboDistribuidor.setLabel("Distribuidor");
        comboDistribuidor.setPlaceholder("Elija Distribuidor o busque introduciendo el nombre");
        comboDistribuidor.setWidth("100%");
    }

    private void crearComboUsuario() {
        listUsuario = usuarioService.listar("");
        // establecemos valores para el comboBox
        comboUsuario.setItemLabelGenerator(Usuario::getNombre);
        comboUsuario.setItems(listUsuario);
        comboUsuario.setLabel("Usuario");
        comboUsuario.setPlaceholder("Elija Usuario o busque introduciendo el nombre");
        comboUsuario.setHeight("90px"); //espacio alto del componente
        comboUsuario.setWidth("100%");
    }

    private void crearFileImagen() {
        imagePreview.setWidth("100%");
        labelImage.setText("Imagen");
        uploadImage = new Upload();
        uploadImage.getStyle().set("box-sizing", "border-box");
        //llamamos al método attach
        attachImageUpload(uploadImage);
    }

    private void convertirImagen() {
        if (imageByte != null) {
            imagePreview = new Image(ConvertToImage.convertToStreamImage(imageByte), "");
        }
    }

    private Component createImagen() {
        if (imageByte != null) {
            return (new HorizontalLayout(imagePreview));
        }
        return new Text("");
    }

    private Component createUpload() {
        return uploadImage;
    }

    private void mostrarImagenUpload() {
        if (component != null) {
            remove(component);
            remove(component2);
        }
        component = createImagen();
        component2 = createUpload();

        addComponentAtIndex(6, component);
        addComponentAtIndex(7, component2);
    }

    private void attachImageUpload(Upload upload) {
        MemoryBuffer uploadBuffer = new MemoryBuffer();
        upload.setAcceptedFileTypes("image/*");
        //upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setReceiver((Receiver) uploadBuffer);
        //acción al subir fichero
        upload.addSucceededListener(e -> {
            try {
                imageByte = uploadBuffer.getInputStream().readAllBytes(); //guardamos la imagen en memoria a tipo byte[], de esta forma podremos pasarla a la entidad y grabarla en la BD
            } catch (IOException ex) {
                Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        //máximo tamaño que aceptams en bytes
        upload.setMaxFileSize(199000); //199000 Bytes = 194.34 Kilobytes
        //acción sino puede cargar el fichero
        upload.addFileRejectedListener(event -> {
            Notification.show("¡El fichero es demasiado grande!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
    }

    /**
     * ****************************************************************************
     * Vaadin viene con un sistema de manejo de eventos para componentes. Usado para escuchar eventos de cambio de valor desde la vista
     * principal. Podremos de esta forma llamar a los métodos desde el método superior DistribuidorView
     * *****************************************************************************
     */
//ContactFormEventes una superclase común para todos los eventos. Contiene el contact que fue editado o eliminado.
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

// El addListenermétodo utiliza el bus de eventos de Vaadin para registrar los tipos de eventos personalizados.
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
