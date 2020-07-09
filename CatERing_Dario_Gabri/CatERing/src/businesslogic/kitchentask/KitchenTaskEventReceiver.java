package businesslogic.kitchentask;


public interface KitchenTaskEventReceiver {

    void updateServiceSheetCreated(ServiceSheet sheet);

    void updateKitchenTaskAdded(ServiceSheet sheet, KitchenTask task);
}
