module StoreAutomation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires jdk.httpserver;
    requires org.json;
    requires jjwt.api;
    requires java.sql;
    requires java.net.http;

    exports client.app;

    opens client.app to javafx.fxml;
    exports client.api;
    opens client.api to javafx.fxml;
}
