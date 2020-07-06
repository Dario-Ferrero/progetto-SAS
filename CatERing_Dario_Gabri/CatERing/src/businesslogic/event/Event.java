package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Event implements EventItemInfo {
    private int id;
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private int participants;
    private User organizer;

    private ObservableList<Service> services;

    public Event(String name) {
        this.name = name;
        id = 0;
    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public Date getDateStart() { return this.dateStart; }
    public Date getDateEnd() { return this.dateEnd; }
    public ObservableList<Service> getServices() {
        return FXCollections.unmodifiableObservableList(this.services);
    }
    public User getOrganizer() {
        return this.organizer;
    }

    public boolean hasService(Service service) {
        return services.contains(service);
    }

    public String toString() {
        return name + ": " + dateStart + "-" + dateEnd + ", " + participants + " pp. (" + organizer.getUserName() + ")";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Event> loadAllEvents() {
        ObservableList<Event> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                Event e = new Event(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("date_start");
                e.dateEnd = rs.getDate("date_end");
                e.participants = rs.getInt("expected_participants");
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                all.add(e);
            }
        });

        for (Event e : all) {
            e.services = Service.loadServicesForEvent(e.id);
        }
        return all;
    }

}
