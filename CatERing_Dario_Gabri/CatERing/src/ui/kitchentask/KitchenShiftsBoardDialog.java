package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchentask.KitchenShift;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class KitchenShiftsBoardDialog {
    public static class ShiftRow {
        public StringProperty shiftString;
        public BooleanProperty fullShift;

        public ShiftRow() {
            shiftString = new SimpleStringProperty("");
            fullShift = new SimpleBooleanProperty(false);
        }

        public void setShiftString(String n) {
            shiftString.setValue(n);
        }

        public String getShiftString() {
            return shiftString.getValue();
        }

        public void setFullShift(boolean b) {
            fullShift.setValue(b);
        }

        public boolean isFullShift() {
            return fullShift.getValue();
        }
    }

    @FXML
    TableView<ShiftRow> shiftsTable;
    ObservableList<ShiftRow> rows;
    ObservableList<KitchenShift> allShifts;

    Stage myStage;


    public void initialize() {
        allShifts = null;
        try {
            allShifts = CatERing.getInstance().getKitchenTaskManager().lookupShiftsBoard();
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }

        rows = FXCollections.observableArrayList();
        for (KitchenShift ks : allShifts) {
            ShiftRow row = new ShiftRow();
            row.shiftString = new SimpleStringProperty(ks.toString());
            row.fullShift = new SimpleBooleanProperty(ks.isFull());
            rows.add(row);
        }
        shiftsTable.setItems(rows);

        TableColumn<ShiftRow, String> shiftStringCol = new TableColumn<>("Turno");
        shiftStringCol.setCellValueFactory(new PropertyValueFactory<>("shiftString"));
        TableColumn<ShiftRow, Boolean> fullShiftCol = new TableColumn<>("Completo");
        fullShiftCol.setCellValueFactory(new PropertyValueFactory<>("fullShift"));
        fullShiftCol.setCellFactory(c -> new CheckBoxTableCell<>(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer integer) {
                return rows.get(integer).fullShift;
            }
        }));

        shiftsTable.getColumns().add(shiftStringCol);
        shiftsTable.getColumns().add(fullShiftCol);
        shiftsTable.setEditable(true);
        shiftStringCol.setEditable(false);
        fullShiftCol.setEditable(true);
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    @FXML
    public void okButtonPressed() {
        for (int i = 0; i < rows.size(); i++) {
            ShiftRow row = rows.get(i);
            KitchenShift shift = allShifts.get(i);
            try {
                CatERing.getInstance().getKitchenTaskManager().setKitchenShiftFull(shift, row.fullShift.getValue());
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
        this.myStage.close();
    }

    @FXML public void annullaButtonPressed() {
        this.myStage.close();
    }
}

