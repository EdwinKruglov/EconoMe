package main.java.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.java.data.Country;
import main.java.data.CountryIndicator;
import main.java.data.CountryIndicatorList;
import main.java.data.CountryList;

public class FXMLDocumentController implements Initializable {

    private ObservableList<Country> countryList;
    private ObservableList<CountryIndicator> countryIndicatorList;
    private ObservableSet<String> countryNames = FXCollections.observableSet();
    private ObservableSet<String> regionNames = FXCollections.observableSet();
    private ObservableSet<String> incomeLevels = FXCollections.observableSet();
    private ObservableSet<String> lendingTypes = FXCollections.observableSet();
    private ObservableSet<String> capitalCities = FXCollections.observableSet();
    private ArrayList<String> selectedItems = new ArrayList<>();
    private enum IndicatorType { GDP, GDPPERCAPITA, INFLATION, UNEMPLOYMENT }

    @FXML
    private StackPane stack;
    @FXML
    protected JFXButton exit;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private AnchorPane root;
    @FXML
    public static AnchorPane rootP;
    @FXML
    private ListView<String> list;

    @FXML
    private PieChart pieChart;
    @FXML
    private LineChart<String, Double> lineChartCurrGDP;
    @FXML
    private CategoryAxis xAxisCurrGDP;
    @FXML
    private NumberAxis yAxisCurrGDP;
    @FXML
    private LineChart<String, Double> lineChartGDPCapita;
    @FXML
    private CategoryAxis xAxisGDPCapita;
    @FXML
    private NumberAxis yAxisGDPCapita;
    @FXML
    private LineChart<String, Double> lineChartInflation;
    @FXML
    private CategoryAxis xAxisInflation;
    @FXML
    private NumberAxis yAxisInflation;
    @FXML
    private LineChart<String, Double> lineChartUnemployment;
    @FXML
    private CategoryAxis xAxisUnemployment;
    @FXML
    private NumberAxis yAxisUnemployment;


    @FXML
    private TabPane tpaneChartTabs;
    @FXML
    private Tab tabCurrGDP;
    @FXML
    private Tab tabGDPCapita;
    @FXML
    private Tab tabInflation;
    @FXML
    private Tab tabUnemployment;
    @FXML
    private Tab tabPieChart;
    @FXML
    private Tab tabBarChart;


    @FXML
    private void handleCountryNameAction(ActionEvent event)
    {
        list.setItems(FXCollections.observableArrayList(countryNames));
        tabPieChart.setDisable(false);
        tabCurrGDP.setDisable(false);
        tabBarChart.setDisable(false);

    }

    @FXML
    private void handleRegionNameAction(ActionEvent event)
    {
        list.setItems(FXCollections.observableArrayList(regionNames));
        tabPieChart.setDisable(false);
        tabCurrGDP.setDisable(false);
        tabBarChart.setDisable(false);
    }

    @FXML
    private void handleIncomeLevelAction(ActionEvent event)
    {
        list.setItems(FXCollections.observableArrayList(incomeLevels));
        tabPieChart.setDisable(false);
        tabCurrGDP.setDisable(false);
        tabBarChart.setDisable(false);
    }

    @FXML
    private void handleLendingTypeAction(ActionEvent event)
    {
        list.setItems(FXCollections.observableArrayList(lendingTypes));
        tabPieChart.setDisable(false);
        tabCurrGDP.setDisable(false);
        tabBarChart.setDisable(false);
    }

    private ArrayList<CountryIndicator> getIndicatorsByCountry(String strCountry)
    {
        ArrayList<CountryIndicator> country = new ArrayList<>();
        for(CountryIndicator c : countryIndicatorList)
            if(c.getCountryValue().equals(strCountry))
                country.add(c);

        return country;
    }

    private ArrayList<String> chartDataToRemove()
    {
        ArrayList<String> remove = new ArrayList<>(selectedItems); // list of countries to remove from chart
        remove.removeAll(list.getSelectionModel().getSelectedItems()); // remove all those that were selected before but aren't now
        System.out.println(remove);
        selectedItems.clear(); // clear the list of previously selected countries
        selectedItems.addAll(list.getSelectionModel().getSelectedItems()); // add currently selected countries for the next click
        return remove;
    }

    @SafeVarargs
    private final void removeOldChartData(ArrayList<String> toRemove, LineChart<String, Double>... charts)
    {
        for (String r : toRemove)
            for (LineChart<String, Double> c : charts)
                c.getData().removeIf(s -> s.getName().equals(r));
    }

    private void updateChart(LineChart<String, Double> chart, ArrayList<ArrayList<CountryIndicator>> data, IndicatorType indicator, boolean showZeroes)
    {
        ArrayList<XYChart.Series<String, Double>> dataSeries = new ArrayList<>();
        for(ArrayList<CountryIndicator> a : data)
        {
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            series.setName(list.getSelectionModel().getSelectedItem());
            for (CountryIndicator c : a)
            {
                double value;
                switch (indicator)
                {
                    case GDP:
                        value = c.getGDP_CURRENT_$US();
                        if (!showZeroes)
                            if (value == 0.0) continue;
                        series.getData().add(new XYChart.Data<>(Integer.toString(c.getDate()), value));
                        break;
                    case GDPPERCAPITA:
                        value = c.getGDP_PER_CAPITA_CURRENT_$US();
                        if (!showZeroes)
                            if (value == 0.0) continue;
                        series.getData().add(new XYChart.Data<>(Integer.toString(c.getDate()), value));
                        break;
                    case INFLATION:
                        value = c.getINFLATION_RATE();
                        if (!showZeroes)
                            if (value == 0.0) continue;
                        series.getData().add(new XYChart.Data<>(Integer.toString(c.getDate()), value));
                        break;
                    case UNEMPLOYMENT:
                        value = c.getUNEMPLOYMENT_RATE();
                        if (!showZeroes)
                            if (value == 0.0) continue;
                        series.getData().add(new XYChart.Data<>(Integer.toString(c.getDate()), value));
                        break;
                }
            }
            dataSeries.add(series);
        }
        chart.getData().addAll(dataSeries);
    }

    @FXML
    private void handleListAction(MouseEvent arg0){
        System.out.println("CCCCCCCCLICCCCCCCCCCCk");

        ArrayList<String> toRemove = chartDataToRemove();
        removeOldChartData(toRemove, lineChartCurrGDP, lineChartGDPCapita, lineChartInflation, lineChartUnemployment);

        ArrayList<ArrayList<CountryIndicator>> results = new ArrayList<>();
        if (toRemove.isEmpty()) // if there's nothing to remove or if we've only selected one country
            results.add(getIndicatorsByCountry(list.getSelectionModel().getSelectedItem())); // get selected country and add it

        if (!tabCurrGDP.isDisabled())
            updateChart(lineChartCurrGDP, results, IndicatorType.GDP, false);

        if (!tabGDPCapita.isDisabled())
            updateChart(lineChartGDPCapita, results, IndicatorType.GDPPERCAPITA, false);

        if (!tabInflation.isDisabled())
            updateChart(lineChartInflation, results, IndicatorType.INFLATION, true);

        if (!tabUnemployment.isDisabled())
            updateChart(lineChartUnemployment, results, IndicatorType.UNEMPLOYMENT, false);

        if (!tabPieChart.isDisabled())
        {
            int lowIncome = 0;
            int lowerMidIncome = 0;
            int upperMidIncome = 0;
            int highIncome = 0;
            int aggregates = 0;

            for (Country c : countryList)
            {
                if (c.getIncomeLevel().equals("Low income"))
                    lowIncome++;
                else if (c.getIncomeLevel().equals("High income"))
                    highIncome++;
                else if (c.getIncomeLevel().equals("Lower middle income"))
                    lowerMidIncome++;
                else if (c.getIncomeLevel().equals("Upper middle income"))
                    upperMidIncome++;
                else
                    aggregates++;
            }

            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("Low Income", lowIncome),
                            new PieChart.Data("High Income", highIncome),
                            new PieChart.Data("Lower Middle Income", lowerMidIncome),
                            new PieChart.Data("Upper Middle Income", upperMidIncome),
                            new PieChart.Data("Aggregates", aggregates));

            pieChart.setData(pieChartData);
            pieChart.setTitle("Income Levels of All Countries");
        }

        if (!tabBarChart.isDisabled())
        {
            String selectedItem = (String) list.getSelectionModel().getSelectedItem();
            ArrayList<CountryIndicator> results2 = new ArrayList<>();
            for(CountryIndicator c: countryIndicatorList){
                if(c.getCountryValue().equals(selectedItem)){
                    results2.add(c);
                }
            }

            //prepare the data
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setLabel("Year");

            XYChart.Series<String, Double> series = new XYChart.Series<>();
            series.setName("data");
            for(CountryIndicator c: results2){
                if(c.getGDP_CURRENT_$US() != 0.0){
                    series.getData().add(new XYChart.Data<>(Integer.toString(c.getDate()), c.getGDP_CURRENT_$US()));
                }
            }

            BarChart barChart = new BarChart<String,Number>(xAxis,yAxis);
            barChart.setTitle("GDP in US dollars");
            barChart.getData().add(series);
            barChart.setCategoryGap(0);
            barChart.setBarGap(0.5);
            tabBarChart.setContent(barChart);
            // System.out.println("TEST");
        }

    }

    @FXML
    private void changeColor(ActionEvent event) {
        JFXButton btn = (JFXButton) event.getSource();
        System.out.println(btn.getText());
        switch(btn.getText())
        {
            case "Color 1":FXMLDocumentController.rootP.setStyle("-fx-background-color:#00FF00");
                break;
            case "Color 2":FXMLDocumentController.rootP.setStyle("-fx-background-color:#0000FF");
                break;
            case "Color 3":FXMLDocumentController.rootP.setStyle("-fx-background-color:#FF0000");
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootP = root;

        try {
            VBox box = FXMLLoader.load(getClass().getClassLoader().getResource("main/java/view/SidePanelContent.fxml"));
            Button countryButton = (Button) box.getChildren().get(1);
            Button regionButton = (Button) box.getChildren().get(2);
            Button incomeLevelButton = (Button) box.getChildren().get(3);
            countryButton.setOnAction(event -> handleCountryNameAction(event));
            regionButton.setOnAction(event -> handleRegionNameAction(event));
            incomeLevelButton.setOnAction(event -> handleIncomeLevelAction(event));
            drawer.getChildren().add(box);

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.countryIndicatorList = new CountryIndicatorList().getCountryIndicators();
        countryList = new CountryList().getCountries();
        pieChart = new PieChart() {
            @Override
            protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
                if (getLabelsVisible()) {
                    getData().forEach(d -> {
                        Optional<Node> opTextNode = pieChart.lookupAll(".chart-pie-label").stream().filter(n -> n instanceof Text && ((Text) n).getText().contains(d.getName())).findAny();
                        if (opTextNode.isPresent()) {
                            ((Text) opTextNode.get()).setText(d.getName() + " " + "(" + (int)d.getPieValue() + ")");
                        }
                    });
                }
                super.layoutChartChildren(top, left, contentWidth, contentHeight);
            }};

        TreeSet<String> countries = new TreeSet<>();
        TreeSet<String> regions = new TreeSet<>();
        TreeSet<String> levels = new TreeSet<>();
        TreeSet<String> types = new TreeSet<>();

        for (int i = 0; i < countryList.size(); ++i) {
            regions.add(countryList.get(i).getRegionName());
            levels.add(countryList.get(i).getIncomeLevel());
            types.add(countryList.get(i).getLendingType());
        }

        //the next for loop only adds an object to the TreeSet "countries" if it's a country, noncountries are excluded
        //noncountries: names found in the TreeSets: regions, levels, types and other_noncountries (where names are added manually)
        TreeSet<String> other_noncountries = new TreeSet<>();
        other_noncountries.add("Early-demographic dividend"); //add other excluded noncountry names to the check, manually
        for (CountryIndicator c : countryIndicatorList)
        {
            String country_name = c.getCountryValue();
            if(!regions.contains(country_name) && !levels.contains(country_name) && !types.contains(country_name) && !other_noncountries.contains(country_name)) {
                countries.add(c.getCountryValue());
            }
            else System.out.println(country_name);
        }

        countryNames = FXCollections.observableSet(countries);
        regionNames = FXCollections.observableSet(regions);
        incomeLevels = FXCollections.observableSet(levels);
        lendingTypes = FXCollections.observableSet(types);

        tabPieChart.setContent(pieChart);

        //System.out.println(drawer.getChildren().get(0).getId());
        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{

            transition.setRate(transition.getRate()*-1);
            transition.play();

            if(drawer.isShown()){

                drawer.close();
                ObservableList<Node> workingCollection1 = FXCollections.observableArrayList(stack.getChildren());
                Collections.swap(workingCollection1, 0, 1);
                stack.getChildren().setAll(workingCollection1);


            }else{
                //drawer.open();
                list.setEditable(true);
                ObservableList<Node> workingCollection = FXCollections.observableArrayList(stack.getChildren());
                Collections.swap(workingCollection, 1, 0);
                stack.getChildren().setAll(workingCollection);
            }
        });
        list.setItems(FXCollections.observableArrayList(countryNames));

        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // allows multiple selection while holding CTRL

        lineChartCurrGDP.setTitle("Current GDP in US$");
        lineChartCurrGDP.setCreateSymbols(false);
        yAxisCurrGDP.setMinorTickCount(500);
        xAxisCurrGDP.setLabel("Year");
        tabCurrGDP.setContent(lineChartCurrGDP);

        lineChartGDPCapita.setTitle("GDP Per Capita in US$");
        lineChartGDPCapita.setCreateSymbols(false);
        yAxisGDPCapita.setMinorTickCount(500);
        xAxisGDPCapita.setLabel("Year");
        tabGDPCapita.setContent(lineChartGDPCapita);

        lineChartInflation.setTitle("Inflation Rate");
        lineChartInflation.setCreateSymbols(false);
        yAxisInflation.setMinorTickCount(500);
        xAxisInflation.setLabel("Year");
        tabInflation.setContent(lineChartInflation);

        lineChartUnemployment.setTitle("Unemployment Rate");
        lineChartUnemployment.setCreateSymbols(false);
        yAxisUnemployment.setMinorTickCount(500);
        xAxisUnemployment.setLabel("Year");
        tabUnemployment.setContent(lineChartUnemployment);
    }

}