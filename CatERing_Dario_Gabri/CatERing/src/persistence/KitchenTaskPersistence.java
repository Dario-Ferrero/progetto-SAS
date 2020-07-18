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
    public void updateKitchenTaskReset(ServiceSheet sheet, KitchenTask task) {
        KitchenTask.updateKitchenTaskReset(sheet, task);
    }

    @Override
    public void updateKitchenTaskUpdated(KitchenTask task) {
        KitchenTask.updateKitchenTask(task);
    }

    @Override
    public void updateKitchenTaskReassigned(KitchenTask task) {
        KitchenTask.updateTaskReassigned(task);
    }

    @Override
    public void updateKitchenTaskDeleted(ServiceSheet sheet, KitchenTask task) {
        KitchenTask.deleteKitchenTask(sheet, task);
    }

    @Override
    public void updateKitchenTasksRearranged(ServiceSheet sheet) {
        ServiceSheet.saveKitchenTasksOrder(sheet);
    }

    @Override
    public void updateKitchenTaskAssigned(KitchenTask task) {
        KitchenTask.updateAssignedTask(task);
    }

    @Override
    public void updateKitchenShiftUpdated(KitchenShift shift) {
        KitchenShift.updateKitchenShift(shift);
    }
}
