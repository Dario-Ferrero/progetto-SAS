package ui.kitchentask;

import businesslogic.CatERing;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import ui.Main;

public class KitchenTaskManagement {
    @FXML
    Label userLabel;

    @FXML
    BorderPane containerPane;

    @FXML
    BorderPane serviceListPane;

    @FXML
    ServiceList serviceListPaneController;

    Main mainPaneController;

    public void initialize() {
        if (CatERing.getInstance().getUserManager().getCurrentUser() != null) {
            String uname = CatERing.getInstance().getUserManager().getCurrentUser().getUserName();
            userLabel.setText(uname);
        }
        serviceListPaneController.setParent(this);
    }

    public void endKitchenTaskManagement() {
        mainPaneController.showStartPane();
    }

    public void setMainPaneController(Main main) { mainPaneController = main; }
}
