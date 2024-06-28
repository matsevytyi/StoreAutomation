package client.interface_adapter.category;

import client.interactor.CategoryInteractor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategoryController {

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameText;

    @FXML
    private TextArea descriptionText;

    @FXML
    private void handleSave() {
        System.out.println("Save button clicked");
        String name = nameText.getText();
        String description = descriptionText.getText();
        CategoryInteractor.saveCategory(name, description);
    }

    @FXML
    public void handleCancel() {
        System.out.println("Cancel button clicked");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
