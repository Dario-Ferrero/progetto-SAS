package businesslogic.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.MenuEventReceiver;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.user.User;

import java.util.ArrayList;

public class KitchenTaskManager {

    private ArrayList<ServiceSheet> openSheets;
    private ArrayList<KitchenTaskEventReceiver> eventReceivers;

    public KitchenTaskManager() {
        openSheets = new ArrayList<>();
        eventReceivers = new ArrayList<>();
    }

    public ServiceSheet openServiceSheet(Event event, Service service) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !event.getOrganizer().equals(user) || !event.hasService(service)) {
            System.err.println("Error in KitchenTaskManager$openServiceSheet");
            throw new UseCaseLogicException();
        }

        ServiceSheet openedSheet = ServiceSheet.loadServiceSheet(service);

        if (openedSheet.getId() == 0) {
            openedSheet = new ServiceSheet(service);
            for (Section sec : service.getApprovedMenu().getSections()) {
                for (MenuItem item : sec.getItems()) {
                    openedSheet.addKitchenTask(new KitchenTask(item.getItemRecipe()));
                }
            }
            for (MenuItem item : service.getApprovedMenu().getFreeItems()) {
                openedSheet.addKitchenTask(new KitchenTask(item.getItemRecipe()));
            }
            notifyServiceSheetCreated(openedSheet);
        }

        openSheets.add(openedSheet);
        return openedSheet;
    }

    public KitchenTask insertKitchenTask(ServiceSheet sheet, KitchenProcedure proc) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet)) {
            System.err.println("Error in KitchenTaskManager$insertKitchenTask");
            throw new UseCaseLogicException();
        }

        KitchenTask newTask = new KitchenTask(proc);
        sheet.addKitchenTask(newTask);
        notifyKitchenTaskAdded(sheet, newTask);

        return newTask;
    }

    private void notifyServiceSheetCreated(ServiceSheet sheet) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateServiceSheetCreated(sheet);
        }
    }

    private void notifyKitchenTaskAdded(ServiceSheet sheet, KitchenTask task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskAdded(sheet, task);
        }
    }

    public void addEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.add(rec);
    }
    public void removeEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }
}
