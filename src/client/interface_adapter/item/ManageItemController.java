package client.interface_adapter.item;

import client.entities.Item;
import client.interactor.ItemInteractor;
import client.view.AlertFactory;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ManageItemController {

    private Item currentItem;

    public void setCurrentItem(Item item) {
        this.currentItem = item;
        nameText.setText(item.getName());
        descriptionText.setText(item.getDescription());
        ManufacturerText.setText(item.getManufacturer());
        PricePerUnitText.setText(String.valueOf(item.getPrice()));
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
    private TextField ManufacturerText;

    @FXML
    private TextField PricePerUnitText;

    @FXML
    private TextField AlterQuantityText;

    @FXML
    private Button IncreaseQuantityButton;

    @FXML
    private Button DecreaseQuantityButton;

    @FXML
    private Button createItemButton;

    @FXML
    public void initialize(){
        AlterQuantityText.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println(newValue);
            if (!newValue.contains("Quantity") && !newValue.matches("\\d*")) {
                AlterQuantityText.setText(newValue.replaceAll("[^\\d]", ""));
                AlertFactory.prepareFailView("Error", "Error", "This filed only accepts numbers");
            }
        });

    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit button clicked");
        Item newItem = new Item((int) currentItem.getId(), nameText.getText(), descriptionText.getText(), ManufacturerText.getText(), Double.parseDouble(PricePerUnitText.getText()), currentItem.getGroup_id());
        ItemInteractor.updateItem(newItem);
    }

    @FXML
    private void handleDelete() {
        System.out.println("Delete button clicked");
        ItemInteractor.deleteItem((int) currentItem.getId());
    }

    @FXML
    public void handleCancel() {
        System.out.println("Cancel button clicked");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void increaseQuantityClicked(){
        System.out.println("Increase Quantity button clicked");
        handleManageQuantity(false);
    }

    @FXML
    public void decreaseQuantityClicked(){
        System.out.println("Increase Quantity button clicked");
        handleManageQuantity(true);
    }

    @FXML
    public void handleManageQuantity(boolean flag) {
        int delta = Integer.parseInt(AlterQuantityText.getText());
        if(flag) delta = -delta;
        ItemInteractor.updateItemQuantity((int) currentItem.getId(), delta);
    }

}
