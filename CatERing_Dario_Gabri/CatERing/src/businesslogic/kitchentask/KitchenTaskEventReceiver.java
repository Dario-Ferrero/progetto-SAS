package businesslogic.kitchentask;


public interface KitchenTaskEventReceiver {

    void updateServiceSheetCreated(ServiceSheet sheet);

    void updateKitchenTaskAdded(ServiceSheet sheet, KitchenTask task);

    void updateKitchenTaskDeleted(ServiceSheet sheet, KitchenTask task);

    void updateKitchenTasksRearranged(ServiceSheet sheet);

    void updateKitchenTaskAssigned(KitchenTask task);

    void updateKitchenTaskReset(ServiceSheet sheet, KitchenTask task);

    void updateKitchenTaskUpdated(KitchenTask task);

    void updateKitchenTaskReassigned(KitchenTask task);

    void updateKitchenShiftUpdated(KitchenShift shift);
}
