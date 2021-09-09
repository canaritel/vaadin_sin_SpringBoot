package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.services.JuegoService;
import org.vaadin.example.ui.MainLayout;
import org.vaadin.example.utils.ConvertToImage;

@Route(value = "listado", layout = MainLayout.class)
@PageTitle("Listado | Vaadin CRM")
@CssImport("./styles/shared-styles.css")
public class ListadoView extends FlexLayout {

    private JuegoService juegoService;
    private List<Juego> listado = new ArrayList<>();

    public ListadoView() {
        if (juegoService == null) {
            juegoService = new JuegoService();
        }

        addClassName("listado-horizontal");  //nombre del componente CSS
        VerticalLayout vertical = new VerticalLayout();

        createSeleccion();
        //cargamos los objetos en nuestro ArrayList
        updateList();

        add(vertical);

        //mostramos los componentes que forman el listado
        for (int i = 0; i < listado.size(); i++) {
            add(createListado(i));
        }
    }

    private void updateList() {
        listado = juegoService.listar("");
    }

    private VerticalLayout createListado(int index) {
        VerticalLayout vertical = new VerticalLayout();
        vertical.addClassName("listado-hijo");
        vertical.setWidth("auto");
        vertical.setAlignItems(Alignment.CENTER);

        Juego juego = (Juego) listado.get(index);

        H3 label = new H3(juego.getTitulo());
        Label label2 = new Label(" " + juego.getSistemaOperativo());
        Label label3 = new Label(" " + juego.getUsuario().getNombre());
        Label label4 = new Label(" " + juego.getDistribuidor().getIdDistribuidor());
        Label label5 = new Label(" " + juego.getPrecio().toPlainString());
        Image image = new Image(ConvertToImage.convertToStreamImage(juego.getImagen()), "");
        //image.setWidth(200, Unit.PIXELS);
        image.setHeight(300, Unit.PIXELS);

        label2.addComponentAsFirst(new Icon(VaadinIcon.DESKTOP));
        label3.addComponentAsFirst(new Icon(VaadinIcon.USER));
        label4.addComponentAsFirst(new Icon(VaadinIcon.STAR));
        label5.addComponentAsFirst(new Icon(VaadinIcon.EURO));

        vertical.setHorizontalComponentAlignment(Alignment.START, label2, label3, label4, label5);
        vertical.add(label, image, label2, label3, label4, label5);

        return vertical;
    }

    private void createSeleccion() {
        Checkbox checkbox = new Checkbox("Seleccionar todo");
        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        Set<String> items = new LinkedHashSet<>(
                Arrays.asList("Listado1", "Listado2", "Listado3"));
        checkboxGroup.setItems(items);
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_HELPER_ABOVE_FIELD);

        Div div = new Div();
        div.addClassName("charts-view");

        checkboxGroup.addValueChangeListener(event -> {
            if (event.getValue().size() == items.size()) {
                checkbox.setValue(true);
                checkbox.setIndeterminate(false);
            } else if (event.getValue().isEmpty()) {
                checkbox.setValue(false);
                checkbox.setIndeterminate(false);
            } else {
                checkbox.setIndeterminate(true);
            }
        });

        checkbox.addValueChangeListener(event -> {
            if (checkbox.getValue()) {
                checkboxGroup.setValue(items);
            } else {
                checkboxGroup.deselectAll();
            }
        });

        checkboxGroup.addValueChangeListener(event -> {
            String datos;
            //Notification.show("Number of selected items: " + event.getValue().size());
            datos = checkboxGroup.getSelectedItems().toString();
            /*
            if (datos.contains("Juegos")) {
                verticalLayout1.setVisible(true);
            } else {
                verticalLayout1.setVisible(false);
            }
            if (datos.contains("S.O.")) {
                verticalLayout2.setVisible(true);
            } else {
                verticalLayout2.setVisible(false);
            }
            if (datos.contains("Edades")) {
                verticalLayout3.setVisible(true);
            } else {
                verticalLayout3.setVisible(false);
            }
             */
        });

        checkboxGroup.setValue(items);  //activamos todos los check
        // checkboxGroup.setValue(Collections.singleton("Estadística S.O.")); //para activar solo un valor checkbox
        div.add(checkbox, checkboxGroup);
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setWidthFull();
        horizontal.setJustifyContentMode(JustifyContentMode.CENTER);
        horizontal.add(div);
        add(horizontal);
        // add(verticalLayout1, verticalLayout2, verticalLayout3);
    }

}
