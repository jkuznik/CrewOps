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

    private final ScheduleChoicePanel shiftBasedGenerator =
            new ScheduleChoicePanel(getTranslation("scheduleContent.shiftBasedGenerator"));
    private final ScheduleChoicePanel individualGenerator =
            new ScheduleChoicePanel(getTranslation("scheduleContent.individualGenerator"));
    private final HorizontalLayout modeSelectorContainer =
            new HorizontalLayout(shiftBasedGenerator, individualGenerator);

    /** Shift based generator components */
    private final ShiftConfigurationComponent shiftConfigurationComponent;

    private final ScheduleConfigurationComponent scheduleConfigurationComponent;

    /** Individual generator components */
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

        //
        // shiftConfigurationComponent.getExistedShifts().forEach(scheduleConfigurationComponent::addShiftResourceDragBar);
        //
        //        shiftConfigurationComponent.addDisplayExistingShiftListener(event -> {
        //            scheduleConfigurationComponent.addShiftResourceDragBar(event.getShiftDTO());
        //        });
        //        shiftConfigurationComponent.addDeleteShiftListener(event -> {
        //            scheduleConfigurationComponent.removeShiftResourceDragBar(event.getDeletedShiftId());
        //        });
        //        shiftConfigurationComponent.addUpdateShiftListener(event -> {
        //            scheduleConfigurationComponent.updateShiftResourceDragBar(event.getShiftDTO());
        //        });

        add(modeSelectorContainer, shiftConfigurationComponent, scheduleConfigurationComponent);
    }

    private void configureModeSelectorContainer() {
        modeSelectorContainer.setMinHeight("400px");
        modeSelectorContainer.addClassNames(LumoUtility.Gap.LARGE, LumoUtility.Width.FULL);
        modeSelectorContainer.setAlignItems(Alignment.CENTER);
        modeSelectorContainer.setFlexGrow(1, shiftBasedGenerator, individualGenerator);
        modeSelectorContainer.getStyle().set("align-items", "stretch");

        shiftBasedGenerator.setSummary(VaadinIcon.CALENDAR, getTranslation("scheduleContent.shiftBasedGeneratorLabel"));
        shiftBasedGenerator.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectorContainer.setMinHeight("200px");
                shiftBasedGenerator.onClickModification("200px", "150px");
                individualGenerator.onClickModification("200px", "150px");

                shiftConfigurationComponent.setVisible(true);
                scheduleConfigurationComponent.setVisible(true);
            }
        });

        individualGenerator.setSummary(
                VaadinIcon.DATE_INPUT, getTranslation("scheduleContent.individualGeneratorLabel"));
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
