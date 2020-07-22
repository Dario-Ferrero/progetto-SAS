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
                    properties.add("Preparato: " + ((newTask.isPrepared()) ? "sì" : "no"));
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
                    KitchenTask taskSelected = taskList.getSelectionModel().getSelectedItem();
                    modificaButton.setDisable(newString.split(":")[0].equals("Preparato")
                                            && (taskSelected.getKitchenShift() != null || taskSelected.getCook() != null));
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
    public void tabelloneTurniPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kitchen-shifts-board-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            KitchenShiftsBoardDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Tabellone dei Turni");
            stage.setScene(new Scene(pane));
            stage.setWidth(720.0);


            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
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
        String property = propertiesList.getSelectionModel().getSelectedItem();
        String[] splitProperty = property.split(":");
        String head = splitProperty[0],
               tail = splitProperty[1].substring(1);
        String result = null;
        switch (head) {
            case "Turno":
                result = modifyShift();
                break;
            case "Cuoco":
                result = modifyCook();
                break;
            case "Tempo Stimato":
                result = modifyTimeRequired(tail.split(" ")[0]) + " minuti";
                break;
            case "Quantità":
                result = modifyQuantity(tail);
                break;
            case "Preparato":
                result = setTaskPrepared();
                break;
            default:
                break;
        }
        // deassegnare turno / cuoco !
        if (result != null) {
            int i = propertiesList.getSelectionModel().getSelectedIndex();
            propertiesList.getItems().remove(i);
            propertiesList.getItems().add(i, head + ": " + result);
            propertiesList.refresh();
            propertiesList.getSelectionModel().select(i);
        }
    }

    private String modifyShift() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("modify-shift-dialog.fxml"));
        ModifyShiftDialog controller = null;
        BorderPane pane = null;
        try {
            pane = loader.load();
            controller = loader.getController();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        KitchenTask taskSelected = taskList.getSelectionModel().getSelectedItem();
        ObservableList<KitchenShift> allShifts = FXCollections.observableArrayList(),
                                     validShifts = FXCollections.observableArrayList();
        try {
            allShifts = CatERing.getInstance().getKitchenTaskManager().lookupShiftsBoard();
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
        for (KitchenShift ks : allShifts) {
            if (!ks.isFull() && (taskSelected.getCook() == null || ks.hasCookAvailable(taskSelected.getCook()))) {
                validShifts.add(ks);
            }
        }

        Stage stage = new Stage();
        controller.setValidShifts(validShifts);
        controller.setOwnStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Scegli il nuovo Turno di Cucina per il Compito");
        stage.setScene(new Scene(pane));
        stage.showAndWait();

        Optional<KitchenShift> chosen = controller.getSelectedKitchenShift();
        try {
            if (chosen.isPresent()) {
                KitchenShift newShift = chosen.get();
                CatERing.getInstance().getKitchenTaskManager().modifyShift(currentSheet, taskSelected, newShift);
                return newShift.toString();
            } else {
                CatERing.getInstance().getKitchenTaskManager().modifyShift(currentSheet, taskSelected, null);
                return (controller.hasConfirmed()) ? "non assegnato" : null;
            }
        } catch (UseCaseLogicException | KitchenTaskException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private String modifyCook() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("modify-cook-dialog.fxml"));
        ModifyCookDialog controller = null;
        BorderPane pane = null;
        try {
            pane = loader.load();
            controller = loader.getController();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        KitchenTask taskSelected = taskList.getSelectionModel().getSelectedItem();
        ObservableList<User> allCooks = User.loadAllCooks(),
                             validCooks = FXCollections.observableArrayList();

        for (User cook : allCooks) {
            if (taskSelected.getKitchenShift() == null || taskSelected.getKitchenShift().hasCookAvailable(cook)) {
                validCooks.add(cook);
            }
        }

        Stage stage = new Stage();
        controller.setValidCooks(validCooks);
        controller.setOwnStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Scegli il nuovo Cuoco per il Compito");
        stage.setScene(new Scene(pane));
        stage.showAndWait();

        Optional<User> chosen = controller.getSelectedCook();
        try {
            if (chosen.isPresent()) {
                User newCook = chosen.get();
                CatERing.getInstance().getKitchenTaskManager().modifyCook(currentSheet, taskSelected, newCook);
                return newCook.toString();
            } else {
                CatERing.getInstance().getKitchenTaskManager().modifyCook(currentSheet, taskSelected, null);
                return (controller.hasConfirmed()) ? "non assegnato" : null;
            }
        } catch (UseCaseLogicException | KitchenTaskException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private String modifyQuantity(String initialText) {
        TextInputDialog dial = new TextInputDialog(initialText);
        dial.setTitle("Modifica Quantità");
        dial.setHeaderText("Modifica la quantità per il Compito selezionato");
        Optional<String> result = dial.showAndWait();

        KitchenTask taskSelected = taskList.getSelectionModel().getSelectedItem();
        if (result.isPresent()) {
            try {
                CatERing.getInstance().getKitchenTaskManager().modifyQuantity(currentSheet, taskSelected, result.get());
                return result.get();
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
        return initialText;
    }
    
    private String modifyTimeRequired(String oldTime) {
        TextInputDialog dial = new TextInputDialog(oldTime);
        dial.setTitle("Modifica Tempo Stimato");
        dial.setHeaderText("Modifica il tempo stimato per il Compito selezionato\nInserire un valore intero");

        while (true) {
            Optional<String> result = dial.showAndWait();
            KitchenTask taskSelected = taskList.getSelectionModel().getSelectedItem();
            if (result.isPresent()) {
                try {
                    int newTime = Integer.parseInt(result.get());
                    CatERing.getInstance().getKitchenTaskManager().modifyTimeRequired(currentSheet, taskSelected, newTime);
                    return result.get();
                } catch (UseCaseLogicException ex) {
                    ex.printStackTrace();
                } catch (NumberFormatException ex) {

                }
            } else {
                return oldTime;
            }
        }
    }

    private String setTaskPrepared() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Impostare il Compito come \"preparato\"?");
        //alert.setContentText();

        ButtonType buttonTypeConfirm = new ButtonType("Conferma");
        ButtonType buttonTypeCancel = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeConfirm) {
            try {
                KitchenTask taskSelected = taskList.getSelectionModel().getSelectedItem();
                CatERing.getInstance().getKitchenTaskManager().setKitchenTaskPrepared(currentSheet, taskSelected);
                return "sì";
            } catch (UseCaseLogicException | KitchenTaskException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
