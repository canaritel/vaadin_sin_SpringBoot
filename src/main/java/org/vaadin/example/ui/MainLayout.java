package org.vaadin.example.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import org.vaadin.example.ui.views.JuegoView;
import org.vaadin.example.ui.views.DistribuidorView;
import org.vaadin.example.ui.views.EstadisticaView;
import org.vaadin.example.ui.views.UsuarioView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import org.vaadin.example.ui.authentication.AccessControlFactory;
import org.vaadin.example.ui.views.ListadoView;
import org.vaadin.example.ui.authentication.AccessControlInterface;

@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class MainLayout extends AppLayout implements RouterLayout {

    private Button logoutButton;
    private Tabs tabs;

    public MainLayout() {
        createHeader();
        createDrawer(createTabs());
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
        userName.setMaxWidth("80px"); //ancho separación

        addToNavbar(header, userName, header2);
    }

    private void createDrawer(Tabs tabs) {
        //Muestra un espacio en la barra de tabs
        VerticalLayout layout = new VerticalLayout();
        layout.add("");
        addToDrawer(layout);

        //Muestra el menú Tabs
        addToDrawer(tabs);

        // Create logout button but don't add it yet; admin view might be added
        // in between (see #onAttach())
        logoutButton = createMenuButton("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(e -> logout());
        logoutButton.getElement().setAttribute("title", "Logout (Ctrl+L)");
        // addToDrawer(logoutButton);
    }

    private Tabs createTabs() {
        tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.MATERIAL_FIXED);
        //tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL); //elimino barra 
        tabs.setId("tabs");

        // Listado de iconos https://vaadin.com/components/vaadin-icons/java-examples
        Tab usuarios = new Tab(
                VaadinIcon.USER.create(),
                createMenuLink(UsuarioView.class, UsuarioView.VIEW_NAME));
        //new RouterLink("Usuarios", UsuarioView.class));

        Tab distribuidores = new Tab(
                VaadinIcon.ROCKET.create(),
                createMenuLink(DistribuidorView.class, DistribuidorView.VIEW_NAME));
        //new RouterLink("Distribuidores", DistribuidorView.class));

        Tab juegos = new Tab(
                VaadinIcon.GAMEPAD.create(),
                createMenuLink(JuegoView.class, JuegoView.VIEW_NAME));
        //new RouterLink("Juegos", JuegoView.class));

        Tab listas = new Tab(
                VaadinIcon.BULLETS.create(),
                createMenuLink(ListadoView.class, ListadoView.VIEW_NAME));
        //new RouterLink("Listado", ListadoView.class));

        tabs.add(usuarios, distribuidores, juegos, listas); //añado las opciones Tabs generales

        return tabs;
    }

    private RouterLink createMenuLink(Class<? extends Component> viewClass, String caption) {
        final RouterLink routerLink = new RouterLink(null, viewClass);
        routerLink.setClassName("menu-link");
        //   routerLink.add(icon);
        routerLink.add(new Span(caption));
        // icon.setSize("24px");
        return routerLink;
    }

    private Button createMenuButton(String caption, Icon icon) {
        final Button routerButton = new Button(caption);
        routerButton.setClassName("menu-button");
        routerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        routerButton.setIcon(icon);
        icon.setSize("24px");
        return routerButton;
    }

    private void registerAdminViewIfApplicable(AccessControlInterface accessControl) {
        // register the admin view dynamically only for any admin user logged in
        if (accessControl.isUserInRole(AccessControlInterface.ADMIN_ROLE_NAME)
                && !RouteConfiguration.forSessionScope()
                        .isRouteRegistered(EstadisticaView.class)) {
            RouteConfiguration.forSessionScope().setRoute(EstadisticaView.VIEW_NAME,
                    EstadisticaView.class, MainLayout.class);
            // as logout will purge the session route registry, no need to
            // unregister the view on logout
        }
    }

    private void logout() {
        AccessControlFactory.getInstance().createAccessControl().signOut();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // User can quickly activate logout with Ctrl+L
        attachEvent.getUI().addShortcutListener(() -> logout(), Key.KEY_L,
                KeyModifier.CONTROL);

        // add the admin view menu item if user has admin role
        final AccessControlInterface accessControl = AccessControlFactory.getInstance()
                .createAccessControl();
        if (accessControl.isUserInRole(AccessControlInterface.ADMIN_ROLE_NAME)) {

            // Create extra navigation target for admins
            registerAdminViewIfApplicable(accessControl);

            // The link can only be created now, because the RouterLink checks
            // that the target is valid.
            Tab estadisticas = new Tab(
                    VaadinIcon.BAR_CHART.create(),
                    createMenuLink(EstadisticaView.class, EstadisticaView.VIEW_NAME));
            tabs.add(estadisticas);
        }

        // Finally, add logout button for all users
        addToDrawer(logoutButton);
    }

}
