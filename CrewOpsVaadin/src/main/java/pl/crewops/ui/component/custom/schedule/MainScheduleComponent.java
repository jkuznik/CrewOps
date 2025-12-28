package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainScheduleComponent extends VerticalLayout {

    private final ChoiceModePanel shiftBasedGenerator =
            new ChoiceModePanel(getTranslation("scheduleContent.shiftBasedGenerator"));
    private final ChoiceModePanel individualGenerator =
            new ChoiceModePanel(getTranslation("scheduleContent.individualGenerator"));
    private final HorizontalLayout modeSelectorContainer =
            new HorizontalLayout(shiftBasedGenerator, individualGenerator);

    /** Shift based generator components */
    private final ScheduleShiftConfigurationComponent scheduleShiftConfigurationComponent;

    private final ScheduleTemplateConfigurationComponent scheduleTemplateConfigurationComponent;
    private final ScheduleCalendarComponent scheduleCalendarComponent;

    /** Individual generator components */
    public MainScheduleComponent() {
        this.scheduleShiftConfigurationComponent = new ScheduleShiftConfigurationComponent();
        this.scheduleTemplateConfigurationComponent = new ScheduleTemplateConfigurationComponent();
        this.scheduleCalendarComponent = new ScheduleCalendarComponent();

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);

        configureModeSelectorContainer();

        scheduleShiftConfigurationComponent.setVisible(false);
        scheduleTemplateConfigurationComponent.setVisible(false);
        scheduleCalendarComponent.setVisible(false);

        scheduleShiftConfigurationComponent
                .getExistedShifts()
                .forEach(scheduleTemplateConfigurationComponent::addShiftResourceDragBar);

        scheduleShiftConfigurationComponent.addDisplayExistingShiftListener(event -> {
            scheduleTemplateConfigurationComponent.addShiftResourceDragBar(event.getShiftDTO());
        });
        scheduleShiftConfigurationComponent.addDeleteShiftListener(event -> {
            scheduleTemplateConfigurationComponent.removeShiftFromPalette(event.getDeletedShiftId());
        });
        scheduleShiftConfigurationComponent.addUpdateShiftListener(event -> {
            scheduleTemplateConfigurationComponent.updateShiftResourceDragBar(event.getShiftDTO());
        });

        add(
                modeSelectorContainer,
                scheduleShiftConfigurationComponent,
                scheduleTemplateConfigurationComponent,
                scheduleCalendarComponent);
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

                scheduleShiftConfigurationComponent.setVisible(true);
                scheduleTemplateConfigurationComponent.setVisible(true);
                scheduleCalendarComponent.setVisible(true);
            }
        });

        individualGenerator.setSummary(
                VaadinIcon.DATE_INPUT, getTranslation("scheduleContent.individualGeneratorLabel"));
        individualGenerator.addClickListener(event -> {
            if (event.getButton() == 0) {
                modeSelectorContainer.setMinHeight("200px");
                shiftBasedGenerator.onClickModification("200px", "150px");
                individualGenerator.onClickModification("200px", "150px");

                scheduleShiftConfigurationComponent.setVisible(false);
                scheduleTemplateConfigurationComponent.setVisible(false);
                scheduleCalendarComponent.setVisible(false);
            }
        });
    }
}
