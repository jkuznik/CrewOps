package pl.crewops.ui.component.content;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.ArrayList;
import java.util.List;
import pl.crewops.ui.component.custom.AddButtonPanel;
import pl.crewops.ui.component.custom.SchedulePanel;
import pl.crewops.ui.component.notification.SuccessNotification;
import pl.crewops.ui.component.panel.ShiftPanel;

public class ScheduleContent extends VerticalLayout {

    private final SchedulePanel panelTimeline =
            new SchedulePanel("Tryb planowania oparty na gotowych harmonogramach zmian...");
    private final SchedulePanel panelIndividual =
            new SchedulePanel("Tryb, w którym ręcznie tworzysz i modyfikujesz godziny pracy...");
    private final HorizontalLayout modeSelectContainer = new HorizontalLayout(panelTimeline, panelIndividual);

    private final List<VerticalLayout> shiftContainers = new ArrayList<>();

    private final AddButtonPanel addButtonPanel = new AddButtonPanel();

    /** layout z kontenerami shiftów. Używamy FlexLayout do zawijania w wierszach. */
    private final FlexLayout shiftsLayout = new FlexLayout(); // ZMIENIONO: Używamy FlexLayout

    public ScheduleContent() {

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);

        modeSelectContainer.setMinHeight("400px");
        modeSelectContainer.addClassNames(LumoUtility.Gap.LARGE, LumoUtility.Width.FULL);
        modeSelectContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        modeSelectContainer.setFlexGrow(1, panelTimeline, panelIndividual);
        modeSelectContainer.getStyle().set("align-items", "stretch");

        panelTimeline.setSummary(VaadinIcon.CALENDAR, "Planowanie harmonogramu zmianowego");
        panelTimeline.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectContainer.setMinHeight("200px");
                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");

                shiftsLayout.setVisible(true);
                addButtonPanel.setVisible(true);

                if (shiftContainers.isEmpty()) {
                    addShiftPanel(); // Dodaj pierwszy panel + przycisk ADD
                }
            }
        });

        panelIndividual.setSummary(VaadinIcon.DATE_INPUT, "Planowanie indywidualne i ad-hoc");
        panelIndividual.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectContainer.setMinHeight("200px");
                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");

                shiftsLayout.setVisible(false);
                addButtonPanel.setVisible(false);
            }
        });

        addButtonPanel.setVisible(false);
        addButtonPanel.addClickListener(event -> addShiftPanel());
        VerticalLayout buttonContainer = createContainer();
        buttonContainer.setMargin(true);
        buttonContainer.add(addButtonPanel);
        buttonContainer.setHeight("550px");

        shiftsLayout.setVisible(false);
        shiftsLayout.setWidthFull();
        shiftsLayout.setFlexWrap(FlexWrap.WRAP);
        shiftsLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        shiftsLayout.setAlignItems(FlexComponent.Alignment.START);
        shiftsLayout.addClassNames(LumoUtility.Gap.MEDIUM);
        shiftsLayout.add(buttonContainer);

        add(modeSelectContainer, shiftsLayout);
    }

    private VerticalLayout createContainer() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(false);
        container.setSpacing(false);
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setWidth("300px");

        container.addClassNames(LumoUtility.Border.ALL, LumoUtility.Padding.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);

        return container;
    }

    private void addShiftPanel() {

        ShiftPanel shift = new ShiftPanel();

        // --- Listener zamykania / usuwania ---
        shift.addCloseListener(event -> {

            // Znajdź kontener, w którym jest ten shiftPanel
            VerticalLayout containerToRemove = shiftContainers.stream()
                    .filter(container -> container.getChildren().anyMatch(c -> c == shift))
                    .findFirst()
                    .orElse(null);

            if (containerToRemove != null) {
                // Usuń z listy i układu
                shiftContainers.remove(containerToRemove);
                shiftsLayout.remove(containerToRemove);

                new SuccessNotification("Usunięto zmianę").open();
            }
        });

        VerticalLayout shiftContainer = createContainer();
        shiftContainer.setMargin(true);
        shiftContainer.add(shift);

        shiftContainers.add(shiftContainer);

        // dodaj kontener tuż przed kontenerem guzika (który jest na końcu FlexLayout)
        shiftsLayout.addComponentAtIndex(shiftsLayout.getComponentCount() - 1, shiftContainer);

        new SuccessNotification("Dodano zmianę").open();
    }
}
