package org.vaadin.example.ui.views;

import com.storedobject.chart.BarChart;
import com.storedobject.chart.CategoryData;
import com.storedobject.chart.Chart;
import com.storedobject.chart.Data;
import com.storedobject.chart.DataMatrix;
import com.storedobject.chart.DataType;
import com.storedobject.chart.LineChart;
import com.storedobject.chart.NightingaleRoseChart;
import com.storedobject.chart.Position;
import com.storedobject.chart.RectangularCoordinate;
import com.storedobject.chart.SOChart;
import com.storedobject.chart.Size;

import com.storedobject.chart.Title;
import com.storedobject.chart.XAxis;
import com.storedobject.chart.YAxis;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.vaadin.example.services.JuegoService;
import org.vaadin.example.ui.MainLayout;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Vaadin CRM")
public class DashboardView extends VerticalLayout {

    private JuegoService juegoService;

    public DashboardView() {
        if (juegoService == null) {
            juegoService = new JuegoService();
        }

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        //    Mostramos las estadísticas   //
        createCharts();
        chart();
        simpleLineChart();
    }

    private void chart() {
        VerticalLayout vertical = new VerticalLayout();
        vertical.setWidth("1010px");
        vertical.setHeight("550px");
        vertical.addClassName("charts-view");
        //flex.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        // Creating a chart display area.
        SOChart soChart = new SOChart();
        soChart.setSize("1000px", "500px");
        // Let us define some inline data.
        //Map<String, Integer> stats = juegoService.getStatsSO();  <<VERSIÓN 1>>
        //Hacemos que HashMap sea synchronized: evitando problemas de orden en procesos multi-hilo (multithread)
        //stats = Collections.synchronizedMap(juegoService.getStatsSO()); //  
        //Map<String, Integer> stats = Collections.synchronizedMap(juegoService.getStatsSO()); <<VERSIÓN 2>>
        /* <<< ORDENAMOS EL OBJETO MAP MEDIENTE UN TREEMAP, Y LO SINCRONIZAMOS >>>
        TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>(hashMapObjeto); */
        TreeMap<String, Integer> stats = new TreeMap<>(Collections.synchronizedMap(juegoService.getStatsSO())); //<<VERSIÓN 3>>

        CategoryData labels = new CategoryData();
        //CategoryData labels = new CategoryData(creaCadenaSO().split(","));
        //almacenamos los datos string
        stats.forEach((name, number) -> {
            labels.add(name);
        });
        //almacenamos los datos numéricos
        Data data = new Data();
        stats.forEach((name, number) -> {
            data.add(number);
        });
        // We are going to create a couple of charts. So, each chart should be positioned appropriately.
        // Create a self-positioning chart.
        NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
        Position p = new Position();
        p.setTop(Size.percentage(50));
        nc.setPosition(p); // Position it leaving 50% space at the top

        // Second chart to add.
        BarChart bc = new BarChart(labels, data);
        bc.setName("TODO");
        RectangularCoordinate rc;
        rc = new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
        p = new Position();
        p.setBottom(Size.percentage(55));
        rc.setPosition(p); // Position it leaving 55% space at the bottom
        bc.plotOn(rc); // Bar chart needs to be plotted on a coordinate system

        // Let's add some titles.
        Title title = new Title("Sistemas Operativos");
        title.setSubtext("Número de Juegos por Sistema Operativo");

        // Add the chart components to the chart display area.
        soChart.add(nc, bc, title);

        // Now, add the chart display (which is a Vaadin Component) to your layout.
        vertical.add(soChart);
        add(vertical);
    }

    private void simpleLineChart() {
        VerticalLayout vertical = new VerticalLayout();
        vertical.setWidth("1010px");
        vertical.setHeight("550px");
        vertical.addClassName("charts-view");
        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("1000px", "500px");

        // Generating some random values for a LineChart
        Random random = new Random();
        Data xValues = new Data(), yValues = new Data();
        for (int x = 0; x < 40; x++) {
            xValues.add(x);
            yValues.add(random.nextDouble());
        }
        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        LineChart lineChart = new LineChart(xValues, yValues);
        lineChart.setName("40 Random Values");

        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Add to my layout
        vertical.add(soChart);
        add(vertical);
    }

    private void createCharts() {
        VerticalLayout flex = new VerticalLayout();
        flex.setWidth("1010px");
        flex.setHeight("610px");
        flex.addClassName("charts-view");
        flex.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        //
        List<Chart> chartJuego = new ArrayList<>();
        // Creating a chart display area
        SOChart soChart = new SOChart();
        //soChart.setSize("1000px", "550px");
        soChart.setWidth("1000px");
        soChart.setHeight("550px");
        //soChart.setSizeFull();
        SOChart soChart2 = new SOChart();
        //soChart.setSize("1000px", "50px");
        soChart2.setWidth("1000px");
        soChart2.setHeight("50px");
        //text.setText("como estas ");
        Title title = new Title(juegoService.total() + " Juegos");
        title.setSubtext("Estadística de precios y juegos");
        soChart2.add(title);
        // Define a data matrix to hold production data.
        DataMatrix dataMatrix = new DataMatrix("Listado Juegos");
        //Almacenamos en un objeto Map los datos de los juegos
        Map<String, Integer> stats = juegoService.getStats(); //llamamos al método getStats
        //almacenamos los datos de columnas y filas en el objeto dataMatrix (nombre del Juego)
        dataMatrix.setColumnNames("Listado Juegos");
        dataMatrix.setRowNames(creaCadena().split(","));
        //stats.forEach((name, number) -> {
        //    dataMatrix.setRowNames(name);
        //});
        dataMatrix.setRowDataName("Precio €");
        dataMatrix.setColumnDataName("Juegos");
        //almacenamos los datos numéricos del precio del Juego
        stats.forEach((name, number) -> {
            dataMatrix.addRow(number);
        });
        // Bar chart variable
        BarChart bc;
        // Define the fist rectangular coordinate.
        XAxis xAxis = new XAxis(DataType.CATEGORY);
        xAxis.setName(dataMatrix.getColumnDataName()); //Columna horizontal derecha
        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName(dataMatrix.getRowDataName());  //Columna vertical superior
        RectangularCoordinate rc = new RectangularCoordinate();
        rc.addAxis(xAxis, yAxis);
        // Create a bar chart for each row
        for (int i = 0; i < dataMatrix.getRowCount(); i++) {
            // Bar chart for the row
            bc = new BarChart(dataMatrix.getColumnNames(), dataMatrix.getRow(i));
            bc.setName(dataMatrix.getRowName(i));
            // Plot that to the coordinate system defined
            bc.plotOn(rc);
            // Add that to the chart list
            chartJuego.add(bc);
        }
        //rc.getPosition(true).setBottom(Size.percentage(10));
        rc.getPosition(true).setTop(Size.percentage(20));
        // Add the chart component(s) to the chart display area
        chartJuego.forEach(soChart::add);
        // Set the component for the view
        flex.add(soChart2, soChart);
        add(flex);
    }

    private String creaCadena() {
        StringBuilder cadena = new StringBuilder();
        //Para poder usar los datos vamos a convertirlo con StringBuilder en un nuevo objeto, almacenando todo en una solo objeto separado por ","
        juegoService.listar("").forEach(juego
                -> cadena.append(juego.getTitulo() + ","));

        //convertimos la cadena en un vector de String
        String[] cadenaArray = new String[]{cadena.toString()};

        String mycadena = Arrays.toString(cadenaArray);
        //quitamos los carácteres seleccionados
        mycadena = mycadena.replace("[", "");
        mycadena = mycadena.replace("]", "");

        return mycadena;
    }

    /*
    private Map<String, Integer> getCountSO() {
        HashMap<String, Integer> stats = new HashMap<>();
        juegoService.countSO().forEach(juego
                -> stats.put(juego.getNombre(), Integer.valueOf(juego.getCantidad())));

        return stats;
    }
     */
    //este método usa funciones de pago y se debe tener muy en cuenta
    /*
    private Component getCompaniesChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        Map<String, Integer> stats = juegoService.getStats();
        stats.forEach((name, number)
                -> dataSeries.add(new DataSeriesItem(name, number)));

        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }
    
    private Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        juegoService.listar("").forEach(juego
                -> stats.put(juego.getTitulo(), juego.getPrecio().intValue()));

        return stats;
    }
    
     */
}
