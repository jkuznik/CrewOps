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
import pl.crewops.infrastructure.core.CoreAPI;
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

    public ShiftPanel(ShiftDTO shiftDTO) {
        this.coreAPI = SpringContextBridge.getBean(CoreAPI.class);
        setSizeFull();

        name.setWidthFull();

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

        if (shiftDTO == null) {
            setSummary(VaadinIcon.MEDAL, "Konfiguracja szablonu..");
            name.setPlaceholder("Podaj nazwę zmiany..");
            addNewPositionSelector();
        } else {
            setSummary(VaadinIcon.MEDAL, shiftDTO.name());
            name.setValue(shiftDTO.name());
            save.setVisible(false);
            close.setVisible(false);
            update.setVisible(true);
            delete.setVisible(true);
            shiftDTO.shiftConfigs().forEach(shiftConfig -> {
                JobPositionSelector existingJobPosition = new JobPositionSelector();
                existingJobPosition.configureExistingJobPositions(shiftConfig);

                VerticalLayout positionContainer = createPositionContainer();
                positionContainer.setSpacing(true);
                positionContainer.setPadding(true);
                positionContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

                HorizontalLayout selectorRow = new HorizontalLayout(existingJobPosition);
                selectorRow.setWidthFull();
                selectorRow.setSpacing(true);
                selectorRow.setAlignItems(FlexComponent.Alignment.CENTER);
                selectorRow.expand(existingJobPosition);

                positionContainer.add(selectorRow);

                // --- Listener usuwania ---
                existingJobPosition.addRemoveListener(ev -> {
                    positionContainers.remove(positionContainer);
                    positionsLayout.remove(positionContainer);
                    updateOrderNumbers(); // Aktualizacja numerów po usunięciu
                });

                positionContainers.add(positionContainer);

                existingJobPosition.setOrderNumber(positionContainers.size());

                positionsLayout.addComponentAtIndex(positionsLayout.getComponentCount() - 1, positionContainer);
            });
        }

        mainContainer.add(name, positionsLayout, configuredButtonLayout());

        addContent(mainContainer);
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

        update.addClickListener(event -> {
            fireEvent(new UpdateEvent(this));
        });

        close.addClickListener(event -> {
            this.setVisible(false);
            fireEvent(new CloseEvent(this));
        });

        delete.addClickListener(event -> {
            this.setVisible(false);
            fireEvent(new DeleteEvent(this));
        });
    }

    private void addNewPositionSelector() {
        VerticalLayout positionContainer = createPositionContainer();
        positionContainer.setSpacing(true);
        positionContainer.setPadding(true);
        positionContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        JobPositionSelector selector = new JobPositionSelector();
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
                coreAPI.createShift(createShiftDTO);
                new SuccessNotification("Pomyślnie utworzono zmianę");
            } catch (Exception ex) {
                new FailNotification(getTranslation("failNotification"));
                return;
            }
            setSummary(VaadinIcon.MEDAL, name.getValue().trim());
            save.setVisible(false);
            close.setVisible(false);
            update.setVisible(true);
            delete.setVisible(true);
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
