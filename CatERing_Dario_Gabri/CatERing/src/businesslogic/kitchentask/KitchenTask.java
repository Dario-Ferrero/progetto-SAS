package businesslogic.kitchentask;

import businesslogic.menu.Menu;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class KitchenTask {
    private static Map<Integer, KitchenTask> loadedKitchenTasks = FXCollections.observableHashMap();
    private int id;
    private int timeRequired;
    private boolean prepared;
    private String quantity;
    private KitchenProcedure procedure;
    private KitchenShift shift;
    private User cook;

    public KitchenTask() { this(null); }
    public KitchenTask(KitchenProcedure procedure) {
        this.id = 0;
        this.procedure = procedure;
    }

    public String toString() {
        return procedure + ": " + "quantity " + quantity + ", time required " + timeRequired + " minutes";
    }

    // GETTER METHODS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getTimeRequired() {
        return timeRequired;
    }
    public void setTimeRequired(int timeRequired) {
        this.timeRequired = timeRequired;
    }
    public boolean isPrepared() {
        return prepared;
    }
    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }
    public String getQuantity() {
        return quantity;
    }

    // SETTER METHODS

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public KitchenProcedure getKitchenProcedure() {
        return procedure;
    }
    public void setKitchenProcedure(KitchenProcedure procedure) {
        this.procedure = procedure;
    }
    public KitchenShift getKitchenShift() {
        return shift;
    }
    public void setKitchenShift(KitchenShift shift) {
        this.shift = shift;
    }

    public User getCook() {
        return cook;
    }

    public void setCook(User cook) {
        this.cook = cook;
    }

    public static void addLoadedKitchenTask(KitchenTask task) { loadedKitchenTasks.putIfAbsent(task.id, task); }

    // STATIC METHODS FOR PERSISTENCE

    public static KitchenTask loadKitchenTaskById(int taskId) {
        if (loadedKitchenTasks.containsKey(taskId)) return loadedKitchenTasks.get(taskId);

        KitchenTask newTask = new KitchenTask();
        String query = "SELECT * FROM KitchenTasks WHERE id="+ taskId;
        int[] fieldIds = new int[3];

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                newTask.id = rs.getInt("id");
                newTask.prepared = rs.getBoolean("prepared");
                newTask.quantity = rs.getString("quantity");
                newTask.timeRequired = rs.getInt("time_required");
                fieldIds[0] = rs.getInt("cook_id");
                fieldIds[1] = rs.getInt("procedure_id");
                fieldIds[2] = rs.getInt("kitchenshift_id");
            }
        });
        if (newTask.id > 0) {
            User cook = User.loadUserById(fieldIds[0]);
            newTask.setCook((cook.getId() > 0) ? cook : null);

            Recipe procedure = Recipe.loadRecipeById(fieldIds[1]);
            newTask.setKitchenProcedure((procedure.getId() > 0) ? procedure : null);

            KitchenShift shift = KitchenShift.loadKitchenShiftById(fieldIds[2]);
            newTask.setKitchenShift((shift.getId() > 0) ? shift : null);

            loadedKitchenTasks.putIfAbsent(newTask.id, newTask);
        }
        return newTask;
    }
}
