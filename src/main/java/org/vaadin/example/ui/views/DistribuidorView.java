package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Component;
import org.vaadin.example.ui.forms.ContactFormDistribuye;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.services.DistribuyeService;
import org.vaadin.example.ui.MainLayout;
import org.vaadin.example.ui.Pagination.DistribuyePagination;

@Route(value = "distribuidores", layout = MainLayout.class)  //si ocultamos no mostrará esta vista en las rutas públicas
@PageTitle("Distribuidores | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class DistribuidorView extends VerticalLayout {

    public static final String VIEW_NAME = "Distribuidores";

    private DistribuyeService distribuyeService;
    private DistribuyePagination pagination;
    //private ListDataProvider<Distribuye> dataProvider;
    private final Grid<Distribuye> grid = new Grid(Distribuye.class);  //creamos grid de tipo distribuye, similar a una tabla
    private final TextField filterText = new TextField();
    private ContactFormDistribuye form; //Crea un campo de tipo formulario

    public DistribuidorView() {

        if (distribuyeService == null) {
            distribuyeService = new DistribuyeService();
        }

        //Damos al componente un nombre de clase CSS
        addClassName("distribuye-view");  //nombre del componente CSS
        setSizeFull(); //le asignamos el máximo tamaño de la ventana
        //Configuramos el grid tabla
        configureGrid();
        //Configuramos el filtro de búsqueda
        configureFilter();

        //Inicializa el formulario en el constructor
        if (form == null) {
            form = new ContactFormDistribuye();
        }

        //Inicializa el constructor Pagination
        if (pagination == null) {
            pagination = new DistribuyePagination(distribuyeService, grid, filterText);
        }

        //Creamos las acciones principales
        form.addListener(ContactFormDistribuye.SaveEvent.class, this::saveContact);
        form.addListener(ContactFormDistribuye.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactFormDistribuye.CloseEvent.class, e -> closeEditor());

        //Crea un Div que envuelve el grid el formDis, le da un nombre de clase CSS y lo convierte en tamaño completo
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        //agrego todos los componentes al VerticalLayout principal
        add(getToolBar(), content, pagination.configurePagination());  //añado componentes y métodos como getToolBar y configurePagination

        //cerramos formularios y otros acciones finales
        closeEditor();
    }

    //Parte de la información para crear el Grid la he sacado de la aplicación Vaadin "Demo Business App"
    private void configureGrid() {
        //dataProvider = DataProvider.ofCollection(distribuyeService.listar(""));
        grid.addClassName("contact-grid"); //añadimos la clase al grid
        grid.setSizeFull(); //ocupamos todo el espacio
        //mostramos las columnas con una función de mostrar listar
        grid.setItems(distribuyeService.listar("")); //sin orden al colocarse
        //grid.setItems(dataProvider.getItems());
        //Borramos todas las columnas para añadirlas manualmente
        grid.removeAllColumns();

        //añadimos las columna y ponemos nombre a cada columna
        //grid.addColumn("idDistribuidor").setHeader("DISTRIBUIDOR");
        grid.addColumn(new ComponentRenderer<>(this::createDistribuidor))
                .setHeader("DISTRIBUIDOR")
                .setSortable(true)
                .setComparator(Distribuye::getIdDistribuidor);

        grid.addColumn(Distribuye::getDireccion).setHeader("DIRECCIÓN");
        grid.addColumn(Distribuye::getCiudad).setHeader("CIUDAD");
        grid.addColumn(Distribuye::getPais).setHeader("PAÍS");

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
        Button button = new Button("Añadir distribuidor", click -> addContact());
        add(button);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveContact(ContactFormDistribuye.SaveEvent evt) {
        String id = evt.getContact().getIdDistribuidor();

        if (distribuyeService.existe(id)) {
            distribuyeService.actualizar(evt.getContact());
        } else {
            distribuyeService.insertar(evt.getContact());
        }
        pagination.updateList();
        closeEditor();
    }

    private void deleteContact(ContactFormDistribuye.DeleteEvent evt) {
        distribuyeService.eliminar(evt.getContact());
        pagination.updateList();
        closeEditor();
    }

    private void editContact(Distribuye distribuye) {
        if (distribuye == null) {
            closeEditor();
        } else {
            form.setContact(distribuye);
            form.setVisible(true);
            addClassName("editing");
        }
        form.EncenderCampo(false);
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Distribuye());
        form.EncenderCampo(true);
    }

    private void closeEditor() {
        pagination.updateList();
        form.setVisible(false);
        removeClassName("editing");
        form.setContact(null);
    }

    private Component createDistribuidor(Distribuye distribuye) {
        HorizontalLayout horizontal = new HorizontalLayout();
        Icon icon = new Icon(VaadinIcon.STAR);
        Label label = new Label(distribuye.getIdDistribuidor());
        horizontal.add(icon, label);
        return horizontal;
    }

}
