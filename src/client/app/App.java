package client.app;

import client.api.LoginAPI;
import client.interface_adapter.root.MainScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static String token;

    public static String getToken() {
        return token;
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            primaryStage.setTitle("JavaFX Layout Example");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/root/RootView.fxml"));
            Parent root = loader.load();
            MainScreenController controller = loader.getController();

            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void authenticate() {

        try {
            System.out.println("authenticating...");
            token = LoginAPI.execute("user", "5f4dcc3b5aa765d61d8327deb882cf99");
            System.out.println(token);
            if (token.equals("bad login")) {
                System.out.println("bad login");
                System.exit(0);
            }
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
