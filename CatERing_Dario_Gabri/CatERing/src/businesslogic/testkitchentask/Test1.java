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
import businesslogic.user.User;
import javafx.collections.ObservableList;

public class Test1 {

    public static void main(String[] args) {
        try {
            CatERing instance = CatERing.getInstance();
            instance.getUserManager().fakeLogin("Lidia");
            System.out.println("User: " + instance.getUserManager().getCurrentUser());

            Event event = Event.loadEventById(3);
            System.out.println("EVENT LOADED: " + event.toString());

            ObservableList<Service> allServices = Service.loadServicesForEvent(event.getId());
/*
            System.out.println("\r\n-- OPEN EXISTING SERVICE SHEET --\r\n");
            Service service = allServices.get(0);
            System.out.println("SERVICE LOADED: " + service.toString());

            ServiceSheet sheet = instance.getKitchenTaskManager().openServiceSheet(event, service);
            System.out.println("TASKS");
            ObservableList<KitchenTask> tasksBefore = sheet.getAllTasks();
            for (KitchenTask task : tasksBefore) {
                System.out.println(tasksBefore.indexOf(task) + ". " + task.toString());
            }

            System.out.println("\r\n-- LOOKUP SHIFTS BOARD --\r\n");
            ObservableList<KitchenShift> allShifts = instance.getKitchenTaskManager().lookupShiftsBoard();
            for (KitchenShift shift : allShifts) {
                System.out.println(shift);
            }

            System.out.println("\n-- DELETE KITCHEN TASK --\n");
            instance.getKitchenTaskManager().deleteKitchenTask(sheet, sheet.getAllTasks().get(4));
            ObservableList<KitchenTask> tasksAfter = sheet.getAllTasks();
            for (KitchenTask task : tasksAfter) {
                System.out.println(tasksAfter.indexOf(task) + ". " + task.toString());
            }

            System.out.println("\r\n-- ASSIGN KITCHEN TASK --\r\n");
            KitchenShift shift = KitchenShift.loadKitchenShiftById(1);
            User cook = User.loadUser("Marinella");
            instance.getKitchenTaskManager().assignKitchenTask(sheet, sheet.getAllTasks().get(0), shift, cook, 15, "600kg");

            System.out.println("TASK ASSIGNED:");
            System.out.println(sheet.getAllTasks().get(0));


            System.out.println("\r\n-- CHANGE KITCHEN TASK POSITION--\r\n");
            instance.getKitchenTaskManager().moveKitchenTask(sheet, newTask, 1);
            System.out.println("\r\nNEW DISPOSITION OF TASKS\r\n");
            ObservableList<KitchenTask> tasksAfter = sheet.getAllTasks();
            for (KitchenTask task : tasksAfter) {
                System.out.println(tasksAfter.indexOf(task) + ". " + task.toString());
            }
*/
            System.out.println("-- CREATE NEW DEFAULT SERVICE SHEET --");
            Service service2 = allServices.get(2);
            System.out.println("SERVICE LOADED: " + service2.toString());

            ServiceSheet newSheet = instance.getKitchenTaskManager().openServiceSheet(event, service2);
            System.out.println("TASKS");
            for (KitchenTask task : newSheet.getAllTasks()) {
                System.out.println(task.toString());
            }

            System.out.println("\r\n-- MODIFICATIONS --\r\n");
            User cook = User.loadUser("Marinella");
            System.out.println("\r\n-- COOKS --\r\n");
            instance.getKitchenTaskManager().modifyCook(newSheet, newSheet.getAllTasks().get(0), null);
            instance.getKitchenTaskManager().modifyCook(newSheet, newSheet.getAllTasks().get(2), null);
            instance.getKitchenTaskManager().modifyCook(newSheet, newSheet.getAllTasks().get(4), null);

            System.out.println("\r\n-- TIME --\r\n");
            instance.getKitchenTaskManager().modifyTimeRequired(newSheet, newSheet.getAllTasks().get(0), 130);
            instance.getKitchenTaskManager().modifyTimeRequired(newSheet, newSheet.getAllTasks().get(4), 25);

            System.out.println("\r\n-- QUANTITY --\r\n");
            instance.getKitchenTaskManager().modifyQuantity(newSheet, newSheet.getAllTasks().get(2), null);
            instance.getKitchenTaskManager().modifyQuantity(newSheet, newSheet.getAllTasks().get(3), "250kg");

            KitchenShift shiftOne = KitchenShift.loadKitchenShiftById(1);
            KitchenShift shiftTwo = KitchenShift.loadKitchenShiftById(2);

            System.out.println("\r\n-- FULL SHIFT --\r\n");
            instance.getKitchenTaskManager().setKitchenShiftFull(shiftOne, false);
            instance.getKitchenTaskManager().setKitchenShiftFull(shiftTwo, false);

            System.out.println("\r\n-- SHIFT --\r\n");
            instance.getKitchenTaskManager().modifyShift(newSheet, newSheet.getAllTasks().get(0), shiftOne);
            instance.getKitchenTaskManager().modifyShift(newSheet, newSheet.getAllTasks().get(1), shiftTwo);

            System.out.println("\r\n-- PREPARED --\r\n");
            for (int i = 2; i < 5; i++) {
                instance.getKitchenTaskManager().setKitchenTaskPrepared(newSheet, newSheet.getAllTasks().get(i));
            }

            /*
            System.out.println("\r\n-- INSERT NEW KITCHEN TASKS --\r\n");
            Recipe rec = Recipe.loadRecipeById(10);
            KitchenTask newTask =  instance.getKitchenTaskManager().insertKitchenTask(newSheet, rec);
            System.out.println("NEW TASK ADDED TO THE SHEET");
            System.out.println(newTask.toString() + "\r\n");

            Recipe rec2 = Recipe.loadRecipeById(7);
            KitchenTask newTask2 =  instance.getKitchenTaskManager().insertKitchenTask(newSheet, rec2);
            System.out.println("NEW TASK ADDED TO THE SHEET");
            System.out.println(newTask2.toString() + "\r\n");



            System.out.println("\r\n-- RESET SERVICE SHEET --\r\n");
            instance.getKitchenTaskManager().resetServiceSheet(newSheet);
            System.out.println("TASKS");
            for (KitchenTask task : newSheet.getAllTasks()) {
                System.out.println(task.toString());
            }
*/
        } catch (UseCaseLogicException e) {
            System.out.println("Errore di logica nello use case");
        } catch (KitchenTaskException e) {
            System.out.println("Errore di KTE");
            e.printStackTrace();
        }
    }
}
