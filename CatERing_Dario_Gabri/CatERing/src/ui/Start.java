package ui;

import javafx.fxml.FXML;

public class Start {

    private Main mainPaneController;

    @FXML
    void beginMenuManagement() {
        mainPaneController.startMenuManagement();
    }

    @FXML
    void beginKitchenTaskManagement() { mainPaneController.startKitchenTaskManagement(); }

    public void setParent(Main main) {
        this.mainPaneController = main;
    }

    public void initialize() {
    }
}
