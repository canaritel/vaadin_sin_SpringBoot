package org.vaadin.example.ui.Pagination;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.example.entities.Distribuye;
import org.vaadin.example.services.DistribuyeService;

public class DistribuyePagination extends HorizontalLayout {

    private DistribuyeService distribuyeService = new DistribuyeService();
    private Grid<Distribuye> grid = new Grid(Distribuye.class);
    private TextField filterText = new TextField();
    //Inicializamos botones para la Paginación
    private final Button buttonInicio = new Button();
    private final Button buttonAnterior = new Button();
    private final Button buttonPagina = new Button();
    private final Button buttonSiguiente = new Button();
    private final Button buttonFin = new Button();
    private final ComboBox<String> valueComboBox = new ComboBox<>();
    //Inicializamos variables para la Paginación
    private double totalPagina;
    private int totalPaginas;
    private int totalRegistros;
    private int itemsPagina;
    private int numeroPagina;

    public DistribuyePagination() {
    }

    public DistribuyePagination(DistribuyeService distribuyeService, Grid grid, TextField filterText) {
        //Inicializamos las variables y parámetros
        inicializeVar();
        this.distribuyeService = distribuyeService;
        this.grid = grid;
        this.filterText = filterText;
        //Calculamos los datos
        datosPagination();
        //creamos las acciones para los botones y comboBox de la paginación
        createListener();
    }

    private void inicializeVar() {
        totalPagina = 0;
        totalPaginas = 0;
        totalRegistros = 0;
        itemsPagina = 15;
        numeroPagina = 0;
    }

    private void datosPagination() {
        totalRegistros = distribuyeService.total(); //guardamos el total de registros
        totalPagina = (double) totalRegistros / itemsPagina;
        totalPaginas = (int) Math.ceil(totalPagina); //redondeamos hacia arriba
    }

    public HorizontalLayout configurePagination() {
        //creamos los iconos para los botones
        buttonInicio.setIcon(VaadinIcon.BACKWARDS.create());
        buttonAnterior.setIcon(VaadinIcon.ANGLE_LEFT.create());
        buttonPagina.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
        buttonSiguiente.setIcon(VaadinIcon.ANGLE_RIGHT.create());
        buttonFin.setIcon(VaadinIcon.FORWARD.create());

        //creamos los componentes HorizontalLayout
        HorizontalLayout toolpagination = new HorizontalLayout();
        HorizontalLayout horizontalItems = new HorizontalLayout(configureItems());
        HorizontalLayout horizontalCount = new HorizontalLayout(configureCount());
        HorizontalLayout horizontalPagination = new HorizontalLayout(buttonInicio, buttonAnterior, buttonPagina,
                                                                     buttonSiguiente, buttonFin);

        //asignamos el ancho total a los componentes creados. Importante para luego poder alinearlos
        toolpagination.setWidthFull();
        horizontalItems.setWidthFull();
        horizontalPagination.setWidthFull();
        horizontalCount.setWidthFull();

        //alineamos los componentes
        horizontalCount.setJustifyContentMode(JustifyContentMode.END);
        toolpagination.setAlignSelf(Alignment.END, horizontalCount);
        toolpagination.setAlignSelf(Alignment.CENTER, horizontalPagination);
        toolpagination.setAlignSelf(Alignment.START, horizontalItems);

        //añadimos todos los componentes para la Paginación
        toolpagination.add(horizontalItems, horizontalPagination, horizontalCount);

        return toolpagination;
    }

    private HorizontalLayout configureItems() {
        valueComboBox.setItems("5", "15", "30", "50");
        valueComboBox.setValue("15");

        return new HorizontalLayout(valueComboBox);
    }

    private HorizontalLayout configureCount() {
        Label label = new Label();
        label.setText(totalRegistros + " registros");
        label.setWidth("100px");
        label.setHeight("35px");

        return new HorizontalLayout(label);
    }

    private void createListener() {
        valueComboBox.addValueChangeListener(e -> {
            numeroPagina = 0;
            itemsPagina = Integer.valueOf(e.getValue());
            datosPagination();
            buttonPagina.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            updateList();
        });

        buttonInicio.addClickListener(e -> {
            if ((numeroPagina + 1) <= 1) {
                return;
            } else {
                numeroPagina = 0;
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, numeroPagina));
                buttonPagina.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });

        buttonAnterior.addClickListener(e -> {
            if ((numeroPagina + 1) <= 1) {
                return;
            } else {
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, --numeroPagina));
                buttonPagina.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });

        buttonSiguiente.addClickListener(e -> {
            if ((numeroPagina + 1) >= totalPaginas) {
                return;
            } else {
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, ++numeroPagina));
                buttonPagina.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });

        buttonFin.addClickListener(e -> {
            if ((numeroPagina + 1) >= totalPaginas) {
                return;
            } else {
                numeroPagina = totalPaginas - 1;
                grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, numeroPagina));
                buttonPagina.setText("Página " + (numeroPagina + 1) + " de " + totalPaginas);
            }
        });
    }

    public void updateList() {
        grid.setItems(distribuyeService.listarPagination(filterText.getValue(), false, itemsPagina, numeroPagina));
    }

}
