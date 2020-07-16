package persistence;

import businesslogic.kitchentask.KitchenShift;
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

    @Override
    public void updateKitchenTaskUpdated(KitchenTask task) {
        KitchenTask.saveKitchenTaskUpdated(task);
    }

    @Override
    public void updateKitchenTaskDeleted(KitchenTask task) {
        KitchenTask.deleteKitchenTask(task);
    }

    @Override
    public void updateKitchenTasksRearranged(ServiceSheet sheet) {
        ServiceSheet.saveKitchenTasksOrder(sheet);
    }

    @Override
    public void updateKitchenTaskAssigned(KitchenTask task, KitchenShift shift) {
        KitchenTask.updateAssignedTask(task, shift);
    }
}
