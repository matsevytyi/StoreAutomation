package client.interface_adapter.category;

import client.api.ManageItemsAPI;
import client.entities.Category;
import client.entities.Item;
import client.interactor.CategoryInteractor;
import client.interactor.MainScreenInteractor;
import client.interface_adapter.item.AddItemController;
import client.interface_adapter.item.ManageItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ManageCategoryController {

    private Category currentCategory;

    public void setCurrentCategory(Category category) {
        this.currentCategory = category;
        nameText.setText(category.getName());
        descriptionText.setText(category.getDescription());
    }

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameText;

    @FXML
    private TextArea descriptionText;

    @FXML
    private Button createItemButton;

    @FXML
    private void handleEdit() {
        System.out.println("Edit button clicked");
        String name = nameText.getText();
        String description = descriptionText.getText();
        CategoryInteractor.updateCategory(new Category(currentCategory.getId(), name, description));
    }

    @FXML
    private void handleDelete() {
        System.out.println("Delete button clicked");
        CategoryInteractor.deleteCategory(currentCategory.getId());
    }

    @FXML
    public void handleCancel() {
        System.out.println("Cancel button clicked");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleCreateItem() {
        System.out.println("Create Item button clicked");

        FXMLLoader fxmlLoader = new FXMLLoader(MainScreenInteractor.class.getResource("/client/view/item/AddItemView.fxml"));

        Parent root;
        try {

            root = fxmlLoader.load();

            AddItemController controller = fxmlLoader.getController();
            controller.setCurrentCategory(currentCategory);
            controller.initializeAfterLoad();

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

        Stage oldStage = (Stage) createItemButton.getScene().getWindow();
        oldStage.close();
    }
}
