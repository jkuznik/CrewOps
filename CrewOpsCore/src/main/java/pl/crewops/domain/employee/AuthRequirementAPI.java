package pl.crewops.domain.employee;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.exception.domain.employee.EmployeeNotFoundException;
import pl.crewops.model.Employee;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthRequirementAPI {
    private final EmployeeRepository employeeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Employee getEmployeeById(UUID uuid) {
        return employeeRepository.findById(uuid).orElseThrow(() -> new EmployeeNotFoundException(uuid.toString()));
    }
}
