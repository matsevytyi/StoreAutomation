package client.interface_adapter.category;

import client.entities.Category;
import client.interactor.CategoryInteractor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        //TODO: Go to AddItemView and place id of current category
        Stage stage = (Stage) createItemButton.getScene().getWindow();
        stage.close();
    }
}
