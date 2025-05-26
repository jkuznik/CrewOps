package pl.crewops.view.component.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.IntStream;
import pl.crewops.model.VehicleFormModel;
import pl.crewops.view.component.grid.BreakdownGrid;
import pl.crewops.view.component.grid.VehicleGrid;

public class VehicleForm extends FormLayout {
    private final VehicleGrid vehicleGrid;
    private final BreakdownGrid breakdownGrid;

    // TODO: implement text field setEnable(false) for update action
    TextField registrationNumber = new TextField("Registration Number");
    // TODO: implement this

    //    TextField vehicleType = new TextField("Vehicle Type");
    TextField make = new TextField("Make");
    TextField model = new TextField("Model");
    ComboBox<Integer> year = new ComboBox<>("Year");
    TextField vin = new TextField("Vin");
    Checkbox broken = new Checkbox("Broken");

    Button save = new Button("Save");
    Button update = new Button("Update");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Button reportBreakdown = new Button("Report Breakdown");
    Button breakdownsList = new Button("Breakdowns List");

    Binder<VehicleFormModel> binder = new Binder<>(VehicleFormModel.class);

    public VehicleForm(VehicleGrid vehicleGrid, BreakdownGrid breakdownGrid) {
        addClassName("vehicle-form");

        localize();

        this.vehicleGrid = vehicleGrid;
        this.breakdownGrid = breakdownGrid;

        binder.bindInstanceFields(this);

        year.addClassName("vehicle-form-year-combobox");
        // TODO: find way to change item background color
        year.setItems(IntStream.rangeClosed(1980, LocalDate.now().getYear())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .toList());

        add(registrationNumber, /*vehicleType,*/ broken, make, model, year, vin, createButtonsLayout());
    }

    public void setFormModeSave() {
        save.setVisible(true);
        update.setVisible(false);
        reportBreakdown.setVisible(false);
        breakdownsList.setVisible(false);
        delete.setVisible(false);
    }

    public void setFormModeUpdate() {
        save.setVisible(false);
        update.setVisible(true);
        reportBreakdown.setVisible(true);
        breakdownsList.setVisible(true);
        delete.setVisible(true);
    }

    private void localize() {
        registrationNumber.setLabel(getTranslation("vehicleForm.registrationNumber"));
        make.setLabel(getTranslation("vehicleForm.make"));
        model.setLabel(getTranslation("vehicleForm.model"));
        year.setLabel(getTranslation("vehicleForm.year"));
        vin.setLabel(getTranslation("vehicleForm.vin"));
        broken.setLabel(getTranslation("vehicleForm.broken"));

        save.setText(getTranslation("vehicleForm.save"));
        update.setText(getTranslation("vehicleForm.update"));
        delete.setText(getTranslation("vehicleForm.delete"));
        close.setText(getTranslation("vehicleForm.close"));
        reportBreakdown.setText(getTranslation("vehicleForm.reportBreakdown"));
        breakdownsList.setText(getTranslation("vehicleForm.breakdownsList"));
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        reportBreakdown.addThemeVariants(ButtonVariant.LUMO_ERROR);
        reportBreakdown.setSizeFull();
        breakdownsList.addThemeVariants(ButtonVariant.LUMO_WARNING);
        breakdownsList.setSizeFull();

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        reportBreakdown.addClickListener(event -> fireEvent(new ReportBreakdown(this, binder.getBean())));
        breakdownsList.addClickListener(event -> displayBreakdowns(vehicleGrid, breakdownGrid));

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));

        var buttonsLayout = new HorizontalLayout(save, update, delete, close);
        return new VerticalLayout(buttonsLayout, reportBreakdown, breakdownsList);
    }

    private void validateAndSave() {
        var vehicleFormModel = VehicleFormModel.builder()
                .make(make.getValue())
                .model(model.getValue())
                .year(year.getValue())
                .vin(vin.getValue())
                .broken(broken.getValue())
                .registrationNumber(registrationNumber.getValue())
                .build();
        binder.setBean(vehicleFormModel);
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    public void displayBreakdowns(VehicleGrid vehicleGrid, BreakdownGrid breakdownGrid) {
        fireEvent(new DisplayBreakdownsEvent(vehicleGrid, breakdownGrid));
    }

    public void setVehicle(VehicleFormModel vehicleFormModel) {
        binder.setBean(vehicleFormModel);
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

    public static class UpdateEvent extends VehicleFormEvent {

        public UpdateEvent(VehicleForm source, VehicleFormModel vehicleFormModel) {
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

    public static class ReportBreakdown extends VehicleFormEvent {

        ReportBreakdown(VehicleForm source, VehicleFormModel vehicleFormModel) {
            super(source, vehicleFormModel);
        }
    }

    public static class DisplayBreakdownsEvent extends VehicleGrid.VehicleGridEvent {

        DisplayBreakdownsEvent(VehicleGrid source, BreakdownGrid breakdownGrid) {
            super(source, breakdownGrid);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    public Registration addReportBreakdownListener(ComponentEventListener<ReportBreakdown> listener) {
        return addListener(ReportBreakdown.class, listener);
    }
}
