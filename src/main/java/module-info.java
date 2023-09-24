module com.example.btreevisualizationdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.btreevisualizationdemo to javafx.graphics, javafx.fxml;

    exports com.example.btreevisualizationdemo;
}