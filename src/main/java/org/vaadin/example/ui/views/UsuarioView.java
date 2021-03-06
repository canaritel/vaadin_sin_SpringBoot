package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Component;
import org.vaadin.example.ui.forms.ContactFormUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.vaadin.example.services.UsuarioService;
import org.vaadin.example.entities.Usuario;
import org.vaadin.example.ui.MainLayout;
import org.vaadin.example.ui.Pagination.UsuarioPagination;

@Route(value = "usuarios", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class) //permite que la ruta por defecto acceda a esta clase
@PageTitle("Usuarios | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
//Para más información mirar: https://vaadin.com/docs/v14/flow/routing/tutorial-routing-exception-handling
public class UsuarioView extends VerticalLayout implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Usuarios";

    private UsuarioService usuarioService;
    private UsuarioPagination pagination;
    //PaginatedGrid<Usuario> grid = new PaginatedGrid<>(Usuario.class);
    private final Grid<Usuario> grid = new Grid(Usuario.class);  //creamos grid de tipo usuario, similar a una tabla
    private final TextField filterText = new TextField();
    private ContactFormUser form; //Crea un campo para el formulario para que pueda acceder a él desde otros métodos más adelante

    public UsuarioView() {
        if (usuarioService == null) {
            usuarioService = new UsuarioService();
        }

        //Le da al componente un nombre de clase CSS
        addClassName("list-view");  //nombre del componente CSS
        setSizeFull(); //le asignamos el máximo tamaño de la ventana
        //Configuramos el grid tabla
        configureGrid();
        //Configuramos el filtro de búsqueda
        configureFilter();

        //Inicializa el formulario en el constructor
        if (form == null) {
            form = new ContactFormUser();
        }

        //Inicializa el constructor Pagination
        if (pagination == null) {
            pagination = new UsuarioPagination(usuarioService, grid, filterText);
        }

        //Creamos las acciones principales
        form.addListener(ContactFormUser.SaveEvent.class, this::saveContact);
        form.addListener(ContactFormUser.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactFormUser.CloseEvent.class, e -> closeEditor());

        //Crea un Div que envuelve el grid y el form, le da un nombre de clase CSS y lo convierte en tamaño completo
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        //agrego todos los componentes al diseño principal
        add(getToolBar(), content, pagination.configurePagination());  //añado componentes y métodos como getToolBar y configurePagination

        //cerramos formularios y otros acciones finales
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("contact-grid"); //añadimos la clase al grid
        grid.setSizeFull(); //ocupamos todo el espacio

        //mostramos las columnas
        grid.setItems(usuarioService.listar("")); //sin orden al colocarse

        grid.removeAllColumns(); //borramos todas las columnas
        //grid.setColumns("idUsuario", "nombre", "apellidos", "edad", "telefono", "activo");
        //grid.removeColumnByKey("idUsuario");
        //grid.removeColumnByKey("telefono"); //eliminamos la columna telefono
        //grid.removeColumnByKey("activo"); //eliminamos la columna telefono

        //grid.addColumn(Usuario::getNombre).setHeader("NOMBRE");
        grid.addColumn(new ComponentRenderer<>(this::createUser))
                .setHeader("NOMBRE")
                .setSortable(true)
                .setComparator(Usuario::getNombre);

        grid.addColumn(Usuario::getApellidos)
                .setSortable(true)
                .setHeader("APELLIDOS");

        grid.addColumn(Usuario::getEdad)
                .setSortable(true)
                .setHeader("EDAD");

        //añadimos la columna telefono modificando datos de la misma
        grid.addColumn(e -> {
            if (e.getTelefono().startsWith("9") || e.getTelefono().startsWith("8")
                    || e.getTelefono().startsWith("6") || e.getTelefono().startsWith("7")) {
                String datousuario = "(+34) " + e.getTelefono();
                return datousuario;
            } else {
                return e.getTelefono();
            }
        })
                .setSortable(true)
                .setHeader("TELÉFONO");

        //añadimos la columna ACTIVO modificando datos de la misma
        //para añadir componentes gráficos tipo image/icon se deberá usar addComponentColumn o ComponentRenderer
        grid.addColumn(new ComponentRenderer<>(this::createIcono))
                .setHeader("ACTIVO")
                .setSortable(true)
                .setWidth("150px");
        //cuando añadimos un elemento no gráfico se usa el método de abajo
        /*
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
         */

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
        //si detecta un cambio en el campo filerText se activa
        filterText.addValueChangeListener(e -> pagination.updateList());
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
        pagination.updateList();
        closeEditor();
    }

    private void deleteContact(ContactFormUser.DeleteEvent evt) {
        usuarioService.eliminar(evt.getContact());
        pagination.updateList();
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
        pagination.updateList();
        form.setVisible(false);
        removeClassName("editing");
        form.setContact(null);
    }

    private Component createIcono(Usuario usuario) {
        Boolean datousuario = usuario.getActivo();
        Icon icon;
        if (datousuario) {
            icon = new Icon(VaadinIcon.CHECK);
            icon.setColor("green");
        } else {
            icon = new Icon(VaadinIcon.CLOSE);
            icon.setColor("grey");
        }
        return icon;
    }

    private Component createUser(Usuario usuario) {
        HorizontalLayout horizontal = new HorizontalLayout();
        Icon icon = new Icon(VaadinIcon.USER);
        Label label = new Label(usuario.getNombre());
        horizontal.add(icon, label);
        return horizontal;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        //Hacemos que cualquier dirección web erronea por defecto sea redirigida a la raiz ""
    }
}
