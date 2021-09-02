package org.vaadin.example.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class GridPagination {
/*
    public static Button buttonInicio;
    public static Button buttonAnterior;
    public static Button buttonPagina;
    public static Button buttonSiguiente;
    public static Button buttonFin;
    public static ComboBox<String> valueComboBox;

    public static double totalPagina = 0;
    public static int totalPaginas = 0;
    public static int totalRegistros = 0;
    public static int itemsPagina = 15;
    public static int numeroPagina = 0;
/*
    //private static final DistribuyeService distribuyeService = new DistribuyeService();
    public static void datosPaginacion(int total) {
        totalRegistros = total;
        //totalRegistros = distribuyeService.total(); //guardamos el total de registros
        totalPagina = (double) totalRegistros / itemsPagina;
        totalPaginas = (int) Math.ceil(totalPagina); //redondeamos hacia arriba y convertimos en int
    }

    public static HorizontalLayout configureItems() {
        if (valueComboBox == null) {
            valueComboBox = new ComboBox<>();
        }
        valueComboBox.setItems("5", "15", "30", "50");
        valueComboBox.setValue("15");

        HorizontalLayout headerItems = new HorizontalLayout();
        headerItems.add(valueComboBox);

        return headerItems;
    }

    public static HorizontalLayout configureCount() {
        Label label = new Label();
        label.setText(GridPagination.totalRegistros + " registros");
        label.setWidth("100px");
        label.setHeight("35px");

        return new HorizontalLayout(label);
    }

    public static HorizontalLayout configurePagination() {
        if (buttonInicio == null) {
            buttonInicio = new Button();
        }
        if (buttonAnterior == null) {
            buttonAnterior = new Button();
        }
        if (buttonPagina == null) {
            buttonPagina = new Button();
        }
        if (buttonSiguiente == null) {
            buttonSiguiente = new Button();
        }
        if (buttonFin == null) {
            buttonFin = new Button();
        }

        HorizontalLayout toolpagination = new HorizontalLayout();

        GridPagination.buttonInicio.setIcon(VaadinIcon.BACKWARDS.create());
        GridPagination.buttonAnterior.setIcon(VaadinIcon.ANGLE_LEFT.create());
        GridPagination.buttonPagina.setText("Página " + (GridPagination.numeroPagina + 1) + " de " + GridPagination.totalPaginas);
        GridPagination.buttonSiguiente.setIcon(VaadinIcon.ANGLE_RIGHT.create());
        GridPagination.buttonFin.setIcon(VaadinIcon.FORWARD.create());

        HorizontalLayout horizontalItems = new HorizontalLayout(GridPagination.configureItems());
        HorizontalLayout horizontalCount = new HorizontalLayout(GridPagination.configureCount());
        HorizontalLayout horizontalPagination
                = new HorizontalLayout(GridPagination.buttonInicio, GridPagination.buttonAnterior, GridPagination.buttonPagina,
                                       GridPagination.buttonSiguiente, GridPagination.buttonFin);

        toolpagination.setWidthFull();
        horizontalItems.setWidthFull();
        horizontalPagination.setWidthFull();
        horizontalCount.setWidthFull();

        horizontalCount.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        toolpagination.setAlignSelf(FlexComponent.Alignment.END, horizontalCount);
        toolpagination.setAlignSelf(FlexComponent.Alignment.CENTER, horizontalPagination);
        toolpagination.setAlignSelf(FlexComponent.Alignment.START, horizontalItems);

        toolpagination.add(horizontalItems, horizontalPagination, horizontalCount);

        //createListener();////////////////////////////////////////
        return toolpagination;
    }
*/
}
