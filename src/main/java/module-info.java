module com.example.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.assignment1 to javafx.fxml;
    exports com.example.assignment1;
    exports controllers;
    opens controllers to javafx.fxml;
}