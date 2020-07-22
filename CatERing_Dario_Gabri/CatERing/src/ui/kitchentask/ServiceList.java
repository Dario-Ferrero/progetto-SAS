package ui.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.Event;
import businesslogic.event.Service;
import businesslogic.kitchentask.ServiceSheet;
import businesslogic.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class ServiceList {
    private KitchenTaskManagement kitchenTaskManagementController;

    @FXML
    ListView<Service> serviceList;

    ObservableList<Service> serviceListItems;

    @FXML
    Button apriButton;
    @FXML
    Button fineButton;

    HashMap<BorderPane, ServiceSheetDialog> serviceSheetContentMap = new HashMap<>();

    public void setParent(KitchenTaskManagement kitchenTaskManagement) {
        kitchenTaskManagementController = kitchenTaskManagement;
    }

    public void initialize() {
        if (serviceListItems == null) {
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            ObservableList<Event> events = CatERing.getInstance().getEventManager().getEvents();
            serviceListItems = FXCollections.observableArrayList();
            for (Event ev : events) {
                if (ev.getChef() != null && ev.getChef().equals(CatERing.getInstance().getUserManager().getCurrentUser())) {
                    serviceListItems.addAll(ev.getServices());
                }
            }
            serviceList.setItems(serviceListItems);
            serviceList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            serviceList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Service>() {
                @Override
                public void changed(ObservableValue<? extends Service> observableValue, Service oldService, Service newService) {
                    User u = CatERing.getInstance().getUserManager().getCurrentUser();
                    apriButton.setDisable(newService == null || newService.getApprovedMenu() == null);
                }
            });
        } else {
            serviceList.refresh();
        }
    }

    @FXML
    public void apriButtonPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("service-sheet-dialog.fxml"));
        try {
            ObservableList<Event> events = CatERing.getInstance().getEventManager().getEvents();
            Event event = null;
            Service service = serviceList.getSelectionModel().getSelectedItem();
            for (Event e : events) {
                if (e.hasService(service)) {
                    event = e;
                }
            }
            ServiceSheet sheet = CatERing.getInstance().getKitchenTaskManager().openServiceSheet(event, service);

            BorderPane pane = loader.load();
            ServiceSheetDialog controller = loader.getController();
            serviceSheetContentMap.putIfAbsent(pane, controller);

            Stage stage = new Stage();
            controller.setOwnStage(stage);
            stage.initModality(Modality.NONE);
            stage.setTitle("Evento: " + event.getName() + " | Servizio: " + service.getName());
            stage.setScene(new Scene(pane, 760, 480));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (UseCaseLogicException ex) {
            System.err.println("Errore nella use case logic");
            ex.printStackTrace();
        }
    }


    @FXML
    public void fineButtonPressed() { kitchenTaskManagementController.endKitchenTaskManagement(); }

    public void selectService(Service s) {
        if (s != null) this.serviceList.getSelectionModel().select(s);
        else this.serviceList.getSelectionModel().select(-1);
    }
}
