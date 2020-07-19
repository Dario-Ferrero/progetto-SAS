package businesslogic.event;

import javafx.collections.ObservableList;

public class EventManager {
    public ObservableList<Event> getEvents() {
        return Event.loadAllEvents();
    }

}
