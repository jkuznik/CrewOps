package pl.crewops.domain.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.crewops.enums.ControllerURL.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.crewops.model.dto.auth.*;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.security.ValidTokenRequest;
import pl.crewops.security.ValidTokenResponse;
import pl.crewops.security.config.TestSecuriityConfig;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {TestSecuriityConfig.class, AuthController.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthAPI authAPI;

    // ----------------------
    // LOGIN
    // ----------------------
    @Test
    @DisplayName("POST /login should return 200 with token")
    void login_ShouldReturn200() throws Exception {
        AuthRequest request = new AuthRequest("john", "secret");
        AuthResponse response = new AuthResponse("mock-token");

        when(authAPI.login(any(AuthRequest.class), any())).thenReturn(response);

        mockMvc.perform(post(LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    // ----------------------
    // VALIDATE TOKEN
    // ----------------------
    @Test
    @DisplayName("POST /validate should return 200 with token validation result")
    void validate_ShouldReturn200() throws Exception {
        ValidTokenRequest request =
                ValidTokenRequest.builder().token("mock-token").build();
        ValidTokenResponse response =
                ValidTokenResponse.builder().valid(true).expiration(new Date()).build();

        when(authAPI.validateToken(any(ValidTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post(VALIDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @DisplayName("GET /employees/{id}/options should return 200 with options")
    @WithMockUser
    void getEmployeeOptions_ShouldReturn200() throws Exception {
        UUID employeeId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();

        AuthUserOptionDTO option = AuthUserOptionDTO.builder()
                .employeeId(employeeId)
                .optionId(optionId)
                .name("dark_mode")
                .enabled(true)
                .build();

        when(authAPI.getOptionsByEmployeeId(employeeId)).thenReturn(Set.of(option));

        mockMvc.perform(get(EMPLOYEE_EID_OPTIONS.replace("{" + EMPLOYEE_ID + "}", employeeId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].optionId").value(optionId.toString()))
                .andExpect(jsonPath("$[0].name").value("dark_mode"))
                .andExpect(jsonPath("$[0].enabled").value(true));
    }

    @Test
    @DisplayName("GET /employees/{id}/options should return 401 when unauthorized")
    void getEmployeeOptions_ShouldReturn401WhenUnauthorized() throws Exception {
        UUID employeeId = UUID.randomUUID();

        mockMvc.perform(get(EMPLOYEE_EID_OPTIONS.replace("{" + EMPLOYEE_ID + "}", employeeId.toString())))
                .andExpect(status().isUnauthorized());
    }

    // ----------------------
    // CREATE EMPLOYEE
    // ----------------------
    @Test
    @DisplayName("POST /employees should return 201 when role is MANAGER or higher")
    @WithMockUser(roles = "MANAGER")
    void createEmployee_ShouldReturn201WithManagerRole() throws Exception {
        CreateEmployeeDTO request = CreateEmployeeDTO.builder()
                .firstName("Anna")
                .lastName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .departments(departmentsDTOs())
                .companyId(UUID.randomUUID())
                .roles(Set.of())
                .build();

        CreateAuthUserResult response = CreateAuthUserResult.builder()
                .employeeDTO(EmployeeDTO.builder().build())
                .authUserDTO(AuthUserDTO.builder().username("anna").build())
                .build();

        when(authAPI.createAuthUserWithRelatedEmployee(any())).thenReturn(response);

        mockMvc.perform(post(EMPLOYEES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authUserDTO.username").value("anna"));
    }

    @Test
    @DisplayName("POST /employees should return 401 when user is not authenticated")
    void createEmployee_ShouldReturn401WhenUnauthorized() throws Exception {
        CreateEmployeeDTO request = CreateEmployeeDTO.builder()
                .firstName("Anna")
                .lastName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .departments(departmentsDTOs())
                .companyId(UUID.randomUUID())
                .roles(Set.of())
                .build();

        mockMvc.perform(post(EMPLOYEES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /employees should return 403 when user has insufficient role")
    @WithMockUser(roles = "MECHANIC")
    void createEmployee_ShouldReturn403WithWrongRole() throws Exception {
        CreateEmployeeDTO request = CreateEmployeeDTO.builder()
                .firstName("Anna")
                .lastName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .departments(departmentsDTOs())
                .companyId(UUID.randomUUID())
                .roles(Set.of())
                .build();

        mockMvc.perform(post(EMPLOYEES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ----------------------
    // TERMINATE EMPLOYEE
    // ----------------------
    @Test
    @DisplayName("DELETE /employees/{id} should return 204 when role is allowed")
    @WithMockUser(roles = "MANAGER")
    void terminateEmployee_ShouldReturn204() throws Exception {
        UUID employeeId = UUID.randomUUID();

        when(authAPI.terminateEmployeeAuthUserAccount(employeeId))
                .thenReturn(EmployeeDTO.builder().build());

        mockMvc.perform(delete(EMPLOYEES_EID.replace("{" + EMPLOYEE_ID + "}", employeeId.toString())))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /employees/{id} should return 401 when unauthorized")
    void terminateEmployee_ShouldReturn401WhenUnauthorized() throws Exception {
        UUID employeeId = UUID.randomUUID();

        mockMvc.perform(delete(EMPLOYEES_EID.replace("{" + EMPLOYEE_ID + "}", employeeId.toString())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /employees/{id} should return 403 when role is insufficient")
    @WithMockUser(roles = "MECHANIC")
    void terminateEmployee_ShouldReturn403WithWrongRole() throws Exception {
        UUID employeeId = UUID.randomUUID();

        mockMvc.perform(delete(EMPLOYEES_EID.replace("{" + EMPLOYEE_ID + "}", employeeId.toString())))
                .andExpect(status().isForbidden());
    }

    // ----------------------
    // UPDATE USER CREDENTIALS (SELF ONLY)
    // ----------------------
    @Test
    @DisplayName("PATCH /update-user-credentials should return 200 when updating own credentials")
    @WithMockUser(username = "user1", roles = "USER")
    void updateUserCredentials_ShouldReturn200WhenSelf() throws Exception {
        UUID employeeId = UUID.randomUUID();
        UpdateAuthUserDTO request = UpdateAuthUserDTO.builder()
                .employeeId(employeeId)
                .username("user1")
                .password("newPass123")
                .currentPassword("oldPass123")
                .build();

        AuthUserDTO response =
                AuthUserDTO.builder().employeeId(employeeId).username("user1").build();

        when(authAPI.updateAuthUserCredentials(any(UpdateAuthUserDTO.class))).thenReturn(response);

        mockMvc.perform(patch(UPDATE_USER_CREDENTIALS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()));
    }

    @Test
    @DisplayName("PATCH /update-user-credentials should return 401 when unauthorized")
    void updateUserCredentials_ShouldReturn401WhenUnauthorized() throws Exception {
        UpdateAuthUserDTO request = UpdateAuthUserDTO.builder()
                .employeeId(UUID.randomUUID())
                .username("user1")
                .password("newPass123")
                .currentPassword("oldPass123")
                .build();

        mockMvc.perform(patch(UPDATE_USER_CREDENTIALS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ----------------------
    // UPDATE USER ROLES (MANAGER ONLY)
    // ----------------------
    @Test
    @DisplayName("PATCH /update-user-roles should return 200 when manager")
    @WithMockUser(roles = "MANAGER")
    void updateRoles_ShouldReturn200() throws Exception {
        UUID employeeId = UUID.randomUUID();
        UpdateAuthUserDTO request = UpdateAuthUserDTO.builder()
                .employeeId(employeeId)
                .username("anna")
                .roles(Set.of(RoleDTO.builder().name("ADMIN").build()))
                .build();

        AuthUserDTO response =
                AuthUserDTO.builder().employeeId(employeeId).username("anna").build();

        when(authAPI.updateAuthUserRoles(any(UpdateAuthUserDTO.class))).thenReturn(response);

        mockMvc.perform(patch(UPDATE_USER_ROLES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anna"))
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()));
    }

    @Test
    @DisplayName("PATCH /update-user-roles should return 401 when unauthorized")
    void updateRoles_ShouldReturn401WhenUnauthorized() throws Exception {
        UpdateAuthUserDTO request = UpdateAuthUserDTO.builder()
                .employeeId(UUID.randomUUID())
                .username("anna")
                .build();

        mockMvc.perform(patch(UPDATE_USER_ROLES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /update-user-roles should return 403 when role is insufficient")
    @WithMockUser(roles = "MECHANIC")
    void updateRoles_ShouldReturn403WithWrongRole() throws Exception {
        UpdateAuthUserDTO request = UpdateAuthUserDTO.builder()
                .employeeId(UUID.randomUUID())
                .username("anna")
                .build();

        mockMvc.perform(patch(UPDATE_USER_ROLES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ----------------------
    // Helpers
    // ----------------------
    Set<DepartmentDTO> departmentsDTOs() {
        return Set.of(DepartmentDTO.builder().name("department").build());
    }
}
