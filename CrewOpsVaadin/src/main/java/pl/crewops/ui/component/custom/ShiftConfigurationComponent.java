package pl.crewops.ui.component.custom;

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
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.ui.component.notification.FailNotification;
import pl.crewops.ui.component.panel.ShiftPanel;

@CssImport("./styles/component/shift-configuration-component.css")
public class ShiftConfigurationComponent extends VerticalLayout {

    private final CoreAPI coreAPI;

    private static final String PANEL_HEIGHT = "550px";
    private static final String PANEL_WIDTH = "400px";

    private final List<VerticalLayout> shiftContainers = new ArrayList<>();
    private final AddButtonPanel addButtonPanel = new AddButtonPanel();
    private final FlexLayout shiftsLayout = new FlexLayout();

    private final VerticalLayout contentContainer = new VerticalLayout();
    private final Button toggleVisibilityButton = new Button(VaadinIcon.ANGLE_DOWN.create());
    private final HorizontalLayout toolbar = createToolbar();

    private boolean isContentVisible = false;
    private boolean isFirstOpen = true;

    public ShiftConfigurationComponent(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        addClassName("shift-content-border");
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);

        add(toolbar, contentContainer);

        contentContainer.setWidthFull();
        contentContainer.setPadding(true);
        contentContainer.setSpacing(true);
        contentContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        contentContainer.setJustifyContentMode(JustifyContentMode.CENTER);

        shiftsLayout.setWidthFull();
        shiftsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        shiftsLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        shiftsLayout.setAlignItems(FlexComponent.Alignment.START);

        contentContainer.add(shiftsLayout);

        // Ustawienie początkowej widoczności
        contentContainer.setVisible(isContentVisible);

        addButtonPanel.addClickListener(event -> addShiftPanel());
    }

    // ------------------ ZMODYFIKOWANA METODA: createToolbar() ------------------

    private HorizontalLayout createToolbar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        bar.getStyle().set("background-color", "transparent");

        Span title = new Span();
        title.setText("Konfiguracja szablonów zmian pracowniczych");
        title.getStyle().set("font-weight", "bold");

        toggleVisibilityButton.addClickListener(e -> toggleContentVisibility());

        bar.add(title, toggleVisibilityButton);
        bar.setFlexGrow(1, toggleVisibilityButton);

        return bar;
    }

    public void updateShiftsLayout() {
        shiftsLayout.removeAll();
        shiftContainers.clear();

        // 2. Dodanie przycisku dodawania (który ma być na końcu)
        VerticalLayout addShiftButtonContainer = createContainer();
        addShiftButtonContainer.setMargin(true);
        addShiftButtonContainer.add(addButtonPanel);
        shiftsLayout.add(addShiftButtonContainer);

        try {
            List<ShiftDTO> allShifts = coreAPI.getAllShifts();

            allShifts.forEach(shift -> {
                VerticalLayout existedShiftContainer = createContainer();
                existedShiftContainer.setMargin(true); // Dodaj margines dla spójności

                var existedShift = new ShiftPanel(shift);

                existedShift.addUpdateListener(event -> {
                    // todo Update medtod
                });

                existedShift.addDeleteListener(event -> {

                    // todo implement delete guardian notification
                    VerticalLayout containerToRemove = shiftContainers.stream()
                            .filter(container -> container.getChildren().anyMatch(c -> c == existedShift))
                            .findFirst()
                            .orElse(null);

                    if (containerToRemove != null) {
                        shiftContainers.remove(containerToRemove);
                        shiftsLayout.remove(containerToRemove);
                    }
                });

                existedShiftContainer.add(existedShift);

                shiftContainers.add(existedShiftContainer);

                shiftsLayout.addComponentAtIndex(shiftsLayout.getComponentCount() - 1, existedShiftContainer);
            });

        } catch (NotAuthenticatedException e) {
            new FailNotification(e.getMessage());
        }
    }

    private void toggleContentVisibility() {
        isContentVisible = !isContentVisible;
        if (isFirstOpen) {
            updateShiftsLayout();
            isFirstOpen = false;
        }
        contentContainer.setVisible(isContentVisible);

        toggleVisibilityButton.setIcon(
                isContentVisible ? VaadinIcon.ANGLE_UP.create() : VaadinIcon.ANGLE_DOWN.create());
    }

    public void ensureFirstShiftPanelExists() {
        if (shiftContainers.isEmpty()) {
            addShiftPanel();
        }
        if (!isContentVisible) {
            toggleContentVisibility();
        }
    }

    private void addShiftPanel() {
        ShiftPanel shift = new ShiftPanel(null);

        shift.addCloseListener(event -> {
            VerticalLayout containerToRemove = shiftContainers.stream()
                    .filter(container -> container.getChildren().anyMatch(c -> c == shift))
                    .findFirst()
                    .orElse(null);

            if (containerToRemove != null) {
                shiftContainers.remove(containerToRemove);
                shiftsLayout.remove(containerToRemove);
            }
        });

        VerticalLayout shiftContainer = createContainer();
        shiftContainer.setMargin(true);
        shiftContainer.add(shift);

        shiftContainers.add(shiftContainer);
        shiftsLayout.addComponentAtIndex(shiftsLayout.getComponentCount() - 1, shiftContainer);
    }

    private VerticalLayout createContainer() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(false);
        container.setSpacing(false);
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        container.setHeight(PANEL_HEIGHT);
        container.setWidth(PANEL_WIDTH);
        return container;
    }
}
