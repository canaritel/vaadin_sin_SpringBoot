package org.vaadin.example.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Map;
import org.vaadin.example.services.JuegoService;
import org.vaadin.example.services.UsuarioService;
import org.vaadin.example.ui.MainLayout;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Vaadin CRM")
public class DashboardView extends VerticalLayout {

    private UsuarioService usuarioService;
    private JuegoService juegoService;

    public DashboardView() {
        if (usuarioService == null) {
            usuarioService = new UsuarioService();
        }

        if (juegoService == null) {
            juegoService = new JuegoService();
        }

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        //add(getContactStats());
        add(getContactStats(), getCompaniesChart());  //usa componentes especiales de pago
    }

    //este método usa funciones de pago y se debe tener muy en cuenta
    private Component getCompaniesChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        Map<String, Integer> stats = juegoService.getStats();
        stats.forEach((name, number)
                -> dataSeries.add(new DataSeriesItem(name, number)));

        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }

    private Span getContactStats() {
        Span stats = new Span(usuarioService.total() + " usuarios");
        stats.addClassName("contact-stats");

        return stats;
    }

}
