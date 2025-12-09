package pl.crewops.ui.component.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.IntStream;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.MachineFormModel;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.ui.component.custom.ComboBoxCustom;
import pl.crewops.ui.component.grid.BreakdownGrid;
import pl.crewops.ui.component.grid.MachineGrid;
import pl.crewops.ui.component.notification.NotAuthenticatedNotification;

public class MachineForm extends FormLayout {
    private final MachineGrid machineGrid;
    private final BreakdownGrid breakdownGrid;

    TextField registerNumber = new TextField();
    TextField make = new TextField();
    TextField model = new TextField();
    ComboBoxCustom<Integer> year = new ComboBoxCustom<>();
    ComboBoxCustom<String> machineType = new ComboBoxCustom<>();
    TextField serialNumber = new TextField();
    Span broken = new Span();

    Button save = new Button();
    Button update = new Button();
    Button delete = new Button();
    Button close = new Button();

    Button reportBreakdown = new Button();
    Button breakdownsList = new Button();

    Binder<MachineFormModel> binder = new Binder<>(MachineFormModel.class);

    public MachineForm(MachineGrid machineGrid, BreakdownGrid breakdownGrid, CoreAPI coreAPI) {
        addClassName("machine-form");

        localize();

        this.machineGrid = machineGrid;
        this.breakdownGrid = breakdownGrid;

        configureBinder();

        populateMachineTypes(coreAPI);

        machineType.setAllowCustomValue(true);

        machineType.addCustomValueSetListener(event -> {
            String customValue = event.getDetail();
            machineType.setValue(customValue);
        });

        year.setItems(IntStream.rangeClosed(1980, LocalDate.now().getYear())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .toList());

        add(broken, registerNumber, machineType, make, model, year, serialNumber, createButtonsLayout());
    }

    private void configureBinder() {
        binder.forField(make)
                .asRequired(getTranslation("validation.required"))
                .withValidator(new StringLengthValidator(getTranslation("validation.length.minmax", 2, 31), 2, 31))
                .bind(MachineFormModel::getMake, MachineFormModel::setMake);

        binder.forField(model)
                .asRequired(getTranslation("validation.required"))
                .withValidator(new StringLengthValidator(getTranslation("validation.length.minmax", 2, 31), 2, 31))
                .bind(MachineFormModel::getModel, MachineFormModel::setModel);

        binder.forField(machineType)
                .asRequired(getTranslation("validation.required"))
                .withValidator(new StringLengthValidator(getTranslation("validation.length.minmax", 2, 31), 2, 31))
                .bind(MachineFormModel::getMachineType, MachineFormModel::setMachineType);

        binder.forField(year)
                .asRequired(getTranslation("validation.required"))
                .bind(MachineFormModel::getYear, MachineFormModel::setYear);

        binder.forField(serialNumber)
                .withValidator(
                        value -> value == null || value.isEmpty() || (value.length() >= 2 && value.length() <= 50),
                        getTranslation("validation.length.optional", 2, 50))
                .bind(MachineFormModel::getVin, MachineFormModel::setVin);

        binder.forField(registerNumber)
                .withValidator(
                        value -> value == null || value.isEmpty() || (value.length() >= 2 && value.length() <= 15),
                        getTranslation("validation.length.optional", 2, 15))
                .bind(MachineFormModel::getRegisterNumber, MachineFormModel::setRegisterNumber);
    }

    public void setFormModeSave() {
        registerNumber.setEnabled(true);
        machineType.setEnabled(true);
        make.setEnabled(true);
        model.setEnabled(true);
        year.setEnabled(true);
        serialNumber.setEnabled(true);

        binder.setBean(MachineFormModel.builder().broken(false).build());

        save.setVisible(true);
        update.setVisible(false);
        reportBreakdown.setVisible(false);
        breakdownsList.setVisible(false);
        delete.setVisible(false);
    }

    public void setFormModeUpdate() {
        registerNumber.setEnabled(true);
        machineType.setEnabled(false);
        make.setEnabled(false);
        model.setEnabled(false);
        year.setEnabled(false);
        serialNumber.setEnabled(false);

        save.setVisible(false);
        update.setVisible(true);
        delete.setVisible(true);

        reportBreakdown.setVisible(true);
        breakdownsList.setVisible(true);
    }

    public void setFormModeEmployeePermission() {
        registerNumber.setEnabled(false);
        machineType.setEnabled(false);
        make.setEnabled(false);
        model.setEnabled(false);
        year.setEnabled(false);
        serialNumber.setEnabled(false);

        save.setVisible(false);
        update.setVisible(false);
        delete.setVisible(false);

        reportBreakdown.setVisible(true);
        breakdownsList.setVisible(true);
    }

    public void setBinderValue(MachineFormModel machineFormModel) {
        binder.setBean(machineFormModel);
        if (machineFormModel != null) {
            broken.setVisible(true);

            machineType.setValue(machineFormModel.getMachineType());

            if (machineFormModel.getBroken()) {
                broken.setText(getTranslation("machineForm.broken.true"));
                broken.getStyle().set("color", "red");
            } else {
                broken.setText(getTranslation("machineForm.broken.false"));
                broken.getStyle().set("color", "green");
            }
        } else {
            broken.setVisible(false);
        }
    }

    public void displayBreakdowns(MachineGrid machineGrid, BreakdownGrid breakdownGrid) {
        fireEvent(new DisplayBreakdownsEvent(machineGrid, breakdownGrid));
    }

    public void populateMachineTypes(CoreAPI coreAPI) {
        try {
            machineType.setItems(coreAPI.getAllMachineTypes().stream()
                    .map(MachineTypeDTO::name)
                    .sorted()
                    .toList());
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void validateAndSave() {
        if (binder.writeBeanIfValid(binder.getBean())) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.writeBeanIfValid(binder.getBean())) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.getElement().getStyle().set("background-color", "#FFA500");
        delete.getElement().getStyle().set("color", "#333333");
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        reportBreakdown.addThemeVariants(ButtonVariant.LUMO_WARNING);
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
        breakdownsList.addClickListener(event -> displayBreakdowns(machineGrid, breakdownGrid));

        binder.addStatusChangeListener(event -> save.setEnabled(!event.hasValidationErrors()));

        var buttonsLayout = new HorizontalLayout(save, update, delete, close);
        return new VerticalLayout(buttonsLayout, reportBreakdown, breakdownsList);
    }

    private void localize() {
        registerNumber.setLabel(getTranslation("machineForm.registrationNumber"));
        make.setLabel(getTranslation("machineForm.make"));
        model.setLabel(getTranslation("machineForm.model"));
        year.setLabel(getTranslation("machineForm.year"));
        serialNumber.setLabel(getTranslation("machineForm.vin"));
        machineType.setLabel(getTranslation("machineForm.availableMachineTypes.label"));

        save.setText(getTranslation("saveButton"));
        update.setText(getTranslation("updateButton"));
        delete.setText(getTranslation("deleteButton"));
        close.setText(getTranslation("closeButton"));
        reportBreakdown.setText(getTranslation("machineForm.reportBreakdown"));
        breakdownsList.setText(getTranslation("machineForm.breakdownsList"));
    }

    public abstract static class MachineFormEvent extends ComponentEvent<MachineForm> {

        private MachineFormModel machineFormModel;

        protected MachineFormEvent(MachineForm source, MachineFormModel machineFormModel) {
            super(source, false);
            this.machineFormModel = machineFormModel;
        }

        public MachineFormModel getMachine() {
            return machineFormModel;
        }
    }

    public static class SaveEvent extends MachineFormEvent {

        SaveEvent(MachineForm source, MachineFormModel machineFormModel) {
            super(source, machineFormModel);
        }
    }

    public static class UpdateEvent extends MachineFormEvent {

        public UpdateEvent(MachineForm source, MachineFormModel machineFormModel) {
            super(source, machineFormModel);
        }
    }

    public static class DeleteEvent extends MachineFormEvent {

        DeleteEvent(MachineForm source, MachineFormModel machineFormModel) {
            super(source, machineFormModel);
        }
    }

    public static class CloseEvent extends MachineFormEvent {

        CloseEvent(MachineForm source) {
            super(source, null);
        }
    }

    public static class ReportBreakdown extends MachineFormEvent {

        ReportBreakdown(MachineForm source, MachineFormModel machineFormModel) {
            super(source, machineFormModel);
        }
    }

    public static class DisplayBreakdownsEvent extends MachineGrid.MachineGridEvent {

        DisplayBreakdownsEvent(MachineGrid source, BreakdownGrid breakdownGrid) {
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
