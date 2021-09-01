package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Component;
import org.vaadin.example.ui.forms.ContactFormDistribuye;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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

@Route(value = "distribuidores", layout = MainLayout.class)
@PageTitle("Distribuidores | Vaadin CRM")
@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class DistribuidorView extends VerticalLayout {

    private DistribuyeService distribuyeService;
    //private ListDataProvider<Distribuye> dataProvider;
    private final Grid<Distribuye> grid = new Grid(Distribuye.class);  //creamos grid de tipo distribuye, similar a una tabla
    private final TextField filterText = new TextField();
    private ContactFormDistribuye form; //Crea un campo para el formulario para que pueda acceder a él desde otros métodos más adelante

    private final Button button1 = new Button();
    private final Button button2 = new Button();
    private final Button button3 = new Button();
    private final Button button4 = new Button();
    private final Button button5 = new Button();
    private final ComboBox<String> valueComboBox = new ComboBox<>();

    private double totalPagina = 0;
    private int totalPaginas = 0;
    private int totalRegistros = 0;
    private int itemsPagina = 15;
    private int numeroPagina = 0;

    public DistribuidorView() {
        if (distribuyeService == null) {
            distribuyeService = new DistribuyeService();
        }

        //Damos al componente un nombre de clase CSS
        addClassName("distribuye-view");  //nombre del componente CSS
        setSizeFull();
        configureGrid();
        configureFilter();

        //Inicializa el formulario en el constructor
        if (form == null) {
            form = new ContactFormDistribuye();
        }
        form.addListener(ContactFormDistribuye.SaveEvent.class, this::saveContact);
        form.addListener(ContactFormDistribuye.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactFormDistribuye.CloseEvent.class, e -> closeEditor());

        //Crea un Div que envuelve el grid el formDis, le da un nombre de clase CSS y lo convierte en tamaño completo
        Div content = new Div(grid, form);
        content.addClassName("content");  ////
        content.setSizeFull();

        //agrego todos los componentes al diseño principal
        add(getToolBar(), content, configurePagination()); //añado no solo componentes sino métodos como getToolBar y configurePagination

        updateList();
        closeEditor();
    }

    private void configureGrid() {
        //dataProvider = DataProvider.ofCollection(distribuyeService.listar(""));
        grid.setWidthFull();
        grid.addClassName("contact-grid"); //añadimos la clase al grid
        grid.setSizeFull(); //ocupamos todo el espacio
        //mostramos las columnas con una función de mostrar listar
        grid.setItems(distribuyeService.listar("")); //sin orden al colocarse

        datosPaginacion();

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

        // Sets the max number of items to be rendered on the grid for each page
        //grid.setPageSize(15);  //todavía no se como implementarlo, no funciona 
        //activamos en grid tabla un evento que llama a editContact cuando se pulsa en algún registro
        grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));

    }

    private void datosPaginacion() {
        totalRegistros = distribuyeService.total(); //guardamos el total de registros
        totalPagina = (double) totalRegistros / itemsPagina;
        totalPaginas = (int) Math.ceil(totalPagina); //redondeamos hacia arriba y convertimos en int
    }

    private Component createDistribuidor(Distribuye distribuye) {
        HorizontalLayout horizontal = new HorizontalLayout();
        Icon icon = new Icon(VaadinIcon.STAR);
        Label label = new Label(distribuye.getIdDistribuidor());
        horizontal.add(icon, label);
        return horizontal;
    }

    private void configureFilter() {
        filterText.setPlaceholder("Filtro de búsqueda...");  //añadimos un texto dentro del textfield
        filterText.setClearButtonVisible(true);  //permitimos borrar facilmente el texto del textfield
        //se activa cuando escribimos algo y pasa un  corto espacio de tiempo (al terminar de escribir)
        filterText.setValueChangeMode(ValueChangeMode.LAZY); //método recomendado para los filtros
        filterText.addValueChangeListener(e -> updateList());
    }

    private HorizontalLayout configurePagination() {
        HorizontalLayout toolpagination = new HorizontalLayout();

        button1.setIcon(VaadinIcon.BACKWARDS.create());
        button2.setIcon(VaadinIcon.ANGLE_LEFT.create());
        button3.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
        button4.setIcon(VaadinIcon.ANGLE_RIGHT.create());
        button5.setIcon(VaadinIcon.FORWARD.create());

        HorizontalLayout horizontalItems = new HorizontalLayout(configureItems());

        HorizontalLayout horizontalCount = new HorizontalLayout();
        Label label = new Label();
        label.setText(totalRegistros + " registros");
        label.setWidth("100px");
        label.setHeight("35px");
        horizontalCount.add(label);

        HorizontalLayout horizontalPagination = new HorizontalLayout();
        horizontalPagination.add(button1, button2, button3, button4, button5);

        toolpagination.setWidthFull();
        horizontalItems.setWidthFull();
        horizontalPagination.setWidthFull();
        horizontalCount.setWidthFull();

        horizontalCount.setJustifyContentMode(JustifyContentMode.END);
        toolpagination.setAlignSelf(Alignment.END, horizontalCount);
        toolpagination.setAlignSelf(Alignment.CENTER, horizontalPagination);
        toolpagination.setAlignSelf(Alignment.START, horizontalItems);

        toolpagination.add(horizontalItems, horizontalPagination, horizontalCount);

        createListener();

        return toolpagination;
    }

    private HorizontalLayout configureItems() {
        valueComboBox.setItems("5", "15", "30", "50");
        valueComboBox.setValue("15");

        HorizontalLayout headerItems = new HorizontalLayout();
        headerItems.add(valueComboBox);

        return headerItems;
    }

    private void createListener() {
        valueComboBox.addValueChangeListener(e -> {
            itemsPagina = Integer.valueOf(e.getSource().getValue());
            datosPaginacion();
            button3.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            updateList();
        });

        button1.addClickListener(e -> {
            if ((numeroPagina + 1) <= 1) {
                return;
            } else {
                numeroPagina = 0;
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, numeroPagina));
                button3.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });

        button2.addClickListener(e -> {
            if ((numeroPagina + 1) <= 1) {
                return;
            } else {
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, --numeroPagina));
                button3.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });

        button4.addClickListener(e -> {
            if ((numeroPagina + 1) >= totalPaginas) {
                return;
            } else {
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, ++numeroPagina));
                button3.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });

        button5.addClickListener(e -> {
            if ((numeroPagina + 1) >= totalPaginas) {
                return;
            } else {
                numeroPagina = totalPaginas - 1;
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, numeroPagina));
                button3.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });
    }

    private HorizontalLayout getToolBar() {
        Button button = new Button("Añadir distribuidor", click -> addContact());
        add(button);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, button);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, numeroPagina));
    }

    private void saveContact(ContactFormDistribuye.SaveEvent evt) {
        String id = evt.getContact().getIdDistribuidor();

        if (distribuyeService.existe(id)) {
            distribuyeService.actualizar(evt.getContact());
        } else {
            distribuyeService.insertar(evt.getContact());
        }
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactFormDistribuye.DeleteEvent evt) {
        distribuyeService.eliminar(evt.getContact());
        updateList();
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
        updateList();
        form.setVisible(false);
        removeClassName("editing");
        form.setContact(null);
    }

}
