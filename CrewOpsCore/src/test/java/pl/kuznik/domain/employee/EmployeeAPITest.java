package pl.kuznik.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.kuznik.IntegrationTest;
import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.Qualification;
import pl.kuznik.entity.Vehicle;
import pl.kuznik.utils.enums.VehicleType;

@Transactional
class EmployeeAPITest extends IntegrationTest {

    @Autowired
    private EmployeeAPI employeeAPI;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Set<Qualification> qualifications;
    private Set<Vehicle> vehicles;

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
    }

    @Test
    void shouldReturnEmployeeWithNoQualificationsAndNoVehicles() {
        // given
        var employeeDTO = CreateEmployeeDTO.builder()
                .firstName("foo")
                .lastName("bar")
                .birthDate(LocalDate.now())
                .phoneNumber("123456789")
                .department("baz")
                .build();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(employeeDTO);
        Employee employee = employeeRepository.findAll().getFirst();

        // then
        assertThat(result.firstName()).isEqualTo("foo");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
    }

    @Test
    void shouldReturnEmployeeWithQualificationsButNoVehicles() {
        // given
        var employeeDTO = CreateEmployeeDTO.builder()
                .firstName("foo")
                .lastName("bar")
                .birthDate(LocalDate.now())
                .phoneNumber("123456789")
                .department("baz")
                .qualifications(qualifications)
                .build();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(employeeDTO);
        Employee employee = employeeRepository.findAll().getFirst();
        Set<Qualification> employeeQualifications = employee.getQualifications();

        // then
        assertThat(result.firstName()).isEqualTo("foo");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
        assertThat(employeeQualifications.size()).isEqualTo(2); // check if qualifications are persist correct
    }

    @Test
    void shouldReturnEmployeeWithQualificationsAndVehicles() {
        // given
        var employeeDTO = CreateEmployeeDTO.builder()
                .firstName("foo")
                .lastName("bar")
                .birthDate(LocalDate.now())
                .phoneNumber("123456789")
                .department("baz")
                .qualifications(qualifications)
                .vehicles(vehicles)
                .build();

        // when
        EmployeeDTO result = employeeAPI.createEmployee(employeeDTO);
        Employee employee = employeeRepository.findAll().getFirst();
        Set<Qualification> employeeQualifications = employee.getQualifications();
        Set<Vehicle> employeeVehicles = employee.getVehicles();
        Optional<Vehicle> first = employeeVehicles.stream()
                .filter(vehicle -> vehicle.getVehicleType().equals(VehicleType.BULLDOZER))
                .findFirst();

        // then
        assertThat(result.firstName()).isEqualTo("foo");
        assertThat(result.firstName()).isEqualTo(employee.getFirstName());
        assertThat(employeeQualifications.size()).isEqualTo(2); // check if qualifications are persist correct
        assertThat(employeeVehicles.size()).isEqualTo(2); // check if vehicles are persist correct
        assertThat(first.isPresent()).isTrue(); // check if vehicle type is persist correct
    }

    @Test
    void shouldReturnEmployeeWhenUpdateObjectIsValid() {
        // TODO: prepare permanent test values
        // given
        //        var updateEmployeeDTO = UpdateEmployeeDTO.builder()
        //                .employeeId(UUID.fromString("818417e6-fd0c-42c4-bcc1-b0dd453b5960"))
        //                .phoneNumber("987654321")
        //                .department("foo")
        //                .build();
        //
        //        // when
        //        EmployeeDTO result = employeeAPI.updateEmployee(updateEmployeeDTO);
        //
        //        // then
        //        assertThat(result.firstName()).isEqualTo("foo");
        //        assertThat(result.lastName()).isEqualTo("bar");
        //        assertThat(result.phoneNumber()).isEqualTo("987654321");
    }
}
