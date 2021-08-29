package org.vaadin.example.ui.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;
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

        //Damos al componente un nombre de clase CSS
        addClassName("horizontal-listado");  //nombre del componente CSS

        updateList();

        for (int i = 0; i < listado.size(); i++) {
            add(createListado(i));
        }
    }

    private void updateList() {
        listado = juegoService.listar("");
    }

    private VerticalLayout createListado(int index) {
        VerticalLayout vertical = new VerticalLayout();
        vertical.addClassName("horizontal-hijo");
        vertical.setWidth("auto");

        Juego juego = (Juego) listado.get(index);
        Label label = new Label(juego.getTitulo());
        Label label2 = new Label(juego.getSistemaOperativo());
        Label label3 = new Label(juego.getUsuario().getNombre());
        Label label4 = new Label(juego.getDistribuidor().getIdDistribuidor());
        Label label5 = new Label(juego.getPrecio().toPlainString());
        Image image = new Image(ConvertToImage.convertToStreamImage(juego.getImagen()), "");

        vertical.add(label, image, label2, label3, label4, label5);

        return vertical;
    }

}
