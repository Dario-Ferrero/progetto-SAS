package ui;

import businesslogic.CatERing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import ui.kitchentask.KitchenTaskManagement;
import ui.menu.MenuManagement;

import java.io.IOException;

public class Main {

    @FXML
    AnchorPane paneContainer;

    @FXML
    FlowPane startPane;

    @FXML
    Start startPaneController;

    @FXML
    BorderPane menuManagementPane;

    @FXML
    BorderPane kitchenTaskManagementPane;

    @FXML
    MenuManagement menuManagementPaneController;

    @FXML
    KitchenTaskManagement kitchenTaskManagementPaneController;

    public void initialize() {
        startPaneController.setParent(this);

        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menu/menu-management.fxml"));
        FXMLLoader kitchenTaskLoader = new FXMLLoader(getClass().getResource("kitchentask/kitchen-task-management.fxml"));

        assert (kitchenTaskLoader != null);

        try {
            menuManagementPane = menuLoader.load();
            menuManagementPaneController = menuLoader.getController();
            menuManagementPaneController.setMainPaneController(this);

            kitchenTaskManagementPane = kitchenTaskLoader.load();
            kitchenTaskManagementPaneController = kitchenTaskLoader.getController();
            kitchenTaskManagementPaneController.setMainPaneController(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void startMenuManagement() {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");

        menuManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(menuManagementPane);
        AnchorPane.setTopAnchor(menuManagementPane, 0.0);
        AnchorPane.setBottomAnchor(menuManagementPane, 0.0);
        AnchorPane.setLeftAnchor(menuManagementPane, 0.0);
        AnchorPane.setRightAnchor(menuManagementPane, 0.0);

    }

    public void startKitchenTaskManagement() {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");

        kitchenTaskManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(kitchenTaskManagementPane);
        AnchorPane.setTopAnchor(kitchenTaskManagementPane, 0.0);
        AnchorPane.setBottomAnchor(kitchenTaskManagementPane, 0.0);
        AnchorPane.setLeftAnchor(kitchenTaskManagementPane, 0.0);
        AnchorPane.setRightAnchor(kitchenTaskManagementPane, 0.0);
    }

    public void showStartPane() {
        startPaneController.initialize();
        paneContainer.getChildren().remove(menuManagementPane);
        paneContainer.getChildren().remove(kitchenTaskManagementPane);
        paneContainer.getChildren().add(startPane);
    }
}
