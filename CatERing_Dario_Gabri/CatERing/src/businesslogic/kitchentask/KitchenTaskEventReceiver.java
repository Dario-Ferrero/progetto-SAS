package businesslogic.kitchentask;


public interface KitchenTaskEventReceiver {

    void updateServiceSheetCreated(ServiceSheet sheet);

    void updateKitchenTaskAdded(ServiceSheet sheet, KitchenTask task);

    void updateKitchenTasksRearranged(ServiceSheet sheet);

    void updateKitchenTaskAssigned(KitchenTask task, KitchenShift shift);
}
