package businesslogic.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
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
        if (!user.isChef() || event.getOrganizer().equals(user) || !event.hasService(service)) {
            throw new UseCaseLogicException();
        }

        ServiceSheet openedSheet = ServiceSheet.loadServiceSheet(service);

        if (openedSheet.getId() == 0) {
            openedSheet = new ServiceSheet(service);
            for (Section sec : service.getMenu().getSections()) {
                for (MenuItem item : sec.getItems()) {
                    openedSheet.addKitchenTask(new KitchenTask(item.getItemRecipe()));
                }
            }
            for (MenuItem item : service.getMenu().getFreeItems()) {
                openedSheet.addKitchenTask(new KitchenTask(item.getItemRecipe()));
            }
            notifyServiceSheetCreated(openedSheet);
        }

        openSheets.add(openedSheet);
        return openedSheet;
    }


    private void notifyServiceSheetCreated(ServiceSheet sheet) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateServiceSheetCreated(sheet);
        }
    }
}
