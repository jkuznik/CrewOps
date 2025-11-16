package pl.crewops.ui.component.content;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
// Importy zredukowane, ponieważ ShiftConfigurationPanel obsługuje logikę zmian
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.ui.component.custom.ScheduleChoicePanel;
import pl.crewops.ui.component.custom.ShiftConfigurationComponent;

public class ScheduleContent extends VerticalLayout {

    private final ScheduleChoicePanel panelTimeline =
            new ScheduleChoicePanel("Tryb planowania oparty na gotowych harmonogramach zmian...");
    private final ScheduleChoicePanel panelIndividual =
            new ScheduleChoicePanel("Tryb, w którym ręcznie tworzysz i modyfikujesz godziny pracy...");
    private final HorizontalLayout modeSelectContainer = new HorizontalLayout(panelTimeline, panelIndividual);

    /** Nowy, osobny komponent do zarządzania panelami zmian. */
    private final ShiftConfigurationComponent shiftConfigurationComponent;

    public ScheduleContent(CoreAPI coreAPI) {
        this.shiftConfigurationComponent = new ShiftConfigurationComponent(coreAPI);

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

                // Ujawnij nowy, kompletny panel konfiguracji
                shiftConfigurationComponent.setVisible(true);
                shiftConfigurationComponent.ensureFirstShiftPanelExists(); // Dodaj pierwszy, jeśli go nie ma
            }
        });

        panelIndividual.setSummary(VaadinIcon.DATE_INPUT, "Planowanie indywidualne i ad-hoc");
        panelIndividual.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectContainer.setMinHeight("200px");
                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");

                // Ukryj cały panel konfiguracji zmian
                shiftConfigurationComponent.setVisible(false);
            }
        });

        // Ukryj panel konfiguracji zmian na starcie
        shiftConfigurationComponent.setVisible(false);

        // Dodaj nowy komponent do głównego układu
        add(modeSelectContainer, shiftConfigurationComponent);
    }

    // Usunięto zbędne metody: createContainer() i addShiftPanel() oraz pola shiftContainers, shiftsLayout,
    // addButtonPanel
}
