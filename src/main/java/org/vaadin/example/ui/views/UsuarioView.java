package org.vaadin.example.ui.views;

import org.vaadin.example.ui.forms.ContactFormUser;
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
import org.vaadin.example.services.UsuarioService;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.ui.MainLayout;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Usuarios | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class UsuarioView extends VerticalLayout {

    private UsuarioService usuarioService;

    private final Grid<Usuario> grid = new Grid(Usuario.class);  //creamos grid de tipo usuario, similar a una tabla
    private final TextField filterText = new TextField();
    private final ContactFormUser form; //Crea un campo para el formulario para que pueda acceder a él desde otros métodos más adelante

    public UsuarioView() {
        if (usuarioService == null) {
            usuarioService = new UsuarioService();
        }

        //Le da al componente un nombre de clase CSS
        addClassName("list-view");  //nombre del componente CSS
        setSizeFull();
        configureGrid();
        configureFilter();

        //Inicializa el formulario en el constructor
        form = new ContactFormUser();
        form.addListener(ContactFormUser.SaveEvent.class, this::saveContact);
        form.addListener(ContactFormUser.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactFormUser.CloseEvent.class, e -> closeEditor());

        //Crea un Div que envuelve el grid y el form, le da un nombre de clase CSS y lo convierte en tamaño completo
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
        //mostramos las columnas
        grid.setItems(usuarioService.listar("")); //sin orden al colocarse
        grid.removeAllColumns();
        //grid.setColumns("idUsuario", "nombre", "apellidos", "edad", "telefono", "activo");
        //grid.removeColumnByKey("idUsuario");
        //grid.removeColumnByKey("telefono"); //eliminamos la columna telefono
        //grid.removeColumnByKey("activo"); //eliminamos la columna telefono
        //añadimos la columna telefono modificando datos de la misma
        grid.addColumn("nombre").setHeader("NOMBRE");
        grid.addColumn("apellidos").setHeader("APELLIDOS");
        grid.addColumn("edad").setHeader("EDAD");
        grid.addColumn("telefono").setHeader("TELÉFONO");
        //grid.addColumn("activo").setHeader("ACTIVO");
        //grid.addColumn(e -> {
        //    String datousuario = "(+34) " + e.getTelefono();
        //    return datousuario;
        //}).setHeader("TELÉFONO");
        //añadimos la columna activo modificando datos de la misma

        grid.addColumn(e -> {
            Boolean datousuario = e.getActivo();
            String activado;
            if (datousuario) {
                activado = "SI";
            } else {
                activado = "NO";
            }
            return activado;
        }).setHeader("ACTIVO").setSortable(true);

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
        Button button = new Button("Añadir usuario", click -> addContact());
        add(button);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveContact(ContactFormUser.SaveEvent evt) {
        if (evt.getContact().getIdUsuario() == null) {
            usuarioService.insertar(evt.getContact());
        } else {
            usuarioService.actualizar(evt.getContact());
        }
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactFormUser.DeleteEvent evt) {
        usuarioService.eliminar(evt.getContact());
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
        grid.setItems(usuarioService.listar(filterText.getValue()));
    }

}
