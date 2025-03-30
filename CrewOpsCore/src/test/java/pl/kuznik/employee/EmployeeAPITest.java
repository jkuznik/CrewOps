// package pl.kuznik.employee;
//
// import java.time.LocalDate;
// import java.util.Set;
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.transaction.annotation.Transactional;
// import pl.kuznik.employee.dto.CreateEmployeeDTO;
// import pl.kuznik.entity.Employee;
// import pl.kuznik.entity.Qualification;
// import pl.kuznik.entity.Vehicle;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
//// TODO: reconfigure to achieve less weight test
// @SpringBootTest
// @Transactional
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
// }
