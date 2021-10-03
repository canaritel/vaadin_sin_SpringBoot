package org.vaadin.example.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.example.entities.Accesos;
import org.vaadin.example.entities.Registros;
import org.vaadin.example.entities.Roles;
import org.vaadin.example.services.AuthService;
import org.vaadin.example.services.RegistroService;
import org.vaadin.example.services.RolesService;
import org.vaadin.example.ui.authentication.AccessControlFactory;
import org.vaadin.example.ui.authentication.AccessControlInterface;

//@Route(value = "login", layout = MainLayout.class)
@Route(value = "login") //no carga la clase MainLayout, perfecto para el Login
@PageTitle("Login | Vaadin CRM")
@CssImport("./styles/shared-styles.css")
public class LoginView extends VerticalLayout {

    private final AccessControlInterface accessControl;

    public LoginView() {
        accessControl = AccessControlFactory.getInstance().createAccessControl();
        //authService = new AuthService();
        buildUI();
    }

    private void buildUI() {
        addClassName("login-rich-content");
        setSizeFull();

        // Creamos el compomente login form
        LoginForm loginForm = new LoginForm();
        loginForm.getElement().getThemeList().add("dark");
        loginForm.setI18n(createSpanishI18n()); //creamos nuestro componente login personalizado a nuestro idioma
        loginForm.addLoginListener((event) -> login(event));
        loginForm.addForgotPasswordListener(
                event -> Notification.show("Una pista: la misma que el username"));

        //loginForm.setOpened(true);
        //Nuevos botones
        // The login button is disabled when clicked to prevent multiple submissions.
        // To restore it, call component.setEnabled(true)
        Button restoreLogin = new Button("Crear nueva cuenta",
                //  event -> crearAcceso());
                event -> Notification.show("Para acceder como administrador utilice admin / admin"));
        restoreLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        // Setting error to true also enables the login button.
        Button showInfo = new Button(new Icon(VaadinIcon.INFO_CIRCLE),
                event -> Notification.show("Aplicación creada en Java versión 11, Jetty versión 9 y Vaadin version 21"));
        showInfo.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.add(restoreLogin, showInfo);

        // layout to center login form when there is sufficient screen space
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setSizeFull();
        //centeringLayout.setClassName("content");
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(loginForm, horizontal);

        // information text about logging in
        //Component loginInformation = buildLoginInformation();
        add(buildLoginInformation());
        add(centeringLayout);

    }

    private LoginI18n createSpanishI18n() {
        final LoginI18n i18n = LoginI18n.createDefault();

        Icon icon = VaadinIcon.INFO_CIRCLE.create();
        icon.setSize("36px");
        icon.getStyle().set("top", "-4px");

        Span title = new Span();
        title.getStyle().set("color", "var(--lumo-base-color)");
        title.add(icon);
        title.add(new Text(" My App"));

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle(title.getText());
        i18n.getHeader().setDescription("Java 11 + Vaadin 14 + OpenBeans 2019");
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setTitle("Acceda a su cuenta");
        i18n.getForm().setSubmit("Entrar");
        i18n.getForm().setPassword("Contraseña");
        i18n.getForm().setForgotPassword("Recordar contraseña");
        i18n.getErrorMessage().setTitle("Usuario/contraseña inválidos");
        i18n.getErrorMessage()
                .setMessage("Confirme su usuario y contraseña e inténtelo nuevamente.");
        i18n.setAdditionalInformation(
                "El acceso en modo usuario permite solo crear y editar. "
                + "Los datos de acceso son: \"user | user\""
                + ". Para el acceso de tipo administrador (con todos los"
                + " permisos) deberá crear una cuenta.");

        return i18n;
    }

    private Component buildLoginInformation() {
        VerticalLayout loginInformation = new VerticalLayout();
        //loginInformation.setClassName("information");
        // Personalización del acceso
        // Cargamos las imágenes
        //Image imgLogo = new Image("images/logo.png", "Vaadin Televoip logo");
        Image imgLogo = new Image("images/Televoip.png", "Vaadin Televoip logo");
        imgLogo.setHeight("120px");

        loginInformation.add(imgLogo);
        loginInformation.setAlignItems(Alignment.CENTER);
        return loginInformation;
    }

    private void login(LoginForm.LoginEvent event) {
        if (accessControl.signIn(event.getUsername(), event.getPassword())) {
            getUI().get().navigate("");
        } else {
            event.getSource().setError(true);
        }
    }

    private void crearAcceso() {
        RolesService rolService = new RolesService();
        Roles rol = new Roles();
        rol = rolService.leerRoles(2); //para admin

        RegistroService registroService = new RegistroService();
        Registros registro = new Registros();
        registro = registroService.leerRegistro(6); //para admin

        // Accesos  acceso = new Accesos("admin", "123456789", rol, registro);
        // AuthService authService = new AuthService();
        // authService.grabarAcceso(acceso);
    }

}
