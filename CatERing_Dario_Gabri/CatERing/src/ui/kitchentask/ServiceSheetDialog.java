package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.kitchentask.KitchenTask;
import businesslogic.kitchentask.ServiceSheet;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ServiceSheetDialog {

    @FXML
    Label userLabel;
    @FXML
    Label serviceLabel;

    @FXML
    ListView<KitchenTask> taskList;
    @FXML
    ListView<String> propertiesList;

    @FXML
    Button deleteTaskButton;
    @FXML
    Button upTaskButton;
    @FXML
    Button downTaskButton;
    @FXML
    Button resetButton;
    @FXML
    Button tabelloneTurniButton;
    @FXML
    Button modificaButton;

    @FXML
    Pane propertiesPane;
    @FXML
    GridPane centralPane;
    Pane emptyPane;
    boolean paneVisible = true;

    Stage myStage;

    public void initialize() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        if (currentUser != null) {
            userLabel.setText(currentUser.toString());
        }
        ServiceSheet toView = CatERing.getInstance().getKitchenTaskManager().getRecentServiceSheet();
        if (toView != null) {
            serviceLabel.setText(toView.getService().toString());
            taskList.setItems(toView.getAllTasks());
        }

        taskList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        taskList.getSelectionModel().select(null);
        taskList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<KitchenTask>() {
            @Override
            public void changed(ObservableValue<? extends KitchenTask> observableValue, KitchenTask oldTask, KitchenTask newTask) {
                if (newTask != null && newTask != oldTask) {
                    if (!paneVisible) {
                        centralPane.getChildren().remove(emptyPane);
                        centralPane.add(propertiesPane, 1, 0);
                        paneVisible = true;
                    }

                    ObservableList<String> properties = FXCollections.observableArrayList();
                    //properties.add(newTask.getKitchenProcedure().getName());
                    properties.add((newTask.getKitchenShift() != null) ? newTask.getKitchenShift().toString() : "");
                    properties.add((newTask.getCook() != null) ? newTask.getCook().toString() : "");
                    properties.add(Integer.toString(newTask.getTimeRequired()) + " minuti");
                    properties.add(newTask.getQuantity());
                    properties.add("Preparato? : " + ((newTask.isPrepared()) ? "sì" : "no"));
                    propertiesList.setItems(properties);

                    // enable other task actions
                    int pos = taskList.getSelectionModel().getSelectedIndex();
                    upTaskButton.setDisable(pos <= 0);
                    downTaskButton.setDisable(pos >= (taskList.getItems().size()) - 1);
                } else if (newTask == null) {
                    // disable section actions
                    deleteTaskButton.setDisable(true);
                    upTaskButton.setDisable(true);
                    downTaskButton.setDisable(true);
                }
                modificaButton.setDisable(true);
            }
        });

        propertiesList.setItems(FXCollections.emptyObservableList());
        propertiesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        propertiesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
                if (newString != null && newString != oldString) {
                    modificaButton.setDisable(false);
                } else if (newString == null) {
                    modificaButton.setDisable(true);
                }
            }
        });
        emptyPane = new BorderPane();
        centralPane.getChildren().remove(propertiesPane);
        centralPane.add(emptyPane, 1, 0);
        paneVisible = false;
    }

    public void setOwnStage(Stage stage) { myStage = stage; }

    @FXML
    public void esciButtonPressed() {
        myStage.close();
    }
}
