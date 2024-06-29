package client.interactor;

import client.entities.Category;
import client.api.ManageCategoriesAPI;
import client.view.AlertFactory;


public class CategoryInteractor {

    public static void saveCategory(String name, String description) {
        int result = ManageCategoriesAPI.addCategory(new Category(1, name, description));
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

    public static void updateCategory(Category category) {
        int result = ManageCategoriesAPI.updateCategory(String.valueOf(category.getId()), category);
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

    public static void deleteCategory(int id) {
        int result = ManageCategoriesAPI.deleteCategory(String.valueOf(id));
        switch (result) {
            case 204:
                AlertFactory.prepareSuccessView("Success", "Category Deleted", "Category deleted successfully");
                break;
            case 401:
                AlertFactory.prepareFailView("Error", "Category Deletion Failed", "Unauthorized access. Please restart or update the app");
                break;
            case 404:
                AlertFactory.prepareFailView("Error", "Category Deletion Failed", "Category not found. Please try again.");
                break;
            default:
                AlertFactory.prepareFailView("Error", "Category Deletion Failed", "Server error. Please try again.");
                break;
        }
    }
}

