package ui.kitchentask;

import businesslogic.kitchentask.KitchenShift;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.Optional;

public class ModifyShiftDialog {
    Stage myStage;

    @FXML
    ComboBox<KitchenShift> shiftsCombo;

    @FXML
    Button confermaButton;

    private ObservableList<KitchenShift> validShifts;
    private KitchenShift selectedShift;
    private boolean confirmed;

    public void initialize() {
        confirmed = false;
        validShifts = null;
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    public void setValidShifts(ObservableList<KitchenShift> shifts) {
        this.validShifts = shifts;
        shiftsCombo.setItems(validShifts);
    }

    @FXML
    public void shiftsComboSelection() {
        KitchenShift sel = shiftsCombo.getValue();
        confermaButton.setDisable(sel == null);
    }

    @FXML
    public void confermaButtonPressed() {
        confirmed = true;
        selectedShift = shiftsCombo.getValue();
        myStage.close();
    }

    @FXML
    public void rimuoviAssegnamentoPressed() {
        confirmed = true;
        myStage.close();
    }

    @FXML
    public void annullaButtonPressed() {
        confirmed = false;
        myStage.close();
    }

    public Optional<KitchenShift> getSelectedKitchenShift() {
        if (!confirmed) selectedShift = null;
        return Optional.ofNullable(selectedShift);
    }

    public boolean hasConfirmed() { return this.confirmed; }
}
