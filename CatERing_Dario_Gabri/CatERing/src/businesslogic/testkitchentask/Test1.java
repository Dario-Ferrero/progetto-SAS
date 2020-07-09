package businesslogic.testkitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.kitchentask.KitchenTask;
import businesslogic.kitchentask.ServiceSheet;
import businesslogic.recipe.Recipe;
import businesslogic.recipe.RecipeManager;
import javafx.collections.ObservableList;

public class Test1 {
    public static void main(String[] args) {
        try {
            System.out.println("TEST 1. \r\n-- OPEN EXISTING SERVICE SHEET --\r\n");
            CatERing instance = CatERing.getInstance();
            instance.getUserManager().fakeLogin("Lidia");
            System.out.println("User: " + instance.getUserManager().getCurrentUser());

            Event event = Event.loadEventById(3);
            System.out.println("EVENT LOADED: " + event.toString());

            ObservableList<Service> allServices = Service.loadServicesForEvent(event.getId());
            Service service = allServices.get(0);
            System.out.println("SERVICE LOADED: " + service.toString());

            ServiceSheet sheet = instance.getKitchenTaskManager().openServiceSheet(event, service);
            System.out.println("TASKS");
            for (KitchenTask task : sheet.getAllTasks()) {
                System.out.println(task.toString());
            }

            System.out.println("TEST 2. \r\n-- INSERT NEW KITCHEN TASK --\r\n");
            Recipe rec = Recipe.loadRecipeById(10);
            KitchenTask task1 =  instance.getKitchenTaskManager().insertKitchenTask(sheet, rec);
            System.out.println("NEW TASK ADDED TO THE SHEET");
            System.out.println(task1.toString() + "\r\n");

            System.out.println("-- CREATE NEW DEFAULT SERVICE SHEET --");
            Service service2 = allServices.get(2);
            System.out.println("SERVICE LOADED: " + service2.toString());

            ServiceSheet newSheet = instance.getKitchenTaskManager().openServiceSheet(event, service2);
            System.out.println("TASKS");
            for (KitchenTask task : newSheet.getAllTasks()) {
                System.out.println(task.toString());
            }

        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
