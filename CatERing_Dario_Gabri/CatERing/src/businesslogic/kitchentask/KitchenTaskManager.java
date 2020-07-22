package businesslogic.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenProcedure;
import businesslogic.user.User;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class KitchenTaskManager {

    private ArrayList<ServiceSheet> openSheets;
    private ArrayList<KitchenTaskEventReceiver> eventReceivers;

    public KitchenTaskManager() {
        openSheets = new ArrayList<>();
        eventReceivers = new ArrayList<>();
    }

    public ServiceSheet getRecentServiceSheet() {
        return this.openSheets.get(0);
    }

    public ServiceSheet openServiceSheet(Event event, Service service) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || event.getChef() == null || !event.getChef().equals(user) || !event.hasService(service)) {
            System.err.println("Error in openServiceSheet");
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

        openSheets.add(0, openedSheet);
        return openedSheet;
    }

    public ServiceSheet resetServiceSheet(ServiceSheet sheet) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet)) {
            System.err.println("Error in resetServiceSheet");
            throw new UseCaseLogicException();
        }

        ArrayList<KitchenTask> tasksToDelete = new ArrayList<>();
        for (KitchenTask task : sheet.getAllTasks()) {
            if (!sheet.getService().getApprovedMenu().hasRecipe(task.getKitchenProcedure())) {
                tasksToDelete.add(task);
            } else {
                task.setTimeRequired(0);
                task.setPrepared(false);
                task.setQuantity("");
                task.setCook(null);
                if (task.getKitchenShift() != null) {
                    task.getKitchenShift().unassignKitchenTask(task);
                    task.setKitchenShift(null);
                }

                notifyKitchenTaskReset(sheet, task);
            }
        }

        for (KitchenTask task : tasksToDelete) {
            deleteKitchenTask(sheet, task);
        }

        return sheet;
    }

    public KitchenTask insertKitchenTask(ServiceSheet sheet, KitchenProcedure proc) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet)) {
            System.err.println("Error in insertKitchenTask");
            throw new UseCaseLogicException();
        }

        KitchenTask newTask = new KitchenTask(proc);
        sheet.addKitchenTask(newTask);
        notifyKitchenTaskAdded(sheet, newTask);

        return newTask;
    }

    public void deleteKitchenTask(ServiceSheet sheet, KitchenTask task) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            System.err.println("Error in deleteKitchenTask");
            throw new UseCaseLogicException();
        }
        sheet.removeKitchenTask(task);
        if (task.getKitchenShift() != null) {
            task.getKitchenShift().unassignKitchenTask(task);
        }
        notifyKitchenTaskDeleted(sheet, task);
    }

    public void moveKitchenTask(ServiceSheet sheet, KitchenTask task, int position) throws UseCaseLogicException, KitchenTaskException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            System.err.println("Error in moveKitchenTask");
            throw new UseCaseLogicException();
        } else if (position < 0 || position >= sheet.getAllTasks().size()) {
            System.err.println("Position parameter out of bounds");
            throw new KitchenTaskException();
        }

        sheet.moveKitchenTask(task, position);
        notifyKitchenTasksRearranged(sheet);
    }

    public void modifyShift(ServiceSheet sheet, KitchenTask task, KitchenShift shift) throws UseCaseLogicException, KitchenTaskException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        } else if (shift != null && (shift.isPastShift() || shift.isFull() ||
                (task.getCook() != null && !shift.hasCookAvailable(task.getCook())))
        ) {
            throw new KitchenTaskException();
        }

        if (task.getKitchenShift() != null) {
            task.getKitchenShift().unassignKitchenTask(task);
        }
        if (shift != null) {
            shift.assignKitchenTask(task);
        }
        task.setKitchenShift(shift);

        notifyKitchenTaskReassigned(task);
    }
    
    public void modifyCook(ServiceSheet sheet, KitchenTask task, User cook) throws UseCaseLogicException, KitchenTaskException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        } else if (cook != null && task.getKitchenShift() != null && !task.getKitchenShift().hasCookAvailable(cook)) {
            throw new KitchenTaskException();
        }

        task.setCook(cook);
        notifyKitchenTaskUpdated(task);
    }

    public void modifyTimeRequired(ServiceSheet sheet, KitchenTask task, int timeRequired) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        }

        task.setTimeRequired(timeRequired);
        notifyKitchenTaskUpdated(task);
    }

    public void modifyQuantity(ServiceSheet sheet, KitchenTask task, String quantity) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        }

        task.setQuantity((quantity != null) ? quantity : "");
        notifyKitchenTaskUpdated(task);
    }

    public void setKitchenTaskPrepared(ServiceSheet sheet, KitchenTask task) throws UseCaseLogicException, KitchenTaskException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() || !openSheets.contains(sheet) || !sheet.hasKitchenTask(task)) {
            throw new UseCaseLogicException();
        } else if (task.getCook() != null || task.getKitchenShift() != null) {
            throw new KitchenTaskException();
        }

        task.setPrepared(true);
        notifyKitchenTaskUpdated(task);
    }

    public ObservableList<KitchenShift> lookupShiftsBoard() throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            System.err.println("Error in lookupShiftsBoard");
            throw new UseCaseLogicException();
        }
        return KitchenShift.loadAllKitchenShifts();
    }

    public void setKitchenShiftFull(KitchenShift shift, boolean full) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }

        shift.setFull(full);
        notifyKitchenShiftUpdated(shift);
    }




    // EVENT RECEIVERS NOTIFY METHODS

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

    private void notifyKitchenTasksRearranged(ServiceSheet sheet) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTasksRearranged(sheet);
        }
    }

    private void notifyKitchenTaskAssigned(KitchenTask task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskAssigned(task);
        }
    }

    private void notifyKitchenTaskReset(ServiceSheet sheet, KitchenTask task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskReset(sheet, task);
        }
    }

    private void notifyKitchenTaskUpdated(KitchenTask task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskUpdated(task);
        }
    }

    private void notifyKitchenTaskReassigned(KitchenTask task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskReassigned(task);
        }
    }

    private void notifyKitchenTaskDeleted(ServiceSheet sheet, KitchenTask task) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenTaskDeleted(sheet, task);
        }
    }

    private void notifyKitchenShiftUpdated(KitchenShift shift) {
        for (KitchenTaskEventReceiver er : this.eventReceivers) {
            er.updateKitchenShiftUpdated(shift);
        }
    }

    public void addEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public void removeEventReceiver(KitchenTaskEventReceiver rec) {
        this.eventReceivers.remove(rec);
    }
}
