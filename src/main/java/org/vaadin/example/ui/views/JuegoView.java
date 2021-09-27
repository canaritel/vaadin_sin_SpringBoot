package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.services.JuegoService;
import org.vaadin.example.ui.MainLayout;
import org.vaadin.example.ui.Pagination.JuegoPagination;

import org.vaadin.example.ui.forms.ContactFormJuego;
import org.vaadin.example.utils.ConvertToImage;

@Route(value = "juegos", layout = MainLayout.class) //si ocultamos no mostrará esta vista en las rutas públicas
@PageTitle("Juegos | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class JuegoView extends VerticalLayout {
    
    public static final String VIEW_NAME = "Juegos";

    private JuegoService juegoService;
    private JuegoPagination pagination;
    private final Grid<Juego> grid = new Grid(Juego.class);  //creamos grid de tipo usuario, similar a una tabla
    //private ListDataProvider<Juego> dataProvider;
    private final TextField filterText = new TextField();
    private ContactFormJuego form; //Crea un campo para el formulario para que pueda acceder a él desde otros métodos más adelante

    private Image image;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public JuegoView() {
        if (juegoService == null) {
            juegoService = new JuegoService();
        }

        //Damos al componente un nombre de clase CSS
        addClassName("juego-view");  //nombre del componente CSS
        setSizeFull(); //le asignamos el máximo tamaño de la ventana
        //Configuramos el grid tabla
        configureGrid();
        //Configuramos el filtro de búsqueda
        configureFilter();

        //Inicializa el formulario en el constructor
        if (form == null) {
            form = new ContactFormJuego();
        }

        //Inicializa el constructor Pagination
        if (pagination == null) {
            pagination = new JuegoPagination(juegoService, grid, filterText);
        }

        // escuchamos los eventos y actuamos
        form.addListener(ContactFormJuego.SaveEvent.class, this::saveContact);
        form.addListener(ContactFormJuego.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactFormJuego.CloseEvent.class, e -> closeEditor());

        //Crea un Div que envuelve el grid el form, le da un nombre de clase CSS y lo convierte en tamaño completo
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        //agrego todos los componentes al diseño principal
        add(getToolBar(), content, pagination.configurePagination());  //añado componentes y métodos como getToolBar y configurePagination

        //cerramos formularios y otros acciones finales
        closeEditor();
    }

    //Parte de la información para crear el Grid la he sacado de la aplicación Vaadin "Demo Business App"
    private void configureGrid() {
        //dataProvider = DataProvider.ofCollection(juegoService.listar(""));
        grid.addClassName("contact-grid"); //añadimos la clase al grid
        grid.setSizeFull(); //ocupamos todo el espacio
        //mostramos las columnas con una función de mostrar listar
        grid.setItems(juegoService.listar("")); //sin orden al colocarse
        //grid.setItems(dataProvider.getItems());
        grid.removeAllColumns();

        //añadimos las columna y ponemos nombre a cada columna
        grid.addColumn(Juego::getTitulo)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("TÍTULO")
                .setSortable(true);

        grid.addColumn(Juego::getSistemaOperativo)
                .setHeader("S.O.")
                .setSortable(true);

        grid.addColumn(bean -> formatter.format(bean.getFechaJuego()))
                .setHeader("FECHA")
                .setSortable(true);

        // Formateamos y añadimos " €" al campo Precio
        grid.addColumn(e -> {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat.setMinimumFractionDigits(2);
            return decimalFormat.format(e.getPrecio()) + " €";
        })
                .setHeader("PRECIO")
                .setTextAlign(ColumnTextAlign.START)
                .setComparator(Comparator.comparing(Juego::getPrecio));

        grid.addColumn(e -> e.getDistribuidor().getIdDistribuidor())
                .setHeader("DISTRIBUIDOR").setSortable(true);

        grid.addColumn(e -> e.getUsuario().getNombre()).
                setHeader("USUARIO").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createImage))
                .setHeader("IMAGEN")
                .setSortable(false)
                .setWidth("150px");

        //ajusta la vista del grid para que los campos puedan leerse más apropiadamente (método general)
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        //activamos en grid tabla un evento que llama a editContact cuando se pulsa en algún registro
        grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));

    }

    private void configureFilter() {
        filterText.setPlaceholder("Filtro de búsqueda...");  //añadimos un texto dentro del textfield
        filterText.setClearButtonVisible(true);  //permitimos borrar facilmente el texto del textfield
        //Establece el modo de cambio de valor para LAZY
        //que el campo de texto le notifique los cambios automáticamente después de un breve tiempo de espera al escribir.
        filterText.setValueChangeMode(ValueChangeMode.LAZY); //método recomendado para los filtros
        //Llama al método updateList siempre que cambia el valor
        filterText.addValueChangeListener(e -> pagination.updateList());
    }

    private HorizontalLayout getToolBar() {
        Button button = new Button("Añadir juego", click -> addContact());
        add(button);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveContact(ContactFormJuego.SaveEvent evt) {
        if (evt.getContact().getIdJuego() == null) {
            juegoService.insertar(evt.getContact());
        } else {
            juegoService.actualizar(evt.getContact());
        }
        pagination.updateList();
        closeEditor();
    }

    private void deleteContact(ContactFormJuego.DeleteEvent evt) {
        juegoService.eliminar(evt.getContact());
        pagination.updateList();
        closeEditor();
    }

    private void editContact(Juego juego) {
        if (juego == null) {
            closeEditor();
        } else {
            form.setContact(juego);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Juego());
    }

    private void closeEditor() {
        pagination.updateList();
        form.setContact(null);  // Establece el contacto del formulario en null, borrando los valores antiguos.
        form.setVisible(false); // Oculta el formulario.
        removeClassName("editing"); //Elimina la "editing" clase CSS de la vista.

    }

    private Component createImage(Juego juego) {
        if (juego.getImagen() == null) {
            image = new Image("images/Imagen-no-disponible.png", "null");
        } else {
            image = new Image(ConvertToImage.convertToStreamImage(juego.getImagen()), "");
        }
        image.setHeight(30, Unit.PIXELS);
        return image;
    }

}
