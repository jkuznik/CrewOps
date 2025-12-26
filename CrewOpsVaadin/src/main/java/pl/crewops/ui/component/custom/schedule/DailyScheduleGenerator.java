package pl.crewops.ui.component.custom.schedule;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.UUID;
import pl.crewops.model.dto.shift.ShiftDTO;

public class DailyScheduleGenerator extends VerticalLayout {

    private final NativeScheduleGrid nativeGrid = new NativeScheduleGrid();

    private final ScheduleTemplateForm form = new ScheduleTemplateForm(nativeGrid);

    public DailyScheduleGenerator() {

        setSizeFull();
        setPadding(false);
        setSpacing(true);

        nativeGrid.updateClientSideData();

        add(nativeGrid, form);
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
}
