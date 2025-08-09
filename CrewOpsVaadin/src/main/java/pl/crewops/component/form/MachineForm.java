package pl.crewops.component.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.IntStream;
import pl.crewops.component.grid.BreakdownGrid;
import pl.crewops.component.grid.MachineGrid;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.MachineFormModel;
import pl.crewops.view.HomeView;

@CssImport("./styles/component/combo-box.css")
public class MachineForm extends FormLayout {
    private final MachineGrid machineGrid;
    private final BreakdownGrid breakdownGrid;

    TextField registerNumber = new TextField();
    TextField make = new TextField();
    TextField model = new TextField();
    ComboBox<Integer> year = new ComboBox<>();
    ComboBox<String> machineType = new ComboBox<>();
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

        binder.bindInstanceFields(this);

        populateMachineTypes(coreAPI);

        machineType.addClassName("machine-form-machine-type-combobox");
        machineType.getElement().setAttribute("theme", "machine-type-combo");
        machineType.setAllowCustomValue(true);

        machineType.addCustomValueSetListener(event -> {
            String customValue = event.getDetail();
            machineType.setValue(customValue);
        });

        year.addClassName("machine-form-year-combobox");
        year.getElement().setAttribute("theme", "year-combo");

        year.setItems(IntStream.rangeClosed(1980, LocalDate.now().getYear())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .toList());

        add(broken, registerNumber, machineType, make, model, year, serialNumber, createButtonsLayout());
    }

    public void setFormModeSave() {
        registerNumber.setEnabled(true);
        machineType.setEnabled(true);
        make.setEnabled(true);
        model.setEnabled(true);
        year.setEnabled(true);
        serialNumber.setEnabled(true);

        machineType.setValue(null);
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

    public void setMachine(MachineFormModel machineFormModel) {
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
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    private void localize() {
        registerNumber.setLabel(getTranslation("machineForm.registrationNumber"));
        make.setLabel(getTranslation("machineForm.make"));
        model.setLabel(getTranslation("machineForm.model"));
        year.setLabel(getTranslation("machineForm.year"));
        serialNumber.setLabel(getTranslation("machineForm.vin"));
        machineType.setLabel(getTranslation("machineForm.availableMachineTypes.label"));

        save.setText(getTranslation("machineForm.save"));
        update.setText(getTranslation("machineForm.update"));
        delete.setText(getTranslation("machineForm.delete"));
        close.setText(getTranslation("machineForm.close"));
        reportBreakdown.setText(getTranslation("machineForm.reportBreakdown"));
        breakdownsList.setText(getTranslation("machineForm.breakdownsList"));
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        reportBreakdown.addThemeVariants(ButtonVariant.LUMO_WARNING);
        reportBreakdown.setSizeFull();
        breakdownsList.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        breakdownsList.setSizeFull();

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        update.addClickListener(event -> validateAndUpdate());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        reportBreakdown.addClickListener(event -> fireEvent(new ReportBreakdown(this, binder.getBean())));
        breakdownsList.addClickListener(event -> displayBreakdowns(machineGrid, breakdownGrid));

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));

        var buttonsLayout = new HorizontalLayout(save, update, delete, close);
        return new VerticalLayout(buttonsLayout, reportBreakdown, breakdownsList);
    }

    private void validateAndSave() {
        var machineFormModel = MachineFormModel.builder()
                .make(make.getValue())
                .model(model.getValue())
                .year(year.getValue())
                .vin(serialNumber.getValue())
                .registerNumber(registerNumber.getValue())
                .machineType(machineType.getValue())
                .broken(false)
                .build();
        binder.setBean(machineFormModel);
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void validateAndUpdate() {
        if (binder.isValid()) {
            fireEvent(new UpdateEvent(this, binder.getBean()));
        }
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
