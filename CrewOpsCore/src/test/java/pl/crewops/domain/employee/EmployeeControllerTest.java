package pl.crewops.domain.employee;

import static pl.crewops.domain.employee.EmployeeTestFactory.*;
import static pl.crewops.enums.ControllerURL.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;
import pl.crewops.security.config.SecurityConfig;
import pl.crewops.security.custom.ClientValidationFilter;
import pl.crewops.security.custom.CustomAuthenticationManager;
import pl.crewops.security.jwt.JwtAuthFilter;
import pl.crewops.security.jwt.JwtAuthProvider;
import pl.crewops.security.jwt.JwtExceptionResolver;
import pl.crewops.security.jwt.JwtService;

@WebMvcTest(
        controllers = EmployeeController.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class),
        excludeFilters =
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ClientValidationFilter.class))
@ContextConfiguration(classes = SecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private ClientValidationFilter clientValidationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtExceptionResolver jwtExceptionResolver;

    @MockitoBean
    private CustomAuthenticationManager authenticationManager;

    @MockitoBean
    private JwtAuthProvider jwtAuthProvider;

    private CreateEmployeeDTO createEmployeeWithEmptyQAndEmptyV;
    private CreateEmployeeDTO createEmployeeDTOWithNullFields;
    private UpdateEmployeeDTO updateEmployeeDTO;
    private UpdateEmployeeDTO updateEmployeeDTONotValid;
    private Employee employeeWithQAndV;
    private Employee employeeWithEmptyQAndEmptyV;
    private EmployeeDTO employeeDTO;
    private Qualification qualification;
    private Vehicle vehicle;
    private final UUID employeeId = UUID.randomUUID();
    private final UUID qualificationId = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createEmployeeWithEmptyQAndEmptyV = EmployeeTestFactory.createEmployeeDTO();
        createEmployeeDTOWithNullFields = createEmployeeDTONotValid();
        updateEmployeeDTO = updateEmployeeDTO();
        updateEmployeeDTONotValid = updateEmployeeDTONotValid();
        employeeWithQAndV = createEmployeeWithQualificationsAndVehicles();
        employeeWithEmptyQAndEmptyV = createEmployeeWithoutQualificationsAndVehicles();
        employeeDTO = employeeDTO();
        qualification = qualification();
        vehicle = vehicle();
    }

    // todo: test are not working well
    //
    //    @Test
    //    void shouldReturnStatusCREATED_whenCreateEmployeeDTOIsValid_byMANAGER() throws Exception {
    //        // given
    //        var principal = new UserPrincipal(AuthUser.builder()
    //                .username("username")
    //                .roles(Set.of(Role.builder().name(RoleType.MANAGER.name()).build()))
    //                .build());
    //
    //        satisfyJwtService();
    //
    //        // when
    //        when(authenticationManager.authenticate(any())).thenReturn(new CustomAuthentication(principal));
    //        when(employeeService.createEmployee(any())).thenReturn(mapToDTO(employeeWithQAndV));
    //
    //        mockMvc.perform(post(ControllerURL.EMPLOYEES)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(
    //                                EmployeeTestFactory.createEmployeeWithoutQualificationsAndVehicles())))
    //                .andExpect(MockMvcResultMatchers.status().isOk());
    //    }
    //
    //    @Test
    //    void getEmployees() throws Exception {
    //        // given
    //        var principal = new UserPrincipal(AuthUser.builder()
    //                .username("username")
    //                .roles(Set.of(Role.builder().name(RoleType.MANAGER.name()).build()))
    //                .build());
    //
    //        satisfyJwtService();
    //
    //        // when
    //        when(userDetailsService.loadUserByUsername(any())).thenReturn(principal);
    //        when(authenticationManager.authenticate(any(CustomAuthentication.class)))
    //                .thenReturn(new CustomAuthentication(principal));
    //        when(employeeService.getAllEmployees(0, 15)).thenReturn(List.of(employeeDTO));
    //
    //        // when
    //        satisfyJwtService();
    //        mockMvc.perform(get(EMPLOYEES).param("page", "0").param("size", "15"))
    //                .andExpect(MockMvcResultMatchers.status().isOk())
    //                .andExpect(MockMvcResultMatchers.content().string("Working"));
    //    }
    //
    //    private void satisfyJwtService() {
    //        when(jwtService.extractTokenFromRequest(any())).thenReturn("token");
    //        when(jwtService.extractUsername(any())).thenReturn("username");
    //        when(jwtService.validateToken(any(), any())).thenReturn(true);
    //    }
}
