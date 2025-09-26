package pl.crewops.domain.option;

import static pl.crewops.enums.ControllerURL.EMPLOYEE_EID_OPTIONS;
import static pl.crewops.enums.ControllerURL.EMPLOYEE_ID;

import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.model.dto.option.OptionDTO;

@RestController
@RequiredArgsConstructor
class OptionController {

    private final OptionAPI optionAPI;

    @GetMapping(EMPLOYEE_EID_OPTIONS)
    public ResponseEntity<Set<OptionDTO>> getOptions(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        return ResponseEntity.status(HttpStatus.OK).body(optionAPI.getOptionsByEmployeeId(employeeId));
    }
}
