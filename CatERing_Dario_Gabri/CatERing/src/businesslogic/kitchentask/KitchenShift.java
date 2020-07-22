package businesslogic.kitchentask;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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



    public String testString() {
        String result = "id: " + this.id + ", start time: " + this.startTime + ", end time: " + this.endTime + ", full: " + full + ".";
        result = result + "\nCOOKS AVAILABLE ("+ this.cooksAvailable.size() +")\n";
        for (User cook : cooksAvailable)
            result = result + cook.toString() + "\n";
        result = result + "\nASSIGNED TASKS ("+ this.assignedTasks.size() +")\n";
        for (KitchenTask task : assignedTasks)
            result = result + task.toString() + "\n";

        return result + "\n";
    }

    public String toString() {
        return "start time: " + this.startTime +
                ", end time: " + this.endTime + ", full: " + full + ".";
    }


    public boolean hasCookAvailable(User cook) {
        return this.cooksAvailable.contains(cook);
    }

    public boolean isFull() { return this.full; }
    public boolean isPastShift() {
        // Da implementare tramite il tempo di login
        return false;
    }
    public void setFull(boolean full) { this.full = full; }

    public void assignKitchenTask(KitchenTask task) { this.assignedTasks.add(task); }
    public void unassignKitchenTask(KitchenTask task) {
        this.assignedTasks.remove(task);
    }

    // STATIC METHODS FOR PERSISTENCE

    public static KitchenShift loadKitchenShiftById(int shiftId) {
        if (loadedKitchenShifts.containsKey(shiftId)) return loadedKitchenShifts.get(shiftId);

        KitchenShift shift = new KitchenShift();
        String query = "SELECT * FROM KitchenShifts KS join AssignedTasks AT on (KS.id=AT.kitchenshift_id) " +
                "join CooksAvailable CA on (KS.id=CA.kitchenshift_id) WHERE KS.id="+ shiftId;
        ArrayList<Integer> availableCookIds = new ArrayList<>(),
                           assignedTaskIds = new ArrayList<>();
        
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shift.id = shiftId;
                shift.full = rs.getBoolean("isFull");
                shift.startTime = rs.getTime("start_time");
                shift.endTime = rs.getTime("end_time");
                int cookId = rs.getInt("cook_id"),
                    taskId = rs.getInt("task_id");
                if (!availableCookIds.contains(cookId))
                    availableCookIds.add(cookId);
                if (!assignedTaskIds.contains(taskId))
                    assignedTaskIds.add(taskId);
            }
        });

        if (shift.id > 0) {
            loadedKitchenShifts.putIfAbsent(shift.id, shift);
            for (Integer cookId : availableCookIds) {
                shift.cooksAvailable.add(User.loadUserById(cookId));
            }
            for (Integer taskId : assignedTaskIds) {
                shift.assignedTasks.add(KitchenTask.loadKitchenTaskById(taskId));
            }
        }


        return shift;
    }
/*
    public static ObservableList<KitchenShift> loadAllKitchenShifts() {
        ObservableList<KitchenShift> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM KitchenShifts KS join AssignedTasks AT on (KS.id=AT.kitchenshift_id) " +
                "join CooksAvailable CA on (KS.id=CA.kitchenshift_id)";
        HashMap<Integer, ArrayList<Integer>> cookIdsMap = new HashMap<>(),
                                             assignedTaskIdsMap = new HashMap<>();
        HashMap<Integer, KitchenShift> shiftsMap = new HashMap<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                cookIdsMap.putIfAbsent(id, new ArrayList<>());
                assignedTaskIdsMap.putIfAbsent(id, new ArrayList<>());
                if (!shiftsMap.containsKey(id)) {
                    shiftsMap.put(id, new KitchenShift());
                    KitchenShift shift = shiftsMap.get(id);
                    shift.full = rs.getBoolean("isFull");
                    shift.startTime = rs.getTime("start_time");
                    shift.endTime = rs.getTime("end_time");
                }
                int taskId = rs.getInt("task_id");
                if (!assignedTaskIdsMap.get(id).contains(taskId)) {
                    assignedTaskIdsMap.get(id).add(taskId);
                }
                int cookId = rs.getInt("cook_id");
                if (!cookIdsMap.get(id).contains(cookId)) {
                    cookIdsMap.get(id).add(cookId);
                }
            }
        });

        for (Integer id : shiftsMap.keySet()) {
            loadedKitchenShifts.putIfAbsent(id, shiftsMap.get(id));
            KitchenShift shift = shiftsMap.get(id);
            for (Integer cookId : cookIdsMap.get(id)) {
                shift.cooksAvailable.add(User.loadUserById(cookId));
            }
            for (Integer taskId : assignedTaskIdsMap.get(id)) {
                shift.assignedTasks.add(KitchenTask.loadKitchenTaskById(taskId));
            }
            all.add(shift);
        }

        return all;
    }*/

    public static ObservableList<KitchenShift> loadAllKitchenShifts() {
        String query = "SELECT id FROM KitchenShifts";
        ArrayList<Integer> shiftIds = new ArrayList<>();
        ObservableList<KitchenShift> result = FXCollections.observableArrayList();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                shiftIds.add(rs.getInt("id"));
            }
        });

        for (Integer i : shiftIds) {
            result.add(loadKitchenShiftById(i));
        }

        return result;
    }

    public static void updateKitchenShift(KitchenShift shift) {
        String shiftUpdate = "UPDATE KitchenShifts SET " +
                "isFull = " + shift.full +
                " WHERE id = " + shift.id;
        PersistenceManager.executeUpdate(shiftUpdate);
    }
}
