module com.pin.praktika {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.pin.praktika to javafx.fxml;
    exports com.pin.praktika;
}