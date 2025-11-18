package pl.crewops.ui.component.panel;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftConfig;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.dto.shift.UpdateShiftDTO;
import pl.crewops.ui.component.custom.AddButtonPanel;
import pl.crewops.ui.component.custom.PanelCustom;
import pl.crewops.ui.component.custom.schedule.JobPositionSelector;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.util.SpringContextBridge;

public class ShiftPanel extends PanelCustom {

    private static final String MAX_POSITIONS_HEIGHT = "300px";
    private static final String SELECTOR_HEIGHT = "100px";

    private final CoreAPI coreAPI;

    private final TextField name = new TextField();

    private final FlexLayout positionsLayout = new FlexLayout();
    private final AddButtonPanel addPositionButton = new AddButtonPanel();

    // todo i18n
    private final Button save = new Button("Save");
    private final Button update = new Button("Update");
    private final Button close = new Button("Close");
    private final Button delete = new Button("Delete");

    private final List<VerticalLayout> positionContainers = new ArrayList<>();

    private final List<JobPositionDTO> allAvailablePositions;
    private ShiftDTO shiftDTO;

    public ShiftPanel(ShiftDTO shiftDTO, List<JobPositionDTO> allAvailablePositions) {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        this.shiftDTO = shiftDTO;
        this.allAvailablePositions = allAvailablePositions;
        setSizeFull();

        name.setWidthFull();

        configurePositionsLayout();
        configureButtons();

        var mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setSpacing(true);
        mainContainer.setPadding(true);
        mainContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        configureShiftPanelDependsShiftDTO();

        mainContainer.add(name, positionsLayout, configuredButtonLayout());

        addContent(mainContainer);
    }

    private void configureShiftPanelDependsShiftDTO() {
        if (shiftDTO == null) {
            this.addClassName("config-mode");

            setSummary(VaadinIcon.MEDAL, "Konfiguracja szablonu..");
            name.setPlaceholder("Podaj nazwę zmiany..");

            addJobPositionRow(new JobPositionSelector(allAvailablePositions));

            save.setVisible(true);
            close.setVisible(true);
        } else {
            setSummary(VaadinIcon.MEDAL, shiftDTO.name());
            name.setValue(shiftDTO.name());

            shiftDTO.shiftConfigs().forEach(shiftConfig -> {
                JobPositionSelector existingConfiguredJobPosition = new JobPositionSelector(allAvailablePositions);
                existingConfiguredJobPosition.configureExistingJobPositions(shiftConfig);

                addJobPositionRow(existingConfiguredJobPosition);
            });

            update.setVisible(true);
            delete.setVisible(true);
        }
    }

    private void addJobPositionRow(JobPositionSelector jobPositionSelector) {
        var existedJobPositionContainer = new HorizontalLayout(jobPositionSelector);
        existedJobPositionContainer.setWidthFull();
        existedJobPositionContainer.setSpacing(true);
        existedJobPositionContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        existedJobPositionContainer.expand(jobPositionSelector);

        var positionContainer = createPositionContainer(existedJobPositionContainer);
        positionContainer.setSpacing(true);
        positionContainer.setPadding(true);
        positionContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        addJobPositionSelectorsListeners(jobPositionSelector, positionContainer);

        positionContainers.add(positionContainer);
        jobPositionSelector.setOrderNumber(positionContainers.size());
        positionsLayout.addComponentAtIndex(positionsLayout.getComponentCount() - 1, positionContainer);
    }

    private void addJobPositionSelectorsListeners(
            JobPositionSelector jobPositionSelector, VerticalLayout positionContainer) {
        jobPositionSelector.addSelectionJobPositionListener(this::handleJobPositionValueChange);

        jobPositionSelector.addRemoveListener(ev -> {
            positionContainers.remove(positionContainer);
            positionsLayout.remove(positionContainer);

            updateOrderNumbers();
            handleAddPositionToItems(ev);
        });
    }

    private void configurePositionsLayout() {
        positionsLayout.setWidthFull();
        positionsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        positionsLayout.addClassNames(LumoUtility.Gap.SMALL);
        positionsLayout.setMaxHeight(MAX_POSITIONS_HEIGHT);
        positionsLayout.getStyle().set("overflow-y", "auto");
    }

    private void configureAddPositionButton() {
        VerticalLayout addButtonWrapper = createPositionContainer(addPositionButton);
        addButtonWrapper.setMargin(true);
        addButtonWrapper.setMinHeight("50px");
        addButtonWrapper.setHeight("70px");
        addPositionButton.addClickListener(event -> addJobPositionRow(new JobPositionSelector(allAvailablePositions)));
        positionsLayout.add(addButtonWrapper);
    }

    private void handleAddPositionToItems(JobPositionSelector.RemoveEvent event) {
        var positionToRevert = event.getValue();
        if (positionToRevert != null) {
            // Przywróć starą wartość we wszystkich INNYCH selektorach
            positionContainers.stream()
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof HorizontalLayout)
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof JobPositionSelector)
                    .map(c -> (JobPositionSelector) c)
                    .filter(selector -> selector != event.getSource()) // Pominięcie aktualnego selektora
                    .forEach(selector -> selector.addJobPositionToItems(positionToRevert));
        }
    }

    private void handleJobPositionValueChange(JobPositionSelector.SelectedJobPositionEvent event) {
        JobPositionDTO oldValue = event.getOldValue();
        JobPositionDTO newValue = event.getNewValue();

        // 1. ZWOLNIENIE starego stanowiska
        if (oldValue != null) {
            // Przywróć starą wartość we wszystkich INNYCH selektorach
            positionContainers.stream()
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof HorizontalLayout)
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof JobPositionSelector)
                    .map(c -> (JobPositionSelector) c)
                    .filter(selector -> selector != event.getSource()) // Pominięcie aktualnego selektora
                    .forEach(selector -> selector.addJobPositionToItems(oldValue));
        }

        // 2. REZERWACJA nowego stanowiska
        if (newValue != null) {
            // Usuń nową wartość ze wszystkich INNYCH selektorów
            positionContainers.stream()
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof HorizontalLayout)
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof JobPositionSelector)
                    .map(c -> (JobPositionSelector) c)
                    .filter(selector -> selector != event.getSource()) // Pominięcie aktualnego selektora
                    .forEach(selector -> selector.removeJobPositionFromItems(newValue));
        }
    }

    private void updateOrderNumbers() {
        for (int i = 0; i < positionContainers.size(); i++) {
            VerticalLayout container = positionContainers.get(i);
            int finalI = i;
            container
                    .getChildren()
                    .filter(c -> c instanceof HorizontalLayout)
                    .flatMap(c -> ((HorizontalLayout) c).getChildren())
                    .filter(c -> c instanceof JobPositionSelector)
                    .map(c -> (JobPositionSelector) c)
                    .findFirst()
                    .ifPresent(selector -> selector.setOrderNumber(finalI + 1));
        }
    }

    private VerticalLayout createPositionContainer(Component component) {
        VerticalLayout container = new VerticalLayout(component);
        container.setPadding(false);
        container.setSpacing(false);
        container.setAlignItems(FlexComponent.Alignment.CENTER);

        container.setHeight(SELECTOR_HEIGHT);
        container.setWidthFull();

        return container;
    }

    private void configureButtons() {
        configureAddPositionButton();

        save.setWidth("50%");
        close.setWidth("50%");
        update.setWidth("50%");
        delete.setWidth("50%");
        save.setVisible(false);
        close.setVisible(false);
        update.setVisible(false);
        delete.setVisible(false);

        configureSaveButton();

        configureUpdateButton();

        close.addClickListener(event -> {
            this.setVisible(false);
            fireEvent(new CloseEvent(this));
        });

        delete.addClickListener(event -> {
            try {
                coreAPI.deleteShiftById(shiftDTO.id());
            } catch (NotAuthenticatedException e) {
                new FailNotification(e.getMessage());
            }
            fireEvent(new DeleteEvent(this));
            this.removeFromParent();
        });
    }

    private void configureSaveButton() {
        save.addClickListener(e -> {
            if (name.getValue() == null || name.getValue().trim().isEmpty()) {
                name.focus();
                name.setErrorMessage("Nazwa zespołu jest wymagana");
                name.setInvalid(true);
                return;
            } else {
                name.setInvalid(false);
            }

            List<JobPositionSelector> activeSelectors = positionContainers.stream()
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof HorizontalLayout)
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof JobPositionSelector)
                    .map(c -> (JobPositionSelector) c)
                    .toList();

            if (activeSelectors.isEmpty()) {
                Notification.show("Dodaj przynajmniej jedno stanowisko");
                return;
            }

            for (JobPositionSelector sel : activeSelectors) {
                if (!sel.validate()) return;
            }

            Set<ShiftConfig> configs = activeSelectors.stream()
                    .map(sel -> ShiftConfig.builder()
                            .jopPosition(sel.getSelectedJobPosition())
                            .critical(sel.isCritical())
                            .relatedEmployee(sel.getSelectedEmployee().orElse(null))
                            .build())
                    .collect(Collectors.toSet());

            CreateShiftDTO createShiftDTO = CreateShiftDTO.builder()
                    .name(name.getValue().trim())
                    .configs(configs)
                    .build();

            try {
                shiftDTO = coreAPI.createShift(createShiftDTO).orElse(shiftDTO);
                new SuccessNotification("Pomyślnie utworzono zmianę");
            } catch (Exception ex) {
                new FailNotification(getTranslation("failNotification"));
                return;
            }

            this.removeClassName("config-mode");
            setSummary(VaadinIcon.MEDAL, name.getValue().trim());
            save.setVisible(false);
            close.setVisible(false);
            update.setVisible(true);
            delete.setVisible(true);
        });
    }

    private void configureUpdateButton() {
        update.addClickListener(event -> {
            if (name.getValue() == null || name.getValue().trim().isEmpty()) {
                name.focus();
                name.setErrorMessage("Nazwa zespołu jest wymagana");
                name.setInvalid(true);
                return;
            } else {
                name.setInvalid(false);
            }

            List<JobPositionSelector> activeSelectors = positionContainers.stream()
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof HorizontalLayout)
                    .flatMap(Component::getChildren)
                    .filter(c -> c instanceof JobPositionSelector)
                    .map(c -> (JobPositionSelector) c)
                    .toList();

            if (activeSelectors.isEmpty()) {
                Notification.show("Dodaj przynajmniej jedno stanowisko");
                return;
            }

            for (JobPositionSelector sel : activeSelectors) {
                if (!sel.validate()) return;
            }

            Set<ShiftConfig> configs = activeSelectors.stream()
                    .map(sel -> ShiftConfig.builder()
                            .jopPosition(sel.getSelectedJobPosition())
                            .critical(sel.isCritical())
                            .relatedEmployee(sel.getSelectedEmployee().orElse(null))
                            .build())
                    .collect(Collectors.toSet());

            UpdateShiftDTO updateShiftDTO = UpdateShiftDTO.builder()
                    .id(shiftDTO.id())
                    .name(name.getValue().trim())
                    .configs(configs)
                    .build();

            try {
                coreAPI.updateShift(updateShiftDTO);
                new SuccessNotification("Pomyślnie zaktualizowano zmianę");
                fireEvent(new UpdateEvent(this));
                setSummary(VaadinIcon.MEDAL, name.getValue().trim());
            } catch (Exception ex) {
                new FailNotification(getTranslation("failNotification"));
            }
        });
    }

    private HorizontalLayout configuredButtonLayout() {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(true);
        buttonLayout.add(save, close, update, delete);
        return buttonLayout;
    }

    public abstract static class ShiftPanelEvent extends ComponentEvent<PanelCustom> {
        public ShiftPanelEvent(PanelCustom source) {
            super(source, false);
        }
    }

    public static class SaveEvent extends ShiftPanelEvent {
        public SaveEvent(PanelCustom source) {
            super(source);
        }
    }

    public static class UpdateEvent extends ShiftPanelEvent {
        public UpdateEvent(PanelCustom source) {
            super(source);
        }
    }

    public static class CloseEvent extends ShiftPanelEvent {
        public CloseEvent(PanelCustom source) {
            super(source);
        }
    }

    public static class DeleteEvent extends ShiftPanelEvent {
        public DeleteEvent(PanelCustom source) {
            super(source);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }
}
