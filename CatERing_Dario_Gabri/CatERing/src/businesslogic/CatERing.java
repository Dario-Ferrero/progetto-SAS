package businesslogic;

import businesslogic.event.EventManager;
import businesslogic.kitchentask.KitchenTaskManager;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.user.UserManager;
import persistence.KitchenTaskPersistence;
import persistence.MenuPersistence;
import persistence.PersistenceManager;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private KitchenTaskManager kitchenMgr;

    private MenuPersistence menuPersistence;
    private KitchenTaskPersistence taskPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        kitchenMgr = new KitchenTaskManager();
        menuPersistence = new MenuPersistence();
        taskPersistence = new KitchenTaskPersistence();
        menuMgr.addEventReceiver(menuPersistence);
        kitchenMgr.addEventReceiver(taskPersistence);
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }

    public KitchenTaskManager getKitchenTaskManager() { return this.kitchenMgr; }

}
