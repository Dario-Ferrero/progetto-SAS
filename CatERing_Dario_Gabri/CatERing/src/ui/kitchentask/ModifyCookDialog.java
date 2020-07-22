package ui.kitchentask;

import businesslogic.user.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.Optional;

public class ModifyCookDialog {
    Stage myStage;

    @FXML
    ComboBox<User> cooksCombo;

    @FXML
    Button confermaButton;

    private ObservableList<User> validCooks;
    private User selectedCook;
    private boolean confirmed;

    public void initialize() {
        confirmed = false;
        validCooks = null;
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    public void setValidCooks(ObservableList<User> cooks) {
        this.validCooks = cooks;
        cooksCombo.setItems(validCooks);
    }

    @FXML
    public void cooksComboSelection() {
        User sel = cooksCombo.getValue();
        confermaButton.setDisable(sel == null);
    }

    @FXML
    public void confermaButtonPressed() {
        confirmed = true;
        selectedCook = cooksCombo.getValue();
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

    public Optional<User> getSelectedCook() {
        if (!confirmed) selectedCook = null;
        return Optional.ofNullable(selectedCook);
    }

    public boolean hasConfirmed() { return this.confirmed; }
}