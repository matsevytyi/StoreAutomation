package client.view;

import javafx.scene.control.Alert;

public class AlertFactory {
    public static void prepareFailView(String title, String HeaderText, String ContentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);

        alert.showAndWait();
    }

    public static void prepareSuccessView(String title, String HeaderText, String ContentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);

        alert.showAndWait();
    }
}
