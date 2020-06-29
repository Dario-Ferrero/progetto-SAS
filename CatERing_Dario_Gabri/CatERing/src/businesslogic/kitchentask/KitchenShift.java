package businesslogic.kitchentask;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class KitchenShift extends Shift {
    private static Map<Integer, KitchenShift> loadedKitchenShifts = FXCollections.observableHashMap();
    private boolean full;
    private ObservableList<KitchenTask> assignedTasks;
    private ObservableList<User> cooksAvailable;

    public KitchenShift() {
        this.id = 0;
        this.assignedTasks = FXCollections.observableArrayList();
        this.cooksAvailable = FXCollections.observableArrayList();
    }


    public boolean isFull() { return this.full; }

    public void setFull(boolean full) { this.full = full; }

    // STATIC METHODS FOR PERSISTENCE

    public static KitchenShift loadKitchenShiftById(int shiftId) {
        if (loadedKitchenShifts.containsKey(shiftId)) return loadedKitchenShifts.get(shiftId);

        KitchenShift shift = new KitchenShift();
        String query = "SELECT * FROM KitchenShifts WHERE id="+ shiftId;
        ArrayList<Integer> availableCookIds = new ArrayList<>(),
                           assignedTaskIds = new ArrayList<>();
        
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shift.full = rs.getBoolean("isFull");
                availableCookIds.add(rs.getInt("available_cook_id"));
                assignedTaskIds.add(rs.getInt("kitchen_task_id"));
            }
        });

        if (shift.id > 0) {
            for (Integer cookId : availableCookIds) {
                shift.cooksAvailable.add(User.loadUserById(cookId));
            }

            for (Integer taskId : assignedTaskIds) {
                shift.assignedTasks.add(KitchenTask.loadKitchenTaskById(taskId));
            }
        }

        loadedKitchenShifts.putIfAbsent(shift.id, shift);
        return shift;
    }
}
