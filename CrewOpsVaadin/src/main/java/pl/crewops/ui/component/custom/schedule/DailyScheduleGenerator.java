package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Optional;
import java.util.UUID;
import pl.crewops.enums.ScheduleTemplateType;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

public class DailyScheduleGenerator extends VerticalLayout {

    private final CoreAPI coreAPI;
    private final AuthenticationResolver authenticationResolver;

    private final NativeScheduleGrid nativeGrid = new NativeScheduleGrid();
    private final Button save = new Button(getTranslation("saveButton"));

    public DailyScheduleGenerator(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);

        setSizeFull();
        setPadding(false);
        setSpacing(true);

        ShiftDTO poranna = new ShiftDTO(UUID.randomUUID(), "Zmiana Poranna", null, "#2ecc71"); // Szmaragdowy
        ShiftDTO druga = new ShiftDTO(
                UUID.randomUUID(),
                "Zmiana Druga",
                null,
                "#3498db"); // Jasnoniebieski (zmieniony kolor dla odróżnienia od nocnej)
        ShiftDTO nocna = new ShiftDTO(UUID.randomUUID(), "Zmiana Nocna", null, "#9b59b6"); // Fioletowy

        ShiftDTO czwarta = new ShiftDTO(UUID.randomUUID(), "Międzyzmiana", null, "#f1c40f"); // Słoneczny żółty
        ShiftDTO popoludniowa =
                new ShiftDTO(UUID.randomUUID(), "Zmiana Popołudniowa", null, "#e67e22"); // Pomarańczowy (Carrot)
        ShiftDTO techniczna = new ShiftDTO(UUID.randomUUID(), "Przegląd", null, "#e74c3c"); // Czerwony (Alizarin)
        ShiftDTO biurowa =
                new ShiftDTO(UUID.randomUUID(), "Administracja", null, "#34495e"); // Ciemny granat (Wet Asphalt)
        ShiftDTO weekendowa = new ShiftDTO(UUID.randomUUID(), "Weekend", null, "#1abc9c"); // Turkusowy (Turquoise)
        ShiftDTO shadow = new ShiftDTO(UUID.randomUUID(), "Shadow Shift", null, "#95a5a6"); // Szary (Concrete)

        configureButtons();

        add(nativeGrid, save);

        nativeGrid.addDay(new ScheduleDay(1));

        nativeGrid.updateClientSideData();
    }

    public void addShiftToPalette(ShiftDTO dto) {
        nativeGrid.registerPaletteTemplate(dto);
    }

    public void removeShiftFromPalette(UUID shiftId) {
        nativeGrid.removeShiftsByTemplate(shiftId);
    }

    public void updateShiftInPalette(ShiftDTO dto) {
        nativeGrid.updateShiftsFromTemplate(dto);
    }

    private void configureButtons() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            CreateScheduleTemplateDTO hardcodedCreateScheduleTemplateDTO = CreateScheduleTemplateDTO.builder()
                    .name("hardcoded")
                    .authorEmployeeId(authenticationResolver.getPrincipal().getEmployeeId())
                    .type(ScheduleTemplateType.DAILY)
                    .privateOwner(false)
                    .days(nativeGrid.collectDataFromGrid())
                    .build();

            try {
                Optional<ScheduleTemplateDTO> schedule = coreAPI.createSchedule(hardcodedCreateScheduleTemplateDTO);
                System.out.println(schedule.toString());
            } catch (NotAuthenticatedException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
