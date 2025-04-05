package pl.crewops;

import org.junit.jupiter.api.Test;
import pl.crewops.dto.employee.EmployeeDTO;

class CrewOpsVaadinTest {

    @Test
    void shouldLoadCoreModule() {
        EmployeeDTO employeeDTO =
                EmployeeDTO.builder().firstName("Test access to model classes").build();
    }
}
