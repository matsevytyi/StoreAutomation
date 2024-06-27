module StoreAutomation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires jdk.httpserver;
    requires org.json;
    requires jjwt.api;
    requires java.sql;

    exports client.APP;

    opens client.APP to javafx.fxml;
}
