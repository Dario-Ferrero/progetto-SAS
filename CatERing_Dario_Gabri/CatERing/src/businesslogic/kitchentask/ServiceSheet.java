package businesslogic.kitchentask;

import businesslogic.event.Service;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceSheet {
    private int id;
    private Service service;
    private ObservableList<KitchenTask> tasks;

    public ServiceSheet() { this(null); }
    public ServiceSheet(Service service) {
        this.id = 0;
        this.service = service;
        this.tasks = FXCollections.observableArrayList();
    }

    // SETTER METHODS

    public void addKitchenTask(KitchenTask task)        { this.tasks.add(task); }
    public boolean removeKitchenTask(KitchenTask task)  { return this.tasks.remove(task); }
    public void setId(int id)         { this.id = id; }
    public void setService(Service s) {
        this.service = s;
    }

    // GETTER METHODS

    public int getId()                               { return this.id; }
    public Service getService()                      { return this.service; }
    public ObservableList<KitchenTask> getAllTasks() { return this.tasks; }
    public boolean hasKitchenTask(KitchenTask task)     { return this.tasks.contains(task); }

    // OPERATION METHODS

    public void moveKitchenTask(KitchenTask task, int position) {
        this.tasks.remove(task);
        System.out.println("IN METHOD CALL\r\n");
        for (KitchenTask t : this.tasks) {
            System.out.println(tasks.indexOf(t) + ". " + t.toString());
        }
        this.tasks.add(position, task);
    }


    // STATIC METHODS FOR PERSISTENCE

    public static void saveNewServiceSheet(ServiceSheet sheet) {
        String sheetInsert = "INSERT INTO catering.ServiceSheets (service_id) VALUES (?);";
        int[] result = PersistenceManager.executeBatchUpdate(sheetInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, sheet.getService().getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if (count == 0) {
                    sheet.id = rs.getInt(1);
                }
            }
        });

        if (result[0] > 0) { // sheet effettivamente inserito
            // salva i compiti
            if (sheet.tasks.size() > 0) {
                KitchenTask.saveAllNewKitchenTasks(sheet.id, sheet.tasks);
            }
        }
    }

    public static void saveKitchenTasksOrder(ServiceSheet sheet) {
        String upd = "UPDATE KitchenTasks SET position = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, sheet.tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, batchCount);
                ps.setInt(2, sheet.tasks.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }

    public static ServiceSheet loadServiceSheet(Service s) {
        String query = "SELECT * FROM ServiceSheets sh " +
                "join SheetTasks st on (sh.id=st.sheet_id) " +
                "join KitchenTasks kts on (st.kitchentask_id=kts.id) WHERE sh.service_id=" + s.getId();
        ServiceSheet newSheet = new ServiceSheet();
        ObservableList<KitchenTask> newTasks = FXCollections.observableArrayList();
        ArrayList<Integer> procedureIds = new ArrayList<>(),
                           cookIds = new ArrayList<>(),
                           shiftIds = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                if (newSheet.getId() == 0)
                    newSheet.setId(rs.getInt("sh.id"));
                KitchenTask kt = new KitchenTask();
                kt.setId(rs.getInt("kitchentask_id"));
                kt.setPrepared(rs.getBoolean("prepared"));
                kt.setQuantity(rs.getString("quantity"));
                kt.setTimeRequired(rs.getInt("time_required"));
                int position = rs.getInt("position");
                newTasks.add(position, kt);
                cookIds.add(position, rs.getInt("cook_id"));
                shiftIds.add(position, rs.getInt("kitchenshift_id"));
                procedureIds.add(position, rs.getInt("procedure_id"));
            }
        });

        newSheet.setService(s);

        for (int i = 0; i < newTasks.size(); i++) {
            System.out.println("Load sheet's task\n" + newTasks.get(i));
            User cook = User.loadUserById(cookIds.get(i));
            newTasks.get(i).setCook((cook.getId() > 0) ? cook : null);

            KitchenShift shift = KitchenShift.loadKitchenShiftById(shiftIds.get(i));
            newTasks.get(i).setKitchenShift((shift.getId() > 0) ? shift : null);

            Recipe recipe = Recipe.loadRecipeById(procedureIds.get(i));
            newTasks.get(i).setKitchenProcedure((recipe.getId() > 0) ? recipe : null);

            newSheet.tasks = newTasks;
            KitchenTask.addLoadedKitchenTask(newTasks.get(i));
        }

        return newSheet;
    }

}
