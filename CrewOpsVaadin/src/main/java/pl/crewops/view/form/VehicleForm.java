package pl.crewops.view.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.view.model.VehicleFormModel;

@SpringComponent

// TODO: config vehicle type binding or keep focus on binding VehicleFormModel instead of VehicleDTO
public class VehicleForm extends FormLayout {
    TextField registrationNumber = new TextField("Registration Number");
    //    TextField vehicleType = new TextField("Vehicle Type");
    TextField make = new TextField("Make");
    TextField model = new TextField("Model");
    TextField year = new TextField("Year");
    TextField vin = new TextField("Vin");
    Checkbox broken = new Checkbox("Broken");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Binder<VehicleDTO> binder = new Binder<>(VehicleDTO.class);

    VehicleFormModel binderModel = new VehicleFormModel();

    public VehicleForm(CoreAPI coreAPI) {
        addClassName("vehicle-form");

        binder.bindInstanceFields(this);

        add(registrationNumber, /*vehicleType,*/ broken, make, model, year, vin);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        delete.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binderModel)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(event -> save.setEnabled(modelValidation(binderModel)));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (modelValidation(binderModel)) {
            fireEvent(new SaveEvent(this, binderModel));
        }
    }

    public void setVehicle(VehicleDTO vehicleDTO) {
        //        binder.readBean(vehicleDTO);
        if (vehicleDTO != null) {
            setModelValues(vehicleDTO);
        }
    }

    public abstract static class VehicleFormEvent extends ComponentEvent<VehicleForm> {

        private VehicleFormModel vehicleFormModel;

        protected VehicleFormEvent(VehicleForm source, VehicleFormModel vehicleFormModel) {
            super(source, false);
            this.vehicleFormModel = vehicleFormModel;
        }

        public VehicleFormModel getVehicle() {
            return vehicleFormModel;
        }
    }

    public static class SaveEvent extends VehicleFormEvent {

        SaveEvent(VehicleForm source, VehicleFormModel vehicleFormModel) {
            super(source, vehicleFormModel);
        }
    }

    public static class DeleteEvent extends VehicleFormEvent {

        DeleteEvent(VehicleForm source, VehicleFormModel vehicleFormModel) {
            super(source, vehicleFormModel);
        }
    }

    public static class CloseEvent extends VehicleFormEvent {

        CloseEvent(VehicleForm source) {
            super(source, null);
        }
    }

    // ask gpt why return method addListener and what exactly it does
    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    private void setModelValues(VehicleDTO vehicleDTO) {
        binderModel.setId(vehicleDTO.id());
        binderModel.setMake(vehicleDTO.make());
        binderModel.setModel(vehicleDTO.model());
        //        binderModel.setVehicleType(vehicleDTO.vehicleType().toString());
        binderModel.setYear(vehicleDTO.year());
        binderModel.setVin(vehicleDTO.vin());
        binderModel.setRegistrationNumber(vehicleDTO.registerNumber());
        binderModel.setBroken(vehicleDTO.broken());
    }

    private boolean modelValidation(VehicleFormModel vehicleFormModel) {
        vehicleFormModel.setRegistrationNumber(registrationNumber.getValue());

        return !vehicleFormModel.getRegistrationNumber().isEmpty();
    }
}
