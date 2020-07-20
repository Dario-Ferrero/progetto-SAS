package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchentask.KitchenShift;
import businesslogic.kitchentask.KitchenTask;
import businesslogic.kitchentask.KitchenTaskException;
import businesslogic.kitchentask.ServiceSheet;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Optional;

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

    private ServiceSheet currentSheet;

    Stage myStage;

    public void initialize() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        if (currentUser != null) {
            userLabel.setText(currentUser.toString());
        }
        ServiceSheet toView = CatERing.getInstance().getKitchenTaskManager().getRecentServiceSheet();
        if (toView != null) {
            currentSheet = toView;
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
                    properties.add("Turno: " + ((newTask.getKitchenShift() != null) ? newTask.getKitchenShift().toString() : "non assegnato"));
                    properties.add("Cuoco: " + ((newTask.getCook() != null) ? newTask.getCook().toString() : "non assegnato"));
                    properties.add("Tempo Stimato: " + Integer.toString(newTask.getTimeRequired()) + " minuti");
                    properties.add("Quantità: " + ((!newTask.getQuantity().equals("")) ? newTask.getQuantity() : "0"));
                    properties.add("Preparato : " + ((newTask.isPrepared()) ? "sì" : "no"));
                    propertiesList.setItems(properties);

                    int pos = taskList.getSelectionModel().getSelectedIndex();
                    deleteTaskButton.setDisable(false);
                    upTaskButton.setDisable(pos <= 0);
                    downTaskButton.setDisable(pos >= (taskList.getItems().size()) - 1);
                } else if (newTask == null) {
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
                if (newString != null && !newString.equals(oldString)) {
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

    @FXML
    public void addTaskButtonPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-task-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            AddTaskDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Scegli la ricetta per il nuovo Compito");
            stage.setScene(new Scene(pane));
            stage.showAndWait();

            Optional<Recipe> chosen = controller.getSelectedRecipe();
            if (chosen.isPresent()) {
                KitchenProcedure proc = chosen.get();
                CatERing.getInstance().getKitchenTaskManager().insertKitchenTask(currentSheet, proc);
            }
        } catch (IOException | UseCaseLogicException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void deleteTaskButtonPressed() {
        KitchenTask task = taskList.getSelectionModel().getSelectedItem();
        try {
            CatERing.getInstance().getKitchenTaskManager().deleteKitchenTask(currentSheet, task);
            taskList.refresh();
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void resetButtonPressed() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset del Foglio Riepilogativo");
        alert.setHeaderText("Conferma di voler resettare il foglio?");
        alert.setContentText("Se sceglierai di resettare, i compiti non appartenenti al menù in uso verranno eliminati," +
                             " mentre i valori degli altri saranno reimpostati a default");

        ButtonType buttonTypeReset = new ButtonType("Conferma");
        ButtonType buttonTypeCancel = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeReset, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeReset) {
            try {
                CatERing.getInstance().getKitchenTaskManager().resetServiceSheet(currentSheet);
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void upTaskButtonPressed() {
        this.changeTaskPosition(-1);
    }

    @FXML
    public void downTaskButtonPressed() {
        this.changeTaskPosition(+1);
    }

    private void changeTaskPosition(int change) {
        int newpos = taskList.getSelectionModel().getSelectedIndex() + change;
        KitchenTask task = taskList.getSelectionModel().getSelectedItem();
        try {
            CatERing.getInstance().getKitchenTaskManager().moveKitchenTask(currentSheet, task, newpos);
            taskList.refresh();
            taskList.getSelectionModel().select(newpos);
        } catch (UseCaseLogicException | KitchenTaskException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void modificaButtonPressed() {

        // caso della modifica di un turno
        String property = propertiesList.getSelectionModel().getSelectedItem();
        String head = property.split(":")[0];

        switch (head) {
        case "Turno ": // a metodo privato!
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kitchen-shifts-dialog.fxml"));
            KitchenShiftsDialog controller = null;
            BorderPane pane = null;
            try {
                pane = loader.load();
                controller = loader.getController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Stage stage = new Stage();
            KitchenTask taskSel = taskList.getSelectionModel().getSelectedItem();
            ObservableList<KitchenShift> allShifts = FXCollections.observableArrayList(),
                    validShifts = FXCollections.observableArrayList();
            try {
                allShifts = CatERing.getInstance().getKitchenTaskManager().lookupShiftsBoard();
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
            for (KitchenShift ks : allShifts) {
                if (!ks.isFull() && (taskSel.getCook() == null || ks.hasCookAvailable(taskSel.getCook()))) {
                    validShifts.add(ks);
                }
            }
            controller.setValidShifts(validShifts);
            controller.setOwnStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Scegli il nuovo Turno di Cucina per il Compito");
            stage.setScene(new Scene(pane));
            stage.showAndWait();


            break;
        default:

        }
    }

    private String modifyShift() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kitchen-shifts-dialog.fxml"));
        KitchenShiftsDialog controller = null;
        BorderPane pane = null;
        try {
            pane = loader.load();
            controller = loader.getController();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        KitchenTask taskSel = taskList.getSelectionModel().getSelectedItem();
        ObservableList<KitchenShift> allShifts = FXCollections.observableArrayList(),
                validShifts = FXCollections.observableArrayList();
        try {
            allShifts = CatERing.getInstance().getKitchenTaskManager().lookupShiftsBoard();
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
        for (KitchenShift ks : allShifts) {
            if (!ks.isFull() && (taskSel.getCook() == null || ks.hasCookAvailable(taskSel.getCook()))) {
                validShifts.add(ks);
            }
        }
        controller.setValidShifts(validShifts);
        controller.setOwnStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Scegli il nuovo Turno di Cucina per il Compito");
        stage.setScene(new Scene(pane));
        stage.showAndWait();

        // set della proprietà con nuovo valore

        return null;
    }

}
