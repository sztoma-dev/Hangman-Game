module com.hangman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hangman to javafx.fxml;
    exports com.hangman;
}