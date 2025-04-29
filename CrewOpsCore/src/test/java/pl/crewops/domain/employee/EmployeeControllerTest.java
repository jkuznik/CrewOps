package pl.crewops.domain.employee;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void createEmployee() {}

    @Test
    void getEmployees() {}

    @Test
    void getEmployeesByQualification() {}

    @Test
    void getEmployeesByVehicleId() {}

    @Test
    void updateEmployee() {}

    @Test
    void removePhoneNumber() {}

    @Test
    void deleteEmployee() {}

    @Test
    void addEmployeeQualification() {}

    @Test
    void removeEmployeeQualification() {}

    @Test
    void updateEmployeeQualification() {}

    @Test
    void addEmployeeVehicles() {}

    @Test
    void removeEmployeeVehicles() {}
}
