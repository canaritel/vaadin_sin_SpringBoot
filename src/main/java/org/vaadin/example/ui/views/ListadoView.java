package org.vaadin.example.ui.views;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.example.entities.Listado;
import org.vaadin.example.ui.MainLayout;

@Route(value = "listado", layout = MainLayout.class)
@PageTitle("Listado | Vaadin CRM")
@CssImport("./styles/shared-styles.css")
@Tag("listado-view")
public class ListadoView extends VerticalLayout implements HasComponents, HasStyle {

    @Id
    private Select<String> sortBy;
    private Listado listado =  new Listado();

    public ListadoView() {
        //Le da al componente un nombre de clase CSS

        //ortBy.setItems("Popularity", "Newest first", "Oldest first");
        //sortBy.setValue("Popularity");

        //add(new LitPagination());
        add(new Listado("Hola1",
                        "https://images.unsplash.com/photo-1519681393784-d120267933ba?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80"));

        
    }

}
