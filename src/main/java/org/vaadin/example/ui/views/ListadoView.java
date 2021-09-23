package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.example.entities.Juego;
import org.vaadin.example.services.JuegoService;
import org.vaadin.example.services.UsuarioService;
import org.vaadin.example.ui.MainLayout;
import org.vaadin.example.utils.ConvertToImage;

@Route(value = "listado", layout = MainLayout.class)  //si ocultamos no mostrará esta vista en las rutas públicas
@PageTitle("Listado | Vaadin CRM")
@CssImport("./styles/shared-styles.css")
public class ListadoView extends FlexLayout {

    private JuegoService juegoService;
    private UsuarioService usuarioService;
    private List<Juego> listado = new ArrayList<>();

    public ListadoView() {
        if (juegoService == null) {
            juegoService = new JuegoService();
        }
        if (usuarioService == null) {
            usuarioService = new UsuarioService();
        }

        addClassName("listado-horizontal");  //nombre del componente CSS
        //cargamos los objetos en nuestro ArrayList
        updateList();
        //creamos el indice
        createIndice();
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

    private void createIndice() {
        HorizontalLayout horizontal1 = new HorizontalLayout();
        horizontal1.addClassName("listado-indice");
        horizontal1.setWidthFull();
        Label label1A = new Label();
        Label label1B = new Label(listado.size() + " juegos");

        HorizontalLayout horizontal2 = new HorizontalLayout();
        horizontal2.addClassName("listado-indice");
        horizontal2.setWidthFull();
        Label label2A = new Label();
        Label label2B = new Label(calculaPrecioJuegos() + " euros");

        HorizontalLayout horizontal3 = new HorizontalLayout();
        horizontal3.addClassName("listado-indice");
        horizontal3.setWidthFull();
        Label label3A = new Label();
        Label label3B = new Label(calculaTotalUsuarios());

        HorizontalLayout horizontal4 = new HorizontalLayout();
        horizontal4.addClassName("listado-indice");
        horizontal4.setWidthFull();
        Label label4A = new Label();
        Label label4B = new Label(calculaTotalSO());

        horizontal1.add(label1A, label1B);
        horizontal2.add(label2A, label2B);
        horizontal3.add(label3A, label3B);
        horizontal4.add(label4A, label4B);
        horizontal1.setAlignItems(Alignment.CENTER);
        horizontal2.setAlignItems(Alignment.CENTER);
        horizontal3.setAlignItems(Alignment.CENTER);
        horizontal4.setAlignItems(Alignment.CENTER);

        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setWidthFull();
        horizontal.setJustifyContentMode(JustifyContentMode.CENTER);
        horizontal.add(horizontal1, horizontal2, horizontal3, horizontal4);

        add(horizontal);
    }

    private String calculaPrecioJuegos() {
        double valorJuegos = 0;
        String texto;
        for (Juego juego : listado) {
            valorJuegos = valorJuegos + juego.getPrecio().doubleValue();
        }

        BigDecimal formatNumber = new BigDecimal(valorJuegos);
        formatNumber = formatNumber.setScale(2, RoundingMode.CEILING);   //solo permitimos 2 decimales
        texto = "€" + formatNumber;

        return texto;
    }

    private String calculaTotalUsuarios() {
        return usuarioService.total() + " Usuarios";
    }

    private String calculaTotalSO() {
        return juegoService.listarSO().size() + " Sistemas Operativos";
    }

}
