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
    exports client.interface_adapter.root;
    exports client.interface_adapter.category;
    exports client.interface_adapter.item;

    opens client.app to javafx.fxml;

    opens client.interface_adapter.item to javafx.fxml;
    opens client.interface_adapter.category to javafx.fxml;
    opens client.interface_adapter.root to javafx.fxml;
    opens client.entities to javafx.base;
}
