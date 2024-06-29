package client.interface_adapter.item;

import client.entities.Category;
import client.entities.Item;
import client.interactor.CategoryInteractor;
import client.interactor.ItemInteractor;
import client.view.AlertFactory;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddItemController {

    @FXML
    private Button saveButton;

    private Category currentCategory;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameText;

    @FXML
    private TextArea descriptionText;

    @FXML
    private TextField ManufacturerText;

    @FXML
    private TextField CategoryText;

    @FXML
    private TextField PricePerUnitText;

    @FXML
    public void initialize() {
        PricePerUnitText.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(newValue.equals("Price per unit"))
                return;
            if (!newValue.matches("\\d*")) {
                PricePerUnitText.setText(newValue.replaceAll("[^\\d]", ""));
                AlertFactory.prepareFailView("Error", "Error", "This filed only accepts numbers");
            }
        });
    }

    public void setCurrentCategory(Category category) {
        currentCategory = category;
    }

    public void initializeAfterLoad() {
        CategoryText.setText(currentCategory.getName());
        CategoryText.setDisable(true);
    }

    @FXML
    private void handleSave() {
        System.out.println("Save button clicked");
        String name = nameText.getText();
        String description = descriptionText.getText();
        Item newItem = new Item(1, name, description, ManufacturerText.getText(), Double.parseDouble(PricePerUnitText.getText()), currentCategory.getId());
        ItemInteractor.saveItem(newItem);
    }

    @FXML
    public void handleCancel() {
        System.out.println("Cancel button clicked");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
