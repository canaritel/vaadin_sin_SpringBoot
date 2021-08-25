package org.vaadin.example.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.validator.BigDecimalRangeValidator;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;

import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.services.DistribuyeService;
import org.vaadin.example.services.UsuarioService;
import org.vaadin.example.utils.ConvertToImage;
import org.vaadin.example.utils.FileUpload;

public class ContactFormJuego122 extends FormLayout {

    private final TextField textTitulo = new TextField("Título");
    private final RadioButtonGroup<String> checkboxSistema = new RadioButtonGroup<>();
    private final DatePicker datePicker = new DatePicker();
    private final BigDecimalField bigDecimalField = new BigDecimalField("Precio de compra");
    //private final TextField textImagen = new TextField("Imagen");
    private final ComboBox<Distribuye> comboDistribuidor = new ComboBox<>();
    private final ComboBox<Usuario> comboUsuario = new ComboBox<>();
    public byte[] imageByte = new byte[65535];
    public Image image = new Image();

    public MemoryBuffer buffer = new MemoryBuffer();
    public Upload upload = new Upload(buffer);
    public Div output = new Div();

    private final Button save = new Button("Grabar");
    private final Button delete = new Button("Eliminar");
    private final Button close = new Button("Cancelar");

    private final DistribuyeService distribuyeService = new DistribuyeService();
    private final UsuarioService usuarioService = new UsuarioService();

    private List<Distribuye> listDistribuye = new ArrayList<>();
    private List<Usuario> listUsuario = new ArrayList<>();

    private final Binder<Juego> binder = new Binder<>(Juego.class);

    private Component component;

    public ContactFormJuego122() {
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

        binder.bindInstanceFields(imageByte);
        //binder.bindInstanceFields(comboUsuario);
        //binder.bindInstanceFields(comboDistribuidor);
        //añado los componentes a la vista
        add(textTitulo, checkboxSistema, datePicker, bigDecimalField, comboDistribuidor, comboUsuario, upload, output);
        add(layout); //añado la línea verticaly así poner los botones debajo
        add(createButtonsLayout());

    }

    public void setContact(Juego juego) {
        binder.setBean(juego);
        if (juego != null) {
            imageByte = juego.getImagen();
            mostrarImagen();
        }
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

    private void crearFileImagen() {

        //FileUpload.createSimpleUploadImage(buffer, upload, imageByte, image);
        //Covertimos la imagen en un objeto de tipo byte[]
        // imageByte = getImageAsByteArray(); ///////////
        //Enviamos imagen al objeto binder
        //if (binder.getBean() != null) {
        //    binder.getBean().setImagen(imageByte);
        //}
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream());
            output.removeAll();
            showOutput(event.getFileName(), component, output);
        });

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            output.removeAll();
            showOutput(event.getErrorMessage(), component, output);
        });

        upload.getElement().addEventListener("file-remove", event -> {
                                         output.removeAll();
                                     });

    }

    private Component createComponent(String mimeType, String fileName, InputStream stream) {
        if (mimeType.startsWith("text")) {
            return createTextComponent(stream);
        } else if (mimeType.startsWith("image")) {

            try {

                byte[] bytes = IOUtils.toByteArray(stream);
                image.getElement().setAttribute("src", new StreamResource(
                                                fileName, () -> new ByteArrayInputStream(bytes)));
                try (ImageInputStream in = ImageIO.createImageInputStream(
                        new ByteArrayInputStream(bytes))) {
                    final Iterator<ImageReader> readers = ImageIO
                            .getImageReaders(in);
                    if (readers.hasNext()) {
                        ImageReader reader = readers.next();
                        try {
                            reader.setInput(in);
                            //image.setWidth(reader.getWidth(0) + "px");
                            //image.setHeight(reader.getHeight(0) + "px");
                            image.setHeight("70px");
                        } finally {
                            reader.dispose();
                        }
                    }
                }
                //Pasamos la imagen a tipo byte
                imageByte = bytes;

            } catch (IOException e) {
                e.printStackTrace();
            }
            //Enviamos imagen al objeto binder
            if (binder.getBean() != null) {
                binder.getBean().setImagen(imageByte);
            }

            return image;
        }
        Div content = new Div();
        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
                                    mimeType, MessageDigestUtil.sha256(stream.toString()));
        content.setText(text);
        return content;
    }

    private Component createTextComponent(InputStream stream) {
        String text;
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return new Text(text);
    }

    private byte[] getImageAsByteArray() {
        try {
            imageByte = IOUtils.toByteArray(buffer.getInputStream());
            return imageByte;
        } catch (IOException ex) {
            Logger.getLogger(ContactFormJuego122.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void showOutput(String text, Component content,
                            HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
        outputContainer.add(content);
    }

    private Component createImagen() {
        if (imageByte != null) {
            image = new Image(ConvertToImage.convertToStreamImage(imageByte), "");
            return (new HorizontalLayout(image));
        }
        return new Text("hola");
    }

    private void mostrarImagen() {
        if (component != null) {
            remove(component);
        }
        component = createImagen();
        addComponentAtIndex(7, component);
    }

    /**
     * ****************************************************************************
     * Vaadin viene con un sistema de manejo de eventos para componentes. Usado para escuchar eventos de cambio de valor desde la vista
     * principal. Podremos de esta forma llamar a los métodos desde el método superior DistribuidorView
     * *****************************************************************************
     */
    //ContactFormEventes una superclase común para todos los eventos. Contiene el contact que fue editado o eliminado.
    public static abstract class ContactFormEvent extends ComponentEvent<ContactFormJuego122> {

        private final Juego juego;

        public ContactFormEvent(ContactFormJuego122 source, Juego juego) {
            super(source, false);
            this.juego = juego;
        }

        public Juego getContact() {
            return juego;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactFormJuego122 source, Juego juego) {
            super(source, juego);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactFormJuego122 source, Juego juego) {
            super(source, juego);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactFormJuego122 source) {
            super(source, null);
        }
    }

    // El addListenermétodo utiliza el bus de eventos de Vaadin para registrar los tipos de eventos personalizados.
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
