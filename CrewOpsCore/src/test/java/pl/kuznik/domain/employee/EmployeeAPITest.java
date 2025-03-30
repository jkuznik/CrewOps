// package pl.kuznik.domain.employee;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import java.time.LocalDate;
// import java.util.Set;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
// import pl.kuznik.domain.employee.dto.UpdateEmployeeDTO;
// import pl.kuznik.entity.Employee;
// import pl.kuznik.entity.Qualification;
// import pl.kuznik.entity.Vehicle;
//
//// TODO: reconfigure to achieve less weight test
// @SpringBootTest
//// @Transactional
// class EmployeeAPITest {
//
//    @Autowired
//    private EmployeeAPI employeeAPI;
//
//    private Set<Qualification> qualifications;
//    private Set<Vehicle> vehicles;
//
//    @BeforeEach
//    void setUp() {
//        var qualification1 =
//                Qualification.builder().name("foo").description("foo").build();
//
//        var qualification2 = Qualification.builder().name("bar").build();
//
//        qualifications = Set.of(qualification1, qualification2);
//    }
//
//    @Test
//    void shouldReturnEmployeeWithNoQualificationsAndNoVehicles() {
//        // given
//        var employeeDTO = CreateEmployeeDTO.builder()
//                .firstName("foo")
//                .lastName("bar")
//                .birthDate(LocalDate.now())
//                .phoneNumber("123456789")
//                .department("baz")
//                .build();
//
//        // when
//        Employee result = employeeAPI.createEmployee(employeeDTO);
//
//        // then
//        assertThat(result.getFirstName()).isEqualTo("foo");
//    }
//
//    @Test
//    void shouldReturnEmployeeWithQualificationsButNoVehicles() {
//        // given
//        var employeeDTO = CreateEmployeeDTO.builder()
//                .firstName("foo")
//                .lastName("bar")
//                .birthDate(LocalDate.now())
//                .phoneNumber("123456789")
//                .department("baz")
//                .qualifications(qualifications)
//                .build();
//
//        // when
//        Employee result = employeeAPI.createEmployee(employeeDTO);
//
//        // then
//        assertThat(result.getFirstName()).isEqualTo("foo");
//    }
//
//    @Test
//    void shouldReturnEmployeeWhenUpdateObjectIsValid() {
//        // given
//        var updateEmployeeDTO = UpdateEmployeeDTO.builder()
//                // TODO: prepare permament test values
//                .employeeId(UUID.fromString("818417e6-fd0c-42c4-bcc1-b0dd453b5960"))
//                .phoneNumber("987654321")
//                .department("foo")
//                .build();
//
//        // when
//        Employee result = employeeAPI.updateEmployee(updateEmployeeDTO);
//
//        // then
//        assertThat(result.getFirstName()).isEqualTo("foo");
//        assertThat(result.getLastName()).isEqualTo("bar");
//        assertThat(result.getPhoneNumber()).isEqualTo("987654321");
//    }
// }
