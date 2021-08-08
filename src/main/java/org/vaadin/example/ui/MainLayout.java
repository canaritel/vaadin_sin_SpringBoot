package org.vaadin.example.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;

@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer(createTabs());

        // Ejemplo para aplicar en lectura de imágenes desde mi base datos ********************
        // byte[] imageBytes = getImageFromDB();
        // StreamResource resource = new StreamResource("fakeImageName.jpg", () -> new ByteArrayInputStream(imageBytes));
        // Image image = new Image(resource, "alternative image text");
        // layout.add(image);
    }

    private void createHeader() {
        //https://stackoverflow.com/questions/57553973/where-should-i-place-my-vaadin-10-static-files/57553974#57553974
        Image img = new Image("images/logo.png", "Vaadin Televoip logo");
        img.setHeight("48px");

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.add(new HorizontalLayout(new DrawerToggle(), img));
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer(Tabs tabs) {
        //Muestra un espacio en la barra de tabs
        VerticalLayout layout = new VerticalLayout();
        layout.add("");
        addToDrawer(layout);
        //Muestra el menú Tabs
        addToDrawer(tabs);
    }

    private Tabs createTabs() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.MATERIAL_FIXED);
        //tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL); //elimino barra 
        tabs.setId("tabs");

        // Listado de iconos https://vaadin.com/components/vaadin-icons/java-examples
        Tab usuarios = new Tab(
                VaadinIcon.USER.create(),
                new RouterLink("Usuarios", UsuarioView.class)
        );

        Tab distribuidores = new Tab(
                VaadinIcon.ROCKET.create(),
                new RouterLink("Distribuidores", DistribuidorView.class)
        );

        Tab juegos = new Tab(
                VaadinIcon.GAMEPAD.create(),
                new RouterLink("Juegos", JuegoView.class)
        );

        Tab estadisticas = new Tab(
                VaadinIcon.BAR_CHART.create(),
                new RouterLink("Estadísticas", DashboardView.class)
        );

        tabs.add(usuarios, distribuidores, juegos, estadisticas);

        return tabs;
    }

    /*
    private void createDrawer2() {
        RouterLink link1 = new RouterLink("Usuarios", UsuarioView.class);
        RouterLink link2 = new RouterLink("Distribuidores", DistribuidorView.class);
        RouterLink link3 = new RouterLink("Juegos", JuegoView.class);
        RouterLink link4 = new RouterLink("Estadísticas", DashboardView.class);

        //link1.setHighlightCondition(HighlightConditions.sameLocation()); //hago que se inicie en este enlace
        addToDrawer(new VerticalLayout(link1, link2, link3, link4));
    }
     */
}
