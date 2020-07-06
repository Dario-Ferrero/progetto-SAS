package businesslogic.testkitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.kitchentask.KitchenTask;
import businesslogic.kitchentask.ServiceSheet;
import javafx.collections.ObservableList;

public class Test1 {
    public static void main(String[] args) {
        try {
            System.out.println("TEST 1. OPEN EXISTING SERVICE SHEET\r\n\r\n");
            CatERing instance = CatERing.getInstance();
            instance.getUserManager().fakeLogin("Lidia");
            System.out.println("User: " + instance.getUserManager().getCurrentUser());

            ObservableList<Event> allEvents = instance.getEventManager().getEvents();
            Event event = allEvents.get(2);
            System.out.println("Event loaded: " + event.toString());

            ObservableList<Service> allServices = Service.loadServicesForEvent(event.getId());
            Service service = allServices.get(0);
            System.out.println("Service loaded: " + service.toString());

            ServiceSheet sheet = instance.getKitchenTaskManager().openServiceSheet(event, service);
            System.out.println("TASKS");
            for (KitchenTask task : sheet.getAllTasks()) {
                System.out.println(task.toString());
            }
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
