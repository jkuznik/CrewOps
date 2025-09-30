package pl.crewops.component.grid;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import pl.crewops.component.form.BreakdownForm;
import pl.crewops.component.form.MachineForm;
import pl.crewops.component.notification.NotAuthenticatedNotification;
import pl.crewops.component.notification.SuccessNotification;
import pl.crewops.component.notification.guardian.DeleteMachineGuardian;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.BreakdownFormModel;
import pl.crewops.model.MachineFormModel;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.BrowserResolver;

@Slf4j
public class MachineGrid extends VerticalLayout {
    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final TextField filter = new TextField();
    private final Button addMachine = new Button();
    private final HorizontalLayout gridToolbar;

    private final Grid<MachineFormModel> grid = new Grid<>();
    private final MachineForm machineForm;
    private final BreakdownForm breakdownForm = new BreakdownForm();

    private MachineFormModel selectedModel;

    public MachineGrid(CoreAPI coreAPI, BreakdownGrid breakdownGrid, AuthenticationResolver authenticationResolver) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = authenticationResolver;
        this.machineForm = new MachineForm(this, breakdownGrid, coreAPI);
        gridToolbar = getToolbar();

        configureGrid();
        configureForm();

        localize();

        this.addDisplayBreakdownsListener(event -> {
            displayBreakdowns(this, breakdownGrid);
        });

        updateMachineGrid();
        closeEditor();

        setSizeFull();
        add(gridToolbar, getContent());
    }

    private void localize() {
        filter.setPlaceholder(getTranslation("machineGrid.filter.placeholder"));

        addMachine.setText(getTranslation("machineGrid.button.addMachine"));

        setColumnHeader("machineType", "machineGrid.column.machineType");
        setColumnHeader("registrationNumber", "machineGrid.column.registrationNumber");
        setColumnHeader("broken", "machineGrid.column.broken");
        setColumnHeader("make", "machineGrid.column.make");
        setColumnHeader("model", "machineGrid.column.model");
    }

    private void setColumnHeader(String key, String translationKey) {
        grid.getColumnByKey(key).setHeader(getTranslation(translationKey));
    }

    public MachineFormModel getSelectedMachine() {
        return selectedModel;
    }

    private HorizontalLayout getContent() {
        var content = new HorizontalLayout(grid, machineForm, breakdownForm);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, machineForm);
        content.setFlexGrow(1, breakdownForm);
        return content;
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();

        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> updateMachineGrid());

        addMachine.addClickListener(event -> addMachine());

        if (authenticationResolver.principalHasManagerPermission()) {
            toolbar.add(filter, addMachine);
        } else {
            toolbar.add(filter);
        }

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(MachineFormModel::getMachineType).setKey("machineType");

        grid.addColumn(MachineFormModel::getRegisterNumber).setSortable(true).setKey("registrationNumber");

        grid.addColumn(new ComponentRenderer<>(machine -> {
                    if (machine.getBroken()) {
                        Icon cross = VaadinIcon.CLOSE_CIRCLE.create();
                        cross.setColor("red");
                        return cross;
                    } else {
                        Icon check = VaadinIcon.CHECK_CIRCLE.create();
                        check.setColor("green");
                        return check;
                    }
                }))
                .setKey("broken");

        grid.addColumn(MachineFormModel::getMake).setKey("make");
        grid.addColumn(MachineFormModel::getModel).setKey("model");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedModel = event.getValue();
            editMachine(event.getValue());
        });
    }

    private void configureForm() {
        if (BrowserResolver.isMobile()) {
            machineForm.setWidthFull();
        } else {
            machineForm.setWidth("25em");
        }

        machineForm.addSaveListener(this::saveMachine);
        machineForm.addUpdateListener(this::updateMachine);
        machineForm.addDeleteListener(this::deleteMachine);
        machineForm.addCloseListener(event -> closeEditor());
        machineForm.addReportBreakdownListener(this::reportBreakdown);
        breakdownForm.addSaveListener(this::saveBreakdown);
        breakdownForm.addCloseListener(event -> closeEditor());
    }

    public void updateMachineGrid() {
        try {
            List<MachineFormModel> machines = coreAPI.getAllMachines().stream()
                    .map(MachineFormModel::toMachineFormModel)
                    .toList();

            if (filter.getValue() == null || filter.getValue().isBlank()) {
                grid.setItems(machines);
            } else {
                grid.setItems(machines.stream()
                        .filter(machineDTO -> machineDTO
                                .getMachineType()
                                .toLowerCase()
                                .contains(filter.getValue().toLowerCase()))
                        .toList());
            }
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void editMachine(MachineFormModel machineFormModel) {
        breakdownForm.setVisible(false);
        if (machineFormModel == null) {
            closeEditor();
        } else {
            machineForm.setBinderValue(machineFormModel);
            if (authenticationResolver.principalHasMechanicPermission()) {
                machineForm.setFormModeUpdate();
            } else {
                machineForm.setFormModeEmployeePermission();
            }
            machineForm.setVisible(true);

            if (BrowserResolver.isMobile()) {
                grid.setVisible(false);
                gridToolbar.setVisible(false);
                machineForm.setWidthFull();
            }
        }
    }

    private void addMachine() {
        breakdownForm.setVisible(false);
        grid.asSingleSelect().clear();
        machineForm.setFormModeSave();
        machineForm.setVisible(true);

        if (BrowserResolver.isMobile()) {
            grid.setVisible(false);
            gridToolbar.setVisible(false);
            machineForm.setWidthFull();
        }
    }

    private void saveMachine(MachineForm.SaveEvent event) {
        try {
            Optional<MachineDTO> machineDTO =
                    coreAPI.createMachine(MachineFormModel.toCreateMachineDTO(event.getMachine()));
            updateMachineGrid();
            machineForm.populateMachineTypes(coreAPI);
            closeEditor();
            machineDTO.ifPresent(value -> new SuccessNotification(
                    getTranslation("addMachineNotification.messagePrefix") + value.registerNumber()));
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void updateMachine(MachineForm.UpdateEvent event) {
        try {
            Optional<MachineDTO> machineDTO =
                    coreAPI.updateMachine(MachineFormModel.toUpdateMachineDTO(event.getMachine()));
            updateMachineGrid();
            closeEditor();
            machineDTO.ifPresent(value -> new SuccessNotification(
                    getTranslation("updateMachineNotification.messagePrefix") + value.registerNumber()));
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void saveBreakdown(BreakdownForm.SaveEvent event) {
        try {
            log.info("Saving breakdown");
            Optional<BreakdownDTO> breakdownDTO =
                    coreAPI.createBreakdown(BreakdownFormModel.toCreateBreakdownDTO(event.getBreakdown()));
            updateMachineGrid();
            closeEditor();
            breakdownDTO.ifPresent(
                    value -> new SuccessNotification(getTranslation("addBreakdownNotification.successAddBreakdown")
                            + " " + value.machine().registerNumber()));
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }
    }

    private void deleteMachine(MachineForm.DeleteEvent event) {
        new DeleteMachineGuardian(event.getMachine(), () -> {
            try {
                coreAPI.deleteMachine(event.getMachine().getId());
                updateMachineGrid();
                machineForm.populateMachineTypes(coreAPI);
                closeEditor();
            } catch (NotAuthenticatedException e) {
                new NotAuthenticatedNotification(e.getMessage());
            }
        });
    }

    private void reportBreakdown(MachineForm.ReportBreakdown event) {
        machineForm.setVisible(false);

        var breakdownFormModel = new BreakdownFormModel();
        try {
            breakdownFormModel.setId(UUID.randomUUID());
            breakdownFormModel.setMachine(coreAPI.getAllMachines().stream()
                    .filter(machineDTO -> machineDTO
                            .registerNumber()
                            .equals(event.getMachine().getRegisterNumber()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No machine registered with id "
                            + event.getMachine().getRegisterNumber())));
            breakdownFormModel.setReportedBy(EmployeeDTO.builder()
                    .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                    .build());
        } catch (NotAuthenticatedException e) {
            new NotAuthenticatedNotification(e.getMessage());
        }

        breakdownForm.setBreakdown(breakdownFormModel);
        breakdownForm.setFormModeSave();
        breakdownForm.setVisible(true);

        if (BrowserResolver.isMobile()) {
            grid.setVisible(false);
            gridToolbar.setVisible(false);
            breakdownForm.setWidthFull();
        }
    }

    public void closeEditor() {
        machineForm.setBinderValue(null);
        machineForm.setVisible(false);
        breakdownForm.setVisible(false);

        if (BrowserResolver.isMobile()) {
            grid.setVisible(true);
            gridToolbar.setVisible(true);
        }
    }

    public void displayBreakdowns(MachineGrid machineGrid, BreakdownGrid breakdownGrid) {
        fireEvent(new DisplayBreakdownsEvent(machineGrid, breakdownGrid));
    }

    public abstract static class MachineGridEvent extends ComponentEvent<MachineGrid> {

        protected MachineGridEvent(MachineGrid source, BreakdownGrid breakdownGrid) {
            super(source, false);
            source.setVisible(false);
            breakdownGrid.setTypeFilter(source.getSelectedMachine().getRegisterNumber());
            breakdownGrid.setVisible(true);
        }
    }

    public static class DisplayBreakdownsEvent extends MachineGridEvent {
        DisplayBreakdownsEvent(MachineGrid source, BreakdownGrid breakdownGrid) {
            super(source, breakdownGrid);
        }
    }

    public Registration addDisplayBreakdownsListener(ComponentEventListener<DisplayBreakdownsEvent> listener) {
        return addListener(DisplayBreakdownsEvent.class, listener);
    }
}
