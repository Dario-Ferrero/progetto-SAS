package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.event.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import ui.Main;

import java.io.IOException;
import java.util.HashMap;

public class KitchenTaskManagement {
    @FXML
    Label userLabel;

    @FXML
    BorderPane containerPane;

    @FXML
    BorderPane serviceListPane;

    @FXML
    ServiceList serviceListPaneController;

    /*
    BorderPane serviceSheetContentPane; // da rimuovere
    ServiceSheetDialog serviceSheetDialogPaneController; // da rimuovere
    */

    Main mainPaneController;

    public void initialize() {
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("service-sheet-dialog.fxml"));
        try {
            serviceSheetContentPane = loader.load();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        serviceSheetDialogPaneController = loader.getController();
        serviceSheetDialogPaneController.setKitchenTaskManagementController(this);
        */
        // Inizializzare la mappa tenendo conto che sono dialog e non pane da sovrapporre

        if (CatERing.getInstance().getUserManager().getCurrentUser() != null) {
            String uname = CatERing.getInstance().getUserManager().getCurrentUser().getUserName();
            userLabel.setText(uname);
        }
        serviceListPaneController.setParent(this);
    }

    public void showServiceList(Service s) {
        serviceListPaneController.initialize();
        serviceListPaneController.selectService(s);
        containerPane.setCenter(serviceListPane);
    }

    public void endKitchenTaskManagement() {
        mainPaneController.showStartPane();
    }

    public void setMainPaneController(Main main) { mainPaneController = main; }
}
