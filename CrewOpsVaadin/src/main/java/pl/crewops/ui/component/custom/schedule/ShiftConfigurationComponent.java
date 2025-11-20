package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.ui.component.custom.AddButtonPanel;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.notification.InfoNotification;
import pl.crewops.ui.component.panel.ShiftPanel;

@CssImport("./styles/component/schedule-content-component.css")
public class ShiftConfigurationComponent extends VerticalLayout {

    private final CoreAPI coreAPI;

    private static final String PANEL_HEIGHT = "550px";
    private static final String PANEL_WIDTH = "400px";

    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_DOWN.create());

    private final List<VerticalLayout> shiftContainers = new ArrayList<>();
    private final FlexLayout panelsLayout = new FlexLayout();
    private final AddButtonPanel addShiftPanelButton = new AddButtonPanel();

    private final VerticalLayout scrollableContainer = new VerticalLayout(panelsLayout);
    private boolean isContentVisible = false;
    private boolean isAnyShiftExist = false;

    private final List<JobPositionDTO> allAvailableJobPositions = new ArrayList<>();

    public ShiftConfigurationComponent(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        try {
            this.allAvailableJobPositions.addAll(coreAPI.getAllJobPositions());
        } catch (NotAuthenticatedException e) {

            new FailNotification(e.getMessage());
        }
        addClassName("component-content-border");
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);

        add(createToolbar(), scrollableContainer);

        configureScrollableContainer();
        configureShiftsLayout();

        generateShiftPanelsForExistedShifts();
    }

    private void configureShiftsLayout() {
        panelsLayout.setWidthFull();
        panelsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        panelsLayout.setJustifyContentMode(JustifyContentMode.START);
        panelsLayout.setAlignItems(Alignment.START);
    }

    private void configureScrollableContainer() {
        scrollableContainer.setWidthFull();
        scrollableContainer.setPadding(true);
        scrollableContainer.setSpacing(true);
        scrollableContainer.setAlignItems(Alignment.CENTER);
        scrollableContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        scrollableContainer.setVisible(isContentVisible);
    }

    private void generateShiftPanelsForExistedShifts() {
        panelsLayout.removeAll();
        shiftContainers.clear();

        injectConfiguredAddButtonPanelToShiftsLayout();

        try {
            List<ShiftDTO> allShifts = coreAPI.getAllShifts();
            allShifts.forEach(shift -> {
                var existedShift = new ShiftPanel(shift, new ArrayList<>(allAvailableJobPositions));
                shiftPanelUpdateButtonListener(existedShift);
                shiftPanelDeleteButtonListener(existedShift);

                VerticalLayout existedShiftContainer = createNewPanel(existedShift);
                existedShiftContainer.setMargin(true);

                shiftContainers.add(existedShiftContainer);
                isAnyShiftExist = true;
                panelsLayout.addComponentAtIndex(panelsLayout.getComponentCount() - 1, existedShiftContainer);
            });

            if (!isAnyShiftExist) {
                addShiftPanel();
            }
        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }
    }

    private void shiftPanelUpdateButtonListener(ShiftPanel existedShift) {
        existedShift.addUpdateListener(event -> {
            // todo Update medtod
            new InfoNotification(
                    "Cała logika jest zawarta bezpośrednio w ShiftPanel, tutaj jest tylko przekazanie info o wystapieniu eventu, jeśli to jest zbędne to usunąć");
        });
    }

    private void shiftPanelDeleteButtonListener(ShiftPanel existedShift) {
        existedShift.addDeleteListener(event -> {

            // todo implement delete guardian notification
            new InfoNotification("Cała logika zwiazana z zapytaniem do BE jest zawarta bezpośrednio w ShiftPanel, "
                    + "a reszta związana z zmianą widoku dzieję się w ShiftConfigurationComponent, zastanowić się czy nie przenieść całości "
                    + "tutaj i dodać GuardianNotification");
            VerticalLayout containerToRemove = shiftContainers.stream()
                    .filter(container -> container.getChildren().anyMatch(c -> c == existedShift))
                    .findFirst()
                    .orElse(null);

            if (containerToRemove != null) {
                shiftContainers.remove(containerToRemove);
                panelsLayout.remove(containerToRemove);
            }
        });
    }

    private void toggleContentVisibility() {
        isContentVisible = !isContentVisible;
        scrollableContainer.setVisible(isContentVisible);

        toggleVisibilityButton.setIcon(
                isContentVisible ? VaadinIcon.ANGLE_UP.create() : VaadinIcon.ANGLE_DOWN.create());
    }

    private void addShiftPanel() {
        var configurableShiftPanel = new ShiftPanel(null, new ArrayList<>(allAvailableJobPositions));
        shiftPanelUpdateButtonListener(configurableShiftPanel);
        shiftPanelDeleteButtonListener(configurableShiftPanel);

        configurableShiftPanel.addCloseListener(event -> {
            VerticalLayout shiftContainerToRemove = shiftContainers.stream()
                    .filter(container -> container.getChildren().anyMatch(c -> c == configurableShiftPanel))
                    .findFirst()
                    .orElse(null);

            if (shiftContainerToRemove != null) {
                shiftContainers.remove(shiftContainerToRemove);
                panelsLayout.remove(shiftContainerToRemove);
            }
        });

        VerticalLayout shiftPanel = createNewPanel(configurableShiftPanel);
        shiftPanel.setMargin(true);

        shiftContainers.add(shiftPanel);
        panelsLayout.addComponentAtIndex(panelsLayout.getComponentCount() - 1, shiftPanel);
    }

    private VerticalLayout createNewPanel(Component component) {
        VerticalLayout container = new VerticalLayout(component);
        container.setPadding(false);
        container.setSpacing(false);
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        container.setHeight(PANEL_HEIGHT);
        container.setWidth(PANEL_WIDTH);
        return container;
    }

    private void injectConfiguredAddButtonPanelToShiftsLayout() {
        VerticalLayout addShiftButtonContainer = createNewPanel(addShiftPanelButton);
        addShiftButtonContainer.setMargin(true);

        addShiftPanelButton.addClickListener(event -> addShiftPanel());
        panelsLayout.add(addShiftButtonContainer);
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        bar.getStyle().set("background-color", "transparent");

        Span title = new Span();
        title.setText(getTranslation("shiftConfigurationComponent.title"));
        title.getStyle().set("font-weight", "bold");

        title.setWidth("50%");
        toggleVisibilityButton.setWidth("50%");
        toggleVisibilityButton.addClickListener(e -> toggleContentVisibility());

        bar.add(title, toggleVisibilityButton);
        bar.setFlexGrow(1, toggleVisibilityButton);

        return bar;
    }
}
