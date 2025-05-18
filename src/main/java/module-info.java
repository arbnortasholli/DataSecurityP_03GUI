module src.datasecurityp_03gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens src.datasecurityp_03gui to javafx.fxml;
    exports src.datasecurityp_03gui;
}