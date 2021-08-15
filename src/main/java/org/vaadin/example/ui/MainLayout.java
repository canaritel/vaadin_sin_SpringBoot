package org.vaadin.example.ui;

import org.vaadin.example.ui.views.JuegoView;
import org.vaadin.example.ui.views.DistribuidorView;
import org.vaadin.example.ui.views.DashboardView;
import org.vaadin.example.ui.views.UsuarioView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
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
        // Cargamos las imágenes
        Image imgLogo = new Image("images/logo.png", "Vaadin Televoip logo");
        imgLogo.setHeight("48px");

        Image imgAvatar = new Image("images/avatar_60px.png", "avatar");
        imgAvatar.setHeight("48px");

        // Configuramos styling para el header
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.addClassName("header");
        header.getThemeList().set("light", true);
        header.setWidthFull();
        //header.setWidth("100%");

        // configuración del alineamiento, barra de navegación e imagen
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.add(new HorizontalLayout(new DrawerToggle(), imgLogo));

        // configuración del nuevo alineamiento para la imagen avatar 
        HorizontalLayout header2 = new HorizontalLayout();
        header2.add(new HorizontalLayout(imgAvatar));
        header2.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

        H4 userName = new H4("Antonio González");
        userName.setMaxWidth("80px"); //separación

        addToNavbar(header, userName, header2);
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
    
    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();

        // Configure styling for the header
        layout.setId("header");
        layout.getThemeList().set("light", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Have the drawer toggle button on the left
        layout.add(new DrawerToggle());

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        viewTitle = new H2("Hola");
        layout.add(viewTitle);

        // A user icon
        layout.add(new Image("images/avatar_60px.png", "Avatar"));

        return layout;
    }
    
     private Component createDrawerContent(Tabs menu) {
        Image img = new Image("images/logo.png", "Vaadin Televoip logo");
        img.setHeight("48px");

        VerticalLayout layout = new VerticalLayout();

        // Configure styling for the drawer
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Have a drawer header with an application logo
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        layout.add(img);

        //logoLayout.add(new H1("My Project"));
        // Display the logo and the menu in the drawer
        layout.add(logoLayout, menu);
        return layout;
    }
    
     */
}
