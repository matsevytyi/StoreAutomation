package client.interactor;

import client.api.ManageCategoriesAPI;
import client.api.ManageItemsAPI;
import client.entities.Category;
import client.entities.DisplayItem;
import client.entities.Item;
import client.entities.Statistics;
import client.interface_adapter.category.ManageCategoryController;
import client.interface_adapter.item.ManageItemController;
import client.view.AlertFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class MainScreenInteractor {
    public static ArrayList<Category> handleLoadCategories(){
        return ManageCategoriesAPI.getCategoriesList();
    }

    // categories handlers

    public static void handleAddCategory(){
        // Load the FXML file for AddCategoryView
        FXMLLoader fxmlLoader = new FXMLLoader(MainScreenInteractor.class.getResource("/client/view/category/AddCategoryView.fxml"));

        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a new stage
        Stage stage = new Stage();
        stage.setTitle("Add Category");

        // Set the scene with the loaded FXML root
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Show the stage
        stage.show();
    }

    public static void handleGoToCategory(Category category){
        // Load the FXML file for AddCategoryView
        FXMLLoader fxmlLoader = new FXMLLoader(MainScreenInteractor.class.getResource("/client/view/category/ManageCategoryView.fxml"));

        Parent root;
        try {
            root = fxmlLoader.load();

            ManageCategoryController controller = fxmlLoader.getController();
            controller.setCurrentCategory(category);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a new stage
        Stage stage = new Stage();
        stage.setTitle("Manage Category");

        // Set the scene with the loaded FXML root
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Show the stage
        stage.show();
    }

    public static void handleGoToItem(DisplayItem item){
        // Load the FXML file for AddCategoryView
        FXMLLoader fxmlLoader = new FXMLLoader(MainScreenInteractor.class.getResource("/client/view/item/ManageItemView.fxml"));

        Parent root;
        try {
            root = fxmlLoader.load();

            Item itemObj = ManageItemsAPI.getItem(item.getId());

            ManageItemController controller = fxmlLoader.getController();
            controller.setCurrentItem(itemObj);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a new stage
        Stage stage = new Stage();
        stage.setTitle("Manage Category");

        // Set the scene with the loaded FXML root
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Show the stage
        stage.show();
    }

    // items handlers

    public static ArrayList<DisplayItem> handleFilterItems(String name, double min_price, double max_price, int category_id){
        try {
            return ManageItemsAPI.getItemList(name, min_price, max_price, category_id);
        } catch (Exception e) {
            AlertFactory.prepareFailView("Error", "Error", e.getMessage());
            throw new RuntimeException(e);
            //return new ArrayList<>();
        }
    }

    public static ArrayList<Statistics> handleStatistics(){
        try {
            return ManageCategoriesAPI.getStatisticsList();
        } catch (Exception e) {
            AlertFactory.prepareFailView("Error", "Error", e.getMessage());
            throw new RuntimeException(e);
            //return new ArrayList<>();
        }
    }

}


