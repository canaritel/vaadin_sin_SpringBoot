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
import com.storedobject.chart.TreeChart;
import com.storedobject.chart.TreeData;
import com.storedobject.chart.XAxis;
import com.storedobject.chart.YAxis;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
        multiLinesChart();
        treeChart();
    }

    private void chart() {
        VerticalLayout flex = new VerticalLayout();
        flex.setWidth("1010px");
        flex.setHeight("610px");
        flex.addClassName("charts-view");
        //flex.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        // Creating a chart display area.
        SOChart soChart = new SOChart();
        soChart.setSize("1000px", "500px");
        // Let us define some inline data.
        CategoryData labels = new CategoryData("Banana", "Apple", "Orange", "Grapes");
        Data data = new Data(25, 40, 20, 30);

        // We are going to create a couple of charts. So, each chart should be positioned
        // appropriately.
        // Create a self-positioning chart.
        NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
        Position p = new Position();
        p.setTop(Size.percentage(50));
        nc.setPosition(p); // Position it leaving 50% space at the top

        // Second chart to add.
        BarChart bc = new BarChart(labels, data);
        RectangularCoordinate rc;
        rc = new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
        p = new Position();
        p.setBottom(Size.percentage(55));
        rc.setPosition(p); // Position it leaving 55% space at the bottom
        bc.plotOn(rc); // Bar chart needs to be plotted on a coordinate system

        // Just to demonstrate it, we are creating a "Download" and a "Zoom" toolbox button.
        // Toolbox toolbox = new Toolbox();
        // toolbox.addButton(new Toolbox.Download(), new Toolbox.Zoom());
        // Let's add some titles.
        Title title = new Title("My First Chart");
        title.setSubtext("2nd Line of the Title");

        // Add the chart components to the chart display area.
        soChart.add(nc, bc, title);

        // Now, add the chart display (which is a Vaadin Component) to your layout.
        flex.add(soChart);
        add(flex);
    }

    private void simpleLineChart() {
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
        add(soChart);
    }

    private void multiLinesChart() {
        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Generating 10 set of values for 10 LineCharts for the equation:
        // y = a + a * x / (a - 11) where a = 1 to 10, x and y are positive
        LineChart[] lineCharts = new LineChart[10];
        Data[] xValues = new Data[lineCharts.length];
        Data[] yValues = new Data[lineCharts.length];
        int i;
        for (i = 0; i < lineCharts.length; i++) {
            xValues[i] = new Data();
            xValues[i].setName("X (a = " + (i + 1) + ")");
            yValues[i] = new Data();
            yValues[i].setName("Y (a = " + (i + 1) + ")");
        }
        // For each line chart, we need only 2 end-points (because they are straight lines).
        int a;
        for (i = 0; i < lineCharts.length; i++) {
            a = i + 1;
            xValues[i].add(0);
            yValues[i].add(a);
            xValues[i].add(11 - a);
            yValues[i].add(0);
        }

        // Line charts are initialized here
        for (i = 0; i < lineCharts.length; i++) {
            lineCharts[i] = new LineChart(xValues[i], yValues[i]);
            lineCharts[i].setName("a = " + (i + 1));
        }

        // Line charts need a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        for (i = 0; i < lineCharts.length; i++) {
            lineCharts[i].plotOn(rc);
            soChart.add(lineCharts[i]); // Add the chart to the display area
        }

        // Add a simple title too
        soChart.add(new Title("Equation: y = a + a * x / (a - 11) where a = 1 to 10, x and y are positive"));

        // We don't want any legends
        soChart.disableDefaultLegend();

        // Add it to my layout
        add(soChart);
    }

    private void treeChart() {
        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Tree chart
        // (By default it assumes circular shape. Otherwise, we can set orientation)
        // All values are randomly generated
        TreeChart tc = new TreeChart();
        TreeData td = new TreeData("Root", 1000);
        tc.setTreeData(td);
        Random r = new Random();
        for (int i = 1; i < 21; i++) {
            td.add(new TreeData("Node " + i, r.nextInt(500)));
        }
        TreeData td1 = td.get(13);
        td = td.get(9);
        for (int i = 50; i < 56; i++) {
            td.add(new TreeData("Node " + i, r.nextInt(500)));
        }
        for (int i = 30; i < 34; i++) {
            td1.add(new TreeData("Node " + i, r.nextInt(500)));
        }

        // Add to the chart display area with a simple title
        soChart.add(tc, new Title("A Circular Tree Chart"));

        // Finally, add it to my layout
        add(soChart);
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
        Map<String, Integer> stats = getStats(); //llamamos al método getStats
        //almacenamos los datos de columnas y filas en el objeto dataMatrix (nombre del Juego)
        dataMatrix.setColumnNames(Arrays.toString(creaCadena()).split(",")); //obtenemos los datos convirtiendo el vector String separando por "," los campos
        dataMatrix.setColumnNames("Listado Juegos");
        //dataMatrix.setColumnNames(Arrays.toString(creaCadenaSO()).split(","));
        dataMatrix.setRowNames(Arrays.toString(creaCadena()).split(","));    //obtenemos los datos convirtiendo el vector String separando por "," los campos
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
            //bc = new BarChart(dataMatrix.getColumnNames(), dataMatrix.getRow(i));
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

    private Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        juegoService.listar("").forEach(juego
                -> stats.put(juego.getTitulo(), juego.getPrecio().intValue()));

        return stats;
    }

    private String[] creaCadena() {
        StringBuilder cadena = new StringBuilder();
        //Para poder usar los datos vamos a convertirlo con StringBuilder en un nuevo objeto almacenando todo en una solo objeto sperado por ","
        juegoService.listar("").forEach(juego
                -> cadena.append(juego.getTitulo() + ","));

        //convertimos la cadena en un vector de String
        String[] cadenaArray = new String[]{cadena.toString()};
        return cadenaArray;
    }

    private String[] creaCadenaSO() {
        StringBuilder cadena = new StringBuilder();
        //Para poder usar los datos vamos a convertirlo con StringBuilder en un nuevo objeto almacenando todo en una solo objeto sperado por ","
        juegoService.listar("").forEach(juego
                -> cadena.append(juego.getSistemaOperativo() + ","));

        //convertimos la cadena en un vector de String
        String[] cadenaArray = new String[]{cadena.toString()};
        return cadenaArray;
    }

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
     */
}
