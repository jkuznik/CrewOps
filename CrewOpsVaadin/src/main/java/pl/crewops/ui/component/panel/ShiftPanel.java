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
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftConfig;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.ui.component.custom.AddButtonPanel;
import pl.crewops.ui.component.custom.JobPositionSelector;
import pl.crewops.ui.component.custom.PanelCustom;
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

    private final Button save = new Button("Save");
    private final Button update = new Button("Update");
    private final Button close = new Button("Close");
    private final Button delete = new Button("Delete");

    private final List<VerticalLayout> positionContainers = new ArrayList<>();

    public ShiftPanel() {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);

        setSummary(VaadinIcon.MEDAL, "Konfiguracja szablonu..");
        setSizeFull();

        name.setWidthFull();
        name.setPlaceholder("Podaj nazwę zmiany..");

        configureButtons();

        addPositionButton.addClickListener(event -> addNewPositionSelector());

        var mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setSpacing(true);
        mainContainer.setPadding(true);
        mainContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        positionsLayout.setWidthFull();
        positionsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        positionsLayout.addClassNames(LumoUtility.Gap.SMALL);
        positionsLayout.setMaxHeight(MAX_POSITIONS_HEIGHT);
        positionsLayout.getStyle().set("overflow-y", "auto");

        VerticalLayout wrappedAddButton = createPositionContainer();
        wrappedAddButton.setMargin(true);
        wrappedAddButton.setMinHeight("50px");
        wrappedAddButton.setHeight("70px");
        wrappedAddButton.add(addPositionButton);

        positionsLayout.add(wrappedAddButton);

        addNewPositionSelector();

        mainContainer.add(name, positionsLayout, configuredButtonLayout());

        addContent(mainContainer);
    }

    public void update(ShiftDTO shiftDTO) {
        name.setValue(shiftDTO.name());
        shiftDTO.jobPositions().forEach(jobPositionDTO -> {
            var existedPosition = createPositionContainer();
            var jobPositionSelector = new JobPositionSelector();
            jobPositionSelector.selectJobPosition(jobPositionDTO);
            positionsLayout.add(existedPosition);
        });
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

    private VerticalLayout createPositionContainer() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(false);
        container.setSpacing(false);
        container.setAlignItems(FlexComponent.Alignment.CENTER);

        container.setHeight(SELECTOR_HEIGHT);
        container.setWidthFull();

        return container;
    }

    private void configureButtons() {
        save.setWidth("50%");
        close.setWidth("50%");
        update.setWidth("50%");
        delete.setWidth("50%");
        update.setVisible(false);
        delete.setVisible(false);

        configureSaveButton();

        close.addClickListener(event -> {
            this.setVisible(false);
            fireEvent(new CloseEvent(this));
        });
    }

    private void addNewPositionSelector() {
        JobPositionSelector selector = new JobPositionSelector();

        try {
            selector.setJobPositions(coreAPI.getAllJobPositions());
            selector.setEmployees(coreAPI.getAllEmployees());
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }

        VerticalLayout positionContainer = createPositionContainer();
        positionContainer.setSpacing(true);
        positionContainer.setPadding(true);
        positionContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        HorizontalLayout selectorRow = new HorizontalLayout(selector);
        selectorRow.setWidthFull();
        selectorRow.setSpacing(true);
        selectorRow.setAlignItems(FlexComponent.Alignment.CENTER);
        selectorRow.expand(selector);

        positionContainer.add(selectorRow);

        // --- Listener usuwania ---
        selector.addRemoveListener(ev -> {
            positionContainers.remove(positionContainer);
            positionsLayout.remove(positionContainer);
            updateOrderNumbers(); // Aktualizacja numerów po usunięciu
        });

        positionContainers.add(positionContainer);

        // NADANIE NUMERU PRZED DODANIEM
        selector.setOrderNumber(positionContainers.size());

        // DODANIE: Dodaj kontener tuż przed kontenerem guzika (który jest na końcu FlexLayout)
        positionsLayout.addComponentAtIndex(positionsLayout.getComponentCount() - 1, positionContainer);
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

            Set<JobPositionDTO> jobPositionsSet = activeSelectors.stream()
                    .map(JobPositionSelector::getSelectedJobPosition)
                    .collect(Collectors.toSet());

            Set<ShiftConfig> configs = activeSelectors.stream()
                    .map(sel -> ShiftConfig.builder()
                            .jopPositionId(sel.getSelectedJobPosition().id())
                            .critical(sel.isCritical())
                            .relatedEmployeeId(sel.getSelectedEmployee()
                                    .map(EmployeeDTO::id)
                                    .orElse(null))
                            .build())
                    .collect(Collectors.toSet());

            CreateShiftDTO createShiftDTO = CreateShiftDTO.builder()
                    .name(name.getValue().trim())
                    .jobPositions(jobPositionsSet)
                    .configs(configs)
                    .build();

            try {
                coreAPI.createShift(createShiftDTO);
                new SuccessNotification("Pomyślnie utworzono zmianę");
            } catch (Exception ex) {
                new FailNotification(getTranslation("failNotification"));
                return;
            }
            save.setVisible(false);
            close.setVisible(false);
            update.setVisible(true);
            delete.setVisible(true);
            setSummary(VaadinIcon.MEDAL, name.getValue().trim());
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

    public static class CloseEvent extends ShiftPanelEvent {
        public CloseEvent(PanelCustom source) {
            super(source);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
