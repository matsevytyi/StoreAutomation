package client.interactor;

import client.api.ManageItemsAPI;
import client.entities.Category;
import client.entities.Item;
import client.view.AlertFactory;

public class ItemInteractor {

    public static void saveItem(Item item) {
        int result = ManageItemsAPI.addItem(item);
        switch (result) {
            case 201:
                AlertFactory.prepareSuccessView("Success", "Category Created", "Category created successfully");
                break;
            case 401:
                AlertFactory.prepareFailView("Error", "Category Creation Failed", "Unauthorized access. Please restart or update the app");
                break;
            case 409:
                AlertFactory.prepareFailView("Error", "Category Creation Failed", "A category with this name already exists. Please choose a different name.");
                break;
            default:
                AlertFactory.prepareFailView("Error", "Category Creation Failed", "Server error. Please try again.");
                break;
        }
    }

    public static void updateItem(Item item) {
        int result = ManageItemsAPI.updateItem(String.valueOf(item.getId()), item);
        switch (result) {
            case 204:
                AlertFactory.prepareSuccessView("Success", "Category Updated", "Category updated successfully");
                break;
            case 401:
                AlertFactory.prepareFailView("Error", "Category Update Failed", "Unauthorized access. Please restart or update the app");
                break;
            case 404:
                AlertFactory.prepareFailView("Error", "Category Update Failed", "Category not found. Please try again.");
                break;
            default:
                AlertFactory.prepareFailView("Error", "Category Update Failed", "Server error. Please try again.");
                break;
        }
    }

    public static void deleteItem(int id) {
        int result = ManageItemsAPI.deleteItem(String.valueOf(id));
        switch (result) {
            case 204:
                AlertFactory.prepareSuccessView("Success", "Item Deleted", "Item deleted successfully");
                break;
            case 401:
                AlertFactory.prepareFailView("Error", "Item Deletion Failed", "Unauthorized access. Please restart or update the app");
                break;
            case 404:
                AlertFactory.prepareFailView("Error", "Item Deletion Failed", "Item not found. Please try again.");
                break;
            default:
                AlertFactory.prepareFailView("Error", "Item Deletion Failed", "Server error. Please try again.");
                break;
        }
    }

    public static void updateItemQuantity(int id, int delta) {
        int result = ManageItemsAPI.alterQuantity(String.valueOf(id), delta);
        switch (result) {
            case 202:
                AlertFactory.prepareSuccessView("Success", "Quantity Update", "Quantity Updated successfully");
                break;
            case 401:
                AlertFactory.prepareFailView("Error", "Quantity Update Failed", "Unauthorized access. Please restart or update the app");
                break;
            case 404:
                AlertFactory.prepareFailView("Error", "Quantity Update Failed", "Item not found. Please try again.");
                break;
            case 409:
                AlertFactory.prepareFailView("Error", "Quantity Update Failed", "Not enough items in stock. Please try again.");
                break;
            default:
                AlertFactory.prepareFailView("Error", "Quantity Update Failed", "Server error. Please try again.");
                break;
        }
    }
}
