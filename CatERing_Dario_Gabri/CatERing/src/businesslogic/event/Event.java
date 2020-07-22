package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Event implements EventItemInfo {
    private static Map<Integer, Event> loadedEvents = FXCollections.observableHashMap();
    private int id;
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private int participants;
    private User organizer,
                 chef; // aggiungere in tabella DB + load dell'evento nel codice

    private ObservableList<Service> services;

    public Event() {
        this("");
    }

    public Event(String name) {
        this.name = name;
        id = 0;
    }

    public static boolean isLoadedEvent(int eventId) { return loadedEvents.containsKey(eventId); }
    public static Event getLoadedEvent(int eventId) { return loadedEvents.get(eventId); }

    public int getId() { return this.id; }
    public ObservableList<Service> getServices() {
        return FXCollections.unmodifiableObservableList(this.services);
    }
    public User getChef() { return this.chef; }
    public String getName() {return this.name; }
    public boolean hasService(Service service) {
        return services.contains(service);
    }

    public String toString() {
        return name + ": " + dateStart + "-" + dateEnd + ", " + participants + " pp. (" + organizer.getUserName() + ")";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static Event loadEventById(int eventId) {
        if (loadedEvents.containsKey(eventId)) return loadedEvents.get(eventId);

        String query = "SELECT * FROM Events WHERE id=" + eventId;
        Event ev = new Event();
        int[] orgChefIds = new int[2];
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                ev.name = rs.getString("name");
                ev.id = rs.getInt("id");
                ev.dateStart = rs.getDate("date_start");
                ev.dateEnd = rs.getDate("date_end");
                ev.participants = rs.getInt("expected_participants");
                orgChefIds[0] = rs.getInt("organizer_id");
                orgChefIds[1] = rs.getInt("chef_id");
            }
        });

        if (ev.id > 0) {
            ev.organizer = User.loadUserById(orgChefIds[0]);
            ev.chef = (orgChefIds[1] > 0) ? User.loadUserById(orgChefIds[1]) : null;
            ev.services = Service.loadServicesForEvent(ev.id);
            loadedEvents.put(ev.id, ev);
        }
        return ev;
    }

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
                int org = rs.getInt("organizer_id"),
                    ch = rs.getInt("chef_id");
                e.organizer = User.loadUserById(org);
                e.chef = (ch > 0) ? User.loadUserById(ch) : null;
                all.add(e);
            }
        });

        for (Event e : all) {
            e.services = Service.loadServicesForEvent(e.id);
        }
        return all;
    }

}
