package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.recipe.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.Optional;

public class AddTaskDialog {
    Stage myStage;

    @FXML
    ComboBox<Recipe> recipeCombo;

    @FXML
    Button aggiungiButton;

    private Recipe selectedRecipe;
    private boolean confirmed;

    public void initialize() {
        recipeCombo.setItems(CatERing.getInstance().getRecipeManager().getRecipes());
        confirmed = false;
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    @FXML
    public void recipeComboSelection() {
        Recipe sel = recipeCombo.getValue();
        aggiungiButton.setDisable(sel == null);
    }

    @FXML
    public void aggiungiButtonPressed() {
        confirmed = true;
        selectedRecipe = recipeCombo.getValue();
        myStage.close();
    }

    @FXML
    public void annullaButtonPressed() {
        confirmed = false;
        myStage.close();
    }

    public Optional<Recipe> getSelectedRecipe() {
        if (!confirmed) selectedRecipe = null;
        return Optional.ofNullable(selectedRecipe);
    }
}
