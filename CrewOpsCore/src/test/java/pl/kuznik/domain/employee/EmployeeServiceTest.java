package pl.kuznik.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;
import pl.kuznik.utils.enums.VehicleType;

@SpringJUnitConfig(classes = {EmployeeService.class, MethodValidationPostProcessor.class})
class EmployeeServiceTest {

    @MockitoBean
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    private Set<Qualification> qualifications;
    private Set<Vehicle> vehicles;
    private CreateEmployeeDTO createEmployeeWithQAndV;
    private CreateEmployeeDTO createEmployeeWithEmptyQAndEmptyV;
    private Employee employeeWithQAndV;
    private Employee employeeWithEmptyQAndEmptyV;

    @BeforeEach
    void setUp() {
        var qualification1 =
                Qualification.builder().name("foo").description("foo").build();
        var qualification2 = Qualification.builder().name("bar").build();
        qualifications = Set.of(qualification1, qualification2);

        var vehicle1 = Vehicle.builder()
                .vehicleType(VehicleType.BULLDOZER)
                .make("make")
                .model("model")
                .year(2020)
                .broken(false)
                .build();
        var vehicle2 = Vehicle.builder()
                .vehicleType(VehicleType.EXCAVATOR)
                .make("make")
                .model("model")
                .year(2021)
                .broken(false)
                .build();
        vehicles = Set.of(vehicle1, vehicle2);

        createEmployeeWithQAndV = CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .qualifications(qualifications)
                .vehicles(vehicles)
                .build();

        createEmployeeWithEmptyQAndEmptyV = CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .build();

        employeeWithQAndV = Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .qualifications(qualifications)
                .vehicles(vehicles)
                .build();

        employeeWithEmptyQAndEmptyV = Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .build();
    }

    @Test
    void shouldReturnEmployeeDTO_whenCreateEmployeeDTOHaveNoQualificationsAndNoVehicles() {
        // when
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employeeWithEmptyQAndEmptyV);
        EmployeeDTO result = employeeService.createEmployee(createEmployeeWithEmptyQAndEmptyV);

        // then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("firstName");
    }

    @Test
    void updateEmployee() {}
}
