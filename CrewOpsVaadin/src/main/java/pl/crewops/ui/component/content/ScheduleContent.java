package pl.crewops.ui.component.content;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

    /**
     * Każdy element w tym liście to:
     * VerticalLayout → który zawiera pojedynczy ShiftPanel
     */
    private final List<VerticalLayout> shiftContainers = new ArrayList<>();

    private final AddButtonPanel addButtonPanel = new AddButtonPanel();

    /** layout z kontenerami shiftów */
    private final HorizontalLayout shiftsLayout = new HorizontalLayout();

    public ScheduleContent() {

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);

        addButtonPanel.setVisible(false);
        shiftsLayout.setVisible(false);
        shiftsLayout.setWidthFull();
        shiftsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        shiftsLayout.setAlignItems(FlexComponent.Alignment.START);
        shiftsLayout.addClassNames(LumoUtility.Gap.MEDIUM);

        // ------------------------------------
        //   Wybór trybu
        // ------------------------------------

        panelTimeline.setSummary(VaadinIcon.CALENDAR, "Planowanie harmonogramu zmianowego");
        panelTimeline.addClickListener(event -> {
            if (event.getButton() == 0) {

                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");

                shiftsLayout.setVisible(true);
                addButtonPanel.setVisible(true);

                if (shiftContainers.isEmpty()) {
                    addShiftPanel();
                }
            }
        });

        panelIndividual.setSummary(VaadinIcon.DATE_INPUT, "Planowanie indywidualne i ad-hoc");
        panelIndividual.addClickListener(event -> {
            if (event.getButton() == 0) {
                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");
            }
        });

        addButtonPanel.addClickListener(event -> addShiftPanel());

        HorizontalLayout selectionLayout = new HorizontalLayout(panelTimeline, panelIndividual);
        selectionLayout.addClassNames(LumoUtility.Gap.LARGE, LumoUtility.Width.FULL);
        selectionLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        selectionLayout.setFlexGrow(1, panelTimeline, panelIndividual);
        selectionLayout.getStyle().set("align-items", "stretch");

        // ------------------------------------
        //   KONTERER NA GUZIK ADD
        // ------------------------------------
        VerticalLayout buttonContainer = createContainer();
        buttonContainer.add(addButtonPanel);
        buttonContainer.setHeight("550px");

        shiftsLayout.add(buttonContainer);

        add(selectionLayout, shiftsLayout);
    }

    // -------------------------------------------------------
    //   🔧 Tworzy kontener dla ShiftPanel lub AddButtonPanel
    // -------------------------------------------------------
    private VerticalLayout createContainer() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(false);
        container.setSpacing(false);
        container.setAlignItems(FlexComponent.Alignment.CENTER);

        container.setWidth("300px"); // ⭐ tu konfigurujesz szerokość kontenera
        container.setHeightFull();
        container.addClassNames(LumoUtility.Border.ALL, LumoUtility.Padding.MEDIUM);

        return container;
    }

    // -------------------------------------------------------
    //   ➕ DODAJ SHIFT PANEL
    // -------------------------------------------------------
    private void addShiftPanel() {

        ShiftPanel shift = new ShiftPanel();

        // --- Listener zamykania / usuwania ---
        shift.addCloseListener(event -> {

            // Znajdź kontener, w którym jest ten shiftPanel
            VerticalLayout containerToRemove = null;

            for (VerticalLayout container : shiftContainers) {
                if (container.getChildren().anyMatch(c -> c == shift)) {
                    containerToRemove = container;
                    break;
                }
            }

            if (containerToRemove != null) {
                // Usuń z listy
                shiftContainers.remove(containerToRemove);

                // Usuń z układu
                shiftsLayout.remove(containerToRemove);
            }
        });

        // --- Tworzenie kontenera dla shiftPanel ---
        VerticalLayout shiftContainer = createContainer();
        shiftContainer.add(shift);

        shiftContainers.add(shiftContainer);

        // dodaj kontener tuż przed kontenerem guzika
        shiftsLayout.addComponentAtIndex(shiftsLayout.getComponentCount() - 1, shiftContainer);

        new SuccessNotification("Dodano zmianę");
    }
}
