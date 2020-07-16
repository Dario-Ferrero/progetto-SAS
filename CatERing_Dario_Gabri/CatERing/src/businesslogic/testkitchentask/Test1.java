package businesslogic.testkitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.kitchentask.KitchenShift;
import businesslogic.kitchentask.KitchenTask;
import businesslogic.kitchentask.KitchenTaskException;
import businesslogic.kitchentask.ServiceSheet;
import businesslogic.recipe.Recipe;
import businesslogic.recipe.RecipeManager;
import javafx.collections.ObservableList;

public class Test1 {
    public static void main(String[] args) {
        try {

            CatERing instance = CatERing.getInstance();
            instance.getUserManager().fakeLogin("Lidia");
            System.out.println("User: " + instance.getUserManager().getCurrentUser());

            ObservableList<KitchenShift> allShifts = instance.getKitchenTaskManager().lookupShiftsBoard();
            for (KitchenShift shift : allShifts) {
                System.out.println(shift);
            }

            /*
            System.out.println("TEST 1. \r\n-- OPEN EXISTING SERVICE SHEET --\r\n");
            Event event = Event.loadEventById(3);
            System.out.println("EVENT LOADED: " + event.toString());

            ObservableList<Service> allServices = Service.loadServicesForEvent(event.getId());
            Service service = allServices.get(0);
            System.out.println("SERVICE LOADED: " + service.toString());

            ServiceSheet sheet = instance.getKitchenTaskManager().openServiceSheet(event, service);
            System.out.println("TASKS");
            ObservableList<KitchenTask> tasksBefore = sheet.getAllTasks();
            for (KitchenTask task : tasksBefore) {
                System.out.println(tasksBefore.indexOf(task) + ". " + task.toString());
            }

            System.out.println("TEST 2. \r\n-- INSERT NEW KITCHEN TASK --\r\n");
            Recipe rec = Recipe.loadRecipeById(10);
            KitchenTask newTask =  instance.getKitchenTaskManager().insertKitchenTask(sheet, rec);
            System.out.println("NEW TASK ADDED TO THE SHEET");
            System.out.println(newTask.toString() + "\r\n");

            System.out.println("TEST 3. \r\n-- CHANGE KITCHEN TASK POSITION--\r\n");
            instance.getKitchenTaskManager().moveKitchenTask(sheet, newTask, 1);
            System.out.println("\r\nNEW DISPOSITION OF TASKS\r\n");
            ObservableList<KitchenTask> tasksAfter = sheet.getAllTasks();
            for (KitchenTask task : tasksAfter) {
                System.out.println(tasksAfter.indexOf(task) + ". " + task.toString());
            }
*/
/*
            System.out.println("-- CREATE NEW DEFAULT SERVICE SHEET --");
            Service service2 = allServices.get(2);
            System.out.println("SERVICE LOADED: " + service2.toString());

            ServiceSheet newSheet = instance.getKitchenTaskManager().openServiceSheet(event, service2);
            System.out.println("TASKS");
            for (KitchenTask task : newSheet.getAllTasks()) {
                System.out.println(task.toString());
            }
*/
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        }
    }
}
