package client.interface_adapter.root;


import client.app.App;
import client.entities.Category;
import client.entities.DisplayItem;
import client.entities.Statistics;
import client.interactor.MainScreenInteractor;
import client.view.AlertFactory;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class MainScreenController {

    // stored entities

    ArrayList<Category> categories;


    // categories Tab
    @FXML
    private ListView<Category> categoryListView;

    @FXML
    private Button createCategoryButton;

    @FXML
    private Button filterItemButton;

    @FXML
    private Button reloadCategoriesButton;


    // items Tab
    @FXML
    private TextField itemSearchField;

    @FXML
    private Button reloadItemsButton;

    @FXML
    private ListView<DisplayItem> itemListView;

    @FXML
    private TextField minPriceFilterField;

    @FXML
    private TextField maxPriceFilterField;

    @FXML
    private ChoiceBox<String> categoryFilterChoiceBox;

    // statistics Tab

    @FXML
    private Button reloadStatsButton;

    @FXML
    private TableView<Statistics> tableView;

    @FXML
    private TableColumn<Statistics, String> categoryColumn;

    @FXML
    private TableColumn<Statistics, Double> totalPriceColumn;

    @FXML
    private TableColumn<Statistics, Integer> totalGoodsColumn;

    private ObservableList<Statistics> statisticList;


    @FXML
    public void initialize() {

        App.authenticate();

        //set Categories Tab

        categories = MainScreenInteractor.handleLoadCategories();
        categoryListView.setItems(observableArrayList(categories));

        categoryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    //open AddCategoryView
                    MainScreenInteractor.handleGoToCategory(categoryListView.getSelectionModel().getSelectedItem());
                }
            }
        });

        //set Items Tab

        ArrayList<String> categoryList = new ArrayList<>(); // Load categories to dropdown

        for (Category category : categories) {
            categoryList.add(category.getName());
        }

        categoryFilterChoiceBox.setItems(observableArrayList(categoryList));
        categoryFilterChoiceBox.setValue("Select Category");

        itemSearchField.setText("");

        minPriceFilterField.setText("");
        maxPriceFilterField.setText("");

        handleFilterItem(); // Load full items list and then add necessary text

        itemSearchField.setText("Enter part of the item name");

        minPriceFilterField.setText("Enter min price");
        maxPriceFilterField.setText("Enter max price");


        //set constraint to enter only numbers
        minPriceFilterField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(newValue.equals("Enter min price"))
                return;
            if (!newValue.matches("\\d*")) {
                minPriceFilterField.setText(newValue.replaceAll("[^\\d]", ""));
                AlertFactory.prepareFailView("Error", "Error", "This filed only accepts numbers");
            }
        });

        maxPriceFilterField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(newValue.equals("Enter max price"))
                return;
            if (!newValue.matches("\\d*")) {
                maxPriceFilterField.setText(newValue.replaceAll("[^\\d]", ""));
                AlertFactory.prepareFailView("Error", "Error", "This filed only accepts numbers");
            }
        });

        itemListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 2) {
                    //open AddCategoryView
                    MainScreenInteractor.handleGoToItem(itemListView.getSelectionModel().getSelectedItem());
                }
            }
        });

        //set statistics Tab

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalGoodsColumn.setCellValueFactory(new PropertyValueFactory<>("totalGoods"));

        // Initialize the data

        ArrayList<Statistics> statistics = MainScreenInteractor.handleStatistics();

        double totalPrice = 0.0;
        int totalGoods = 0;

        statisticList = observableArrayList();

        for (Statistics statistic : statistics) {
            statisticList.add(statistic);
            totalPrice += statistic.getTotalPrice();
            totalGoods += statistic.getTotalGoods();
        }

        statisticList.add(0, new Statistics("All", totalPrice, totalGoods));

        // Set the data in the TableView
        tableView.setItems(statisticList);

        System.out.println("MainScreenController initialized");

    }

    //universal events
    @FXML
    private void handleReload() {
        initialize();
    }

    // categories events
    @FXML
    private void handleCreateCategory() {
        System.out.println("Create Category button clicked");
        //open AddCategoryView
        MainScreenInteractor.handleAddCategory();
    }

    // items events
    @FXML
    private void handleFilterItem() {
        String name = itemSearchField.getText();
        if(!minPriceFilterField.getText().matches("\\d*") || !maxPriceFilterField.getText().matches("\\d*")){
            AlertFactory.prepareFailView("Error", "Error", "This filed only accepts numbers");
            return;
        }
        double minPrice = -1;
        double maxPrice = -1;
        try{
            minPrice = Double.parseDouble(minPriceFilterField.getText());
        }catch (Exception e){
        }
        try{
            maxPrice = Double.parseDouble(maxPriceFilterField.getText());
        }catch (Exception e){
        }

        String category = categoryFilterChoiceBox.getValue();

        if(category == null || category.contains("Select Category")){
            ArrayList<DisplayItem> items = MainScreenInteractor.handleFilterItems(name, minPrice, maxPrice, -1);
            itemListView.setItems(observableArrayList(items));
        } else {
            for (Category c : categories) {
                if (c.getName().equals(category)) {

                    ArrayList<DisplayItem> items = MainScreenInteractor.handleFilterItems(name, minPrice, maxPrice, c.getId());
                    itemListView.setItems(observableArrayList(items));
                    break;
                }
            }
        }





        System.out.println("Filter items button clicked: " + name + " " + minPrice + " " + maxPrice + " " + category);
        //MainScreenInteractor.handleFilterItems(minPrice, maxPrice, category);
    }

    //statistics events


}

