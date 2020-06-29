package businesslogic.kitchentask;

import businesslogic.event.Event;
import businesslogic.event.Service;

public interface KitchenTaskEventReceiver {

    public void updateServiceSheetCreated(ServiceSheet sheet);
}
