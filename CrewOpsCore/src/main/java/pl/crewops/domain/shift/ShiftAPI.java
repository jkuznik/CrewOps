package pl.crewops.domain.shift;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;

@Validated
public interface ShiftAPI {

    ShiftDTO createShift(@NotNull @Valid CreateShiftDTO createShiftDTO);
}
