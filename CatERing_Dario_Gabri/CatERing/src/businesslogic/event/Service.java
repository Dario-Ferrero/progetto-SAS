package businesslogic.event;

import businesslogic.menu.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;

public class Service implements EventItemInfo {
    private static Map<Integer, Service> loadedServices = FXCollections.observableHashMap();
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu proposedMenu, approvedMenu;

    public Service(String name) {
        this.name = name;
    }

    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp."
                + " menù approvato: " + ((this.approvedMenu != null) ? this.approvedMenu.getTitle() : "NO");
    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public Menu getApprovedMenu()       { return this.approvedMenu; }
    public void setApprovedMenu(Menu m) { this.approvedMenu = m; }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Service> loadServicesForEvent(int eventId) {
        if (Event.isLoadedEvent(eventId)) {
            Event ev = Event.getLoadedEvent(eventId);
            return ev.getServices();
        }

        ObservableList<Service> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, service_date, time_start, time_end, expected_participants, approved_menu_id " +
                "FROM Services WHERE event_id = " + eventId;
        ArrayList<Integer> approvedMenuIds = new ArrayList<>();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                Service serv = new Service(name);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                approvedMenuIds.add(rs.getInt("approved_menu_id"));
                result.add(serv);
            }
        });
        for (int i = 0; i < result.size(); i++) {
            Service s = result.get(i);
            int menuId = approvedMenuIds.get(i);
            Menu menuApproved = (menuId > 0) ? Menu.loadMenuById(menuId) : null;
            s.setApprovedMenu((menuId > 0) ? menuApproved : null);
            loadedServices.put(s.id, s);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Service) {
            Service that = (Service)obj;
            return this.id == that.id;
        }
        return false;
    }
}
