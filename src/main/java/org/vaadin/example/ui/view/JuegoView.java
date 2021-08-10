package org.vaadin.example.ui.view;

import org.vaadin.example.ui.form.ContactFormUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.text.SimpleDateFormat;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.services.JuegoService;
import org.vaadin.example.ui.MainLayout;

@Route(value = "juegos", layout = MainLayout.class)
@PageTitle("Juegos | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class JuegoView extends VerticalLayout {

    private JuegoService juegoService;

    private final Grid<Juego> grid = new Grid(Juego.class);  //creamos grid de tipo usuario, similar a una tabla
    private final TextField filterText = new TextField();
    private final ContactFormUser form; //Crea un campo para el formulario para que pueda acceder a él desde otros métodos más adelante

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public JuegoView() {
        if (juegoService == null) {
            juegoService = new JuegoService();
        }

        //Le da al componente un nombre de clase CSS
        addClassName("juego-view");  //nombre del componente CSS
        setSizeFull();
        configureGrid();
        configureFilter();

        //Inicializa el formulario en el constructor
        form = new ContactFormUser();
        //form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        //form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        //form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());

        //Crea un Div que envuelve el grid el form, le da un nombre de clase CSS y lo convierte en tamaño completo
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        //agrego todos los componentes al diseño principal
        add(getToolBar(), content); //añado no solo componentes sino métodos como getToolBar
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("contact-grid"); //añadimos la clase al grid
        grid.setSizeFull(); //ocupamos todo el espacio
        //mostramos las columnas con una función de mostrar listar
        grid.setItems(juegoService.listar("")); //sin orden al colocarse
        grid.removeAllColumns();

        //añadimos las columna y ponemos nombre a cada columna
        grid.addColumn("titulo").setHeader("TÍTULO");
        grid.addColumn("sistemaOperativo").setHeader("S.O.");
        //grid.addColumn("fechaJuego").setHeader("Fecha");
        grid.addColumn(bean -> formatter.format(bean.getFechaJuego()))
                .setHeader("FECHA").setSortable(true);
        grid.addColumn("precio").setHeader("PRECIO");
        grid.addColumn(e -> e.getDistribuidor().getIdDistribuidor())
                .setHeader("DISTRIBUIDOR").setSortable(true);
        grid.addColumn(e -> e.getUsuario().getNombre()).
                setHeader("USUARIO").setSortable(true);

        //ajusta la vista del grid para que los campos puedan leerse más apropiadamente (método general)
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        //activamos en grid tabla un evento que llama a editContact cuando se pulsa en algún registro
        grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));

    }

    private void configureFilter() {
        filterText.setPlaceholder("Filtro de búsqueda...");  //añadimos un texto dentro del textfield
        filterText.setClearButtonVisible(true);  //permitimos borrar facilmente el texto del textfield
        //se activa cuando escribimos algo y pasa un  corto espacio de tiempo (al terminar de escribir)
        filterText.setValueChangeMode(ValueChangeMode.LAZY); //método recomendado para los filtros
        filterText.addValueChangeListener(e -> updateList());
    }

    private HorizontalLayout getToolBar() {
        Button button = new Button("Añadir juego", click -> addContact());
        add(button);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveContact(ContactFormUser.SaveEvent evt) {
        if (evt.getContact().getIdUsuario() == null) {
            //     juegoService.insertar(evt.getContact());
        } else {
            //     usuarioService.actualizar(evt.getContact());
        }
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactFormUser.DeleteEvent evt) {
        //juegoService.eliminar(evt.getContact());
        updateList();
        closeEditor();
    }

    private void editContact(Juego juego) {
        if (juego == null) {
            closeEditor();
        } else {
            //form.setContact(juego);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Juego());
    }

    private void closeEditor() {
        updateList();
        form.setVisible(false);
        removeClassName("editing");
        form.setContact(null);
    }

    private void updateList() {
        grid.setItems(juegoService.listar(filterText.getValue()));
    }

}
