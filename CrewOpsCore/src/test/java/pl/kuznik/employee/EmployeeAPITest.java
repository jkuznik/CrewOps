package pl.kuznik.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {EmployeeAPI.class, EmployeeService.class, EmployeeRepository.class})
class EmployeeAPITest {

    private Set<Qualification> qualifications;
    private Set<Vehicle> vehicles;

    @BeforeEach
    void setUp() {
        var qualification1 = Qualification.builder()
                .name("foo")
                .description("foo")
                .build();

        var qualification2 = Qualification.builder()
                .name("bar")
                .build();

        qualifications = Set.of(qualification1, qualification2);
    }

    @Test
    void shouldReturnEmployeeWithNoQualificationsAndNoVehicles() {
    }
}