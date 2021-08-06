package org.vaadin.example.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.HighlightConditions;

@CssImport("./styles/shared-styles.css") //aplicamos CSS, en Netbeans ver en Files carpeta Frontend - Styles
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Vaadin CRM");
        logo.addClassName("logo");
        
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink ("Lista", ListView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());
        
        addToDrawer(new VerticalLayout(listLink, new RouterLink("Dashboard", DashboardView.class)));
    }
 
    
}
