package pl.crewops.ui.component.content;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.ui.component.custom.schedule.ScheduleChoicePanel;
import pl.crewops.ui.component.custom.schedule.ScheduleConfigurationComponent;
import pl.crewops.ui.component.custom.schedule.ShiftConfigurationComponent;

public class ScheduleContent extends VerticalLayout {

    // todo i18n
    private final ScheduleChoicePanel shiftBasedGenerator =
            new ScheduleChoicePanel("Tryb planowania oparty na gotowych harmonogramach zmian...");
    private final ScheduleChoicePanel individualGenerator =
            new ScheduleChoicePanel("Tryb, w którym ręcznie tworzysz i modyfikujesz godziny pracy...");
    private final HorizontalLayout modeSelectorContainer =
            new HorizontalLayout(shiftBasedGenerator, individualGenerator);

    /** Shift based generator components */
    private final ShiftConfigurationComponent shiftConfigurationComponent;

    private final ScheduleConfigurationComponent scheduleConfigurationComponent;

    /** Individual generator components */

    // todo i18n
    public ScheduleContent(CoreAPI coreAPI) {
        this.shiftConfigurationComponent = new ShiftConfigurationComponent(coreAPI);
        this.scheduleConfigurationComponent = new ScheduleConfigurationComponent(coreAPI);

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);

        configureModeSelectorContainer();

        shiftConfigurationComponent.setVisible(false);
        scheduleConfigurationComponent.setVisible(false);

        add(modeSelectorContainer, shiftConfigurationComponent, scheduleConfigurationComponent);
    }

    private void configureModeSelectorContainer() {
        modeSelectorContainer.setMinHeight("400px");
        modeSelectorContainer.addClassNames(LumoUtility.Gap.LARGE, LumoUtility.Width.FULL);
        modeSelectorContainer.setAlignItems(Alignment.CENTER);
        modeSelectorContainer.setFlexGrow(1, shiftBasedGenerator, individualGenerator);
        modeSelectorContainer.getStyle().set("align-items", "stretch");

        shiftBasedGenerator.setSummary(VaadinIcon.CALENDAR, "Planowanie harmonogramu zmianowego");
        shiftBasedGenerator.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectorContainer.setMinHeight("200px");
                shiftBasedGenerator.onClickModification("200px", "150px");
                individualGenerator.onClickModification("200px", "150px");

                shiftConfigurationComponent.setVisible(true);
                scheduleConfigurationComponent.setVisible(true);
            }
        });

        individualGenerator.setSummary(VaadinIcon.DATE_INPUT, "Planowanie indywidualne i ad-hoc");
        individualGenerator.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectorContainer.setMinHeight("200px");
                shiftBasedGenerator.onClickModification("200px", "150px");
                individualGenerator.onClickModification("200px", "150px");

                shiftConfigurationComponent.setVisible(false);
                scheduleConfigurationComponent.setVisible(false);
            }
        });
    }
}
