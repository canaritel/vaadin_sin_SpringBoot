package org.vaadin.example.ui;

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
import org.vaadin.example.entities.Juego;
import org.vaadin.example.services.UsuarioService;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.services.JuegoService;

@Route(value = "juegos", layout = MainLayout.class)
@PageTitle("Juegos | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class JuegoView extends VerticalLayout {

    private JuegoService juegoService;

    private final Grid<Juego> grid = new Grid(Juego.class);  //creamos grid de tipo usuario, similar a una tabla
    private final TextField filterText = new TextField();
    private final ContactForm form; //Crea un campo para el formulario para que pueda acceder a él desde otros métodos más adelante

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
        form = new ContactForm();
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
        grid.addColumn("titulo").setHeader("Título");
        grid.addColumn("sistema_operativo").setHeader("S.O.");
        grid.addColumn("fecha_juego").setHeader("Fecha");
        grid.addColumn("precio").setHeader("Precio");

        //ajusta la vista del grid para que los campos puedan leerse más apropiadamente (método general)
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        //activamos en grid tabla un evento que llama a editContact
        //  grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));

    }

    private void configureFilter() {
        filterText.setPlaceholder("Filtrar por nombre...");  //añadimos un texto dentro del textfield
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

    private void saveContact(ContactForm.SaveEvent evt) {
        if (evt.getContact().getIdUsuario() == null) {
            //     juegoService.insertar(evt.getContact());
        } else {
            //     usuarioService.actualizar(evt.getContact());
        }
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent evt) {
        //juegoService.eliminar(evt.getContact());
        updateList();
        closeEditor();
    }

    private void editContact(Usuario usuario) {
        if (usuario == null) {
            closeEditor();
        } else {
            form.setContact(usuario);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Usuario());
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
