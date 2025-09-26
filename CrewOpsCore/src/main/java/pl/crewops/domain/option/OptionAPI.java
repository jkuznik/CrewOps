package pl.crewops.domain.option;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.option.OptionDTO;

@Validated
public interface OptionAPI {

    Set<OptionDTO> getOptionsByEmployeeId(@NotNull UUID employeeId);
}
