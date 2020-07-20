package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.kitchentask.KitchenShift;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.Optional;

public class KitchenShiftsDialog {
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
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    public void setValidShifts(ObservableList<KitchenShift> shifts) {
        this.validShifts = shifts;
    }

    @FXML
    public void shiftsComboSelection() {
        shiftsCombo.setItems(validShifts);
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
    public void annullaButtonPressed() {
        confirmed = false;
        myStage.close();
    }

    public Optional<KitchenShift> getSelectedKitchenShift() {
        if (!confirmed) selectedShift = null;
        return Optional.ofNullable(selectedShift);
    }
}
