package pl.crewops.ui.component.content;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pl.crewops.ui.component.custom.SchedulePanel;

public class ScheduleContent extends VerticalLayout {

    private final SchedulePanel panelTimeline = new SchedulePanel(
            "Tryb planowania oparty na gotowych harmonogramach zmian. Idealny do szybkiego i powtarzalnego przypisywania pracowników do predefiniowanych slotów czasowych. Ta opcja pozwala na wizualizację obciążenia oraz efektywne zarządzanie standardowymi grafikami. Jest to szybkie i ustrukturyzowane podejście.");
    private final SchedulePanel panelIndividual = new SchedulePanel(
            "Tryb, w którym ręcznie tworzysz i modyfikujesz godziny pracy dla każdego pracownika z osobna. Jest to elastyczne rozwiązanie dla niestandardowych umów, indywidualnych wymagań lub projektów, gdzie godziny pracy zmieniają się dynamicznie. Zapewnia pełną kontrolę nad każdą pojedynczą godziną pracy.");

    public ScheduleContent() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);

        panelTimeline.setSummary(VaadinIcon.CALENDAR, "Planowanie harmonogramu zmianowego");
        panelTimeline.addClickListener(event -> {
            if (event.getButton() == 0) {
                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");
            }
        });

        panelIndividual.setSummary(VaadinIcon.DATE_INPUT, "Planowanie indywidualne i ad-hoc");
        panelIndividual.addClickListener(event -> {
            if (event.getButton() == 0) {
                panelTimeline.onClickModification("200px", "150px");
                panelIndividual.onClickModification("200px", "150px");
            }
        });

        HorizontalLayout selectionLayout = new HorizontalLayout(panelTimeline, panelIndividual);
        selectionLayout.addClassNames(LumoUtility.Gap.LARGE, LumoUtility.Width.FULL);

        selectionLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        selectionLayout.setFlexGrow(
                1, panelTimeline, panelIndividual); // Panele zajmują równą część dostępnej przestrzeni
        selectionLayout.getStyle().set("align-items", "stretch"); // Rozciągnięcie paneli na całą wysokość

        add(selectionLayout);
    }
}
