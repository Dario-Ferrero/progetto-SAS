package persistence;

import businesslogic.kitchentask.KitchenTask;
import businesslogic.kitchentask.KitchenTaskEventReceiver;
import businesslogic.kitchentask.ServiceSheet;

public class KitchenTaskPersistence implements KitchenTaskEventReceiver {

    @Override
    public void updateServiceSheetCreated(ServiceSheet sheet) {
        ServiceSheet.saveNewServiceSheet(sheet);
    }

    @Override
    public void updateKitchenTaskAdded(ServiceSheet sheet, KitchenTask task) {
        KitchenTask.saveNewKitchenTask(sheet.getId(), task, sheet.getAllTasks().indexOf(task));
    }
}
