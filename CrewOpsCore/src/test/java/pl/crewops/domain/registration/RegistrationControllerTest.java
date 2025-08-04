package pl.crewops.domain.registration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.crewops.dto.address.AddressDTO;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.auth.AuthUserDTO;
import pl.crewops.dto.auth.CreateAuthUserResult;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;
import pl.crewops.security.config.TestSecuriityConfig;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(
        classes = {TestSecuriityConfig.class, RegistrationController.class, MethodValidationPostProcessor.class})
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /register should return 400 when postalCode is blank")
    void registerCustomer_ShouldReturn400_BlankPostalCode() throws Exception {
        var address = CreateAddressDTO.builder()
                .postalCode("  ")
                .city("Warsaw")
                .street("Main St")
                .localNumber("10A")
                .build();

        var company = CreateCompanyDTO.builder()
                .name("Valid Company")
                .email("contact@company.com")
                .build();

        var tenant = CreateTenantDTO.builder()
                .createCompanyDTO(company)
                .createAddressDTO(address)
                .build();

        var employee = CreateEmployeeDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+48123123123")
                .build();

        var command = CreateCustomerCommand.builder()
                .createTenantDTO(tenant)
                .createEmployeeDTO(employee)
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /register should return 400 when email is invalid")
    void registerCustomer_ShouldReturn400_InvalidEmail() throws Exception {
        var address = CreateAddressDTO.builder()
                .postalCode("00-001")
                .city("Warsaw")
                .street("Main St")
                .localNumber("10A")
                .build();

        var company = CreateCompanyDTO.builder()
                .name("Valid Company")
                .email("not-an-email") // Invalid
                .build();

        var tenant = CreateTenantDTO.builder()
                .createCompanyDTO(company)
                .createAddressDTO(address)
                .build();

        var employee = CreateEmployeeDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+48123123123")
                .build();

        var command = CreateCustomerCommand.builder()
                .createTenantDTO(tenant)
                .createEmployeeDTO(employee)
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /register should return 400 when CreateEmployeeDTO is null")
    void registerCustomer_ShouldReturn400_NullEmployee() throws Exception {
        var address = CreateAddressDTO.builder()
                .postalCode("00-001")
                .city("Warsaw")
                .street("Main St")
                .localNumber("10A")
                .build();

        var company = CreateCompanyDTO.builder()
                .name("Company")
                .email("admin@company.com")
                .build();

        var tenant = CreateTenantDTO.builder()
                .createCompanyDTO(company)
                .createAddressDTO(address)
                .build();

        var command = CreateCustomerCommand.builder()
                .createTenantDTO(tenant)
                .createEmployeeDTO(null)
                .build();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /register should return 400 when request body is invalid")
    void registerCustomer_ShouldReturn400ForInvalidBody() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /register should return 200 and full result when data is valid and user is SYSTEM_ADMIN")
    void registerCustomer_ShouldReturn200_WhenDataIsValidAndRoleIsSystemAdmin() throws Exception {
        // Given
        UUID companyId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID authUserId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

        var createCustomerCommand = CreateCustomerCommand.builder()
                .createTenantDTO(CreateTenantDTO.builder()
                        .createCompanyDTO(CreateCompanyDTO.builder()
                                .name("Tech Solutions")
                                .email("info@techsolutions.com")
                                .taxId("testTaxId")
                                .build())
                        .createAddressDTO(CreateAddressDTO.builder()
                                .postalCode("00-001")
                                .city("Warsaw")
                                .street("Main Street")
                                .localNumber("10A")
                                .build())
                        .build())
                .createEmployeeDTO(CreateEmployeeDTO.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .birthDate(LocalDate.of(1990, 1, 1))
                        .phoneNumber("123456789")
                        .department("HR")
                        .companyId(companyId)
                        .build())
                .build();

        var result = CreateCustomerResult.builder()
                .companyDTO(CompanyDTO.builder()
                        .id(companyId)
                        .name("Tech Solutions")
                        .email("info@techsolutions.com")
                        .address(AddressDTO.builder()
                                .id(addressId)
                                .postalCode("00-001")
                                .city("Warsaw")
                                .street("Main Street")
                                .localNumber("10A")
                                .build())
                        .status(CompanyStatus.ACTIVE)
                        .build())
                .authUserResult(CreateAuthUserResult.builder()
                        .employeeDTO(EmployeeDTO.builder()
                                .id(employeeId)
                                .firstName("John")
                                .lastName("Doe")
                                .birthDate(LocalDate.of(1990, 1, 1))
                                .phoneNumber("123456789")
                                .department("HR")
                                .roles(Set.of())
                                .qualifications(Set.of())
                                .machines(Set.of())
                                .active(true)
                                .build())
                        .authUserDTO(AuthUserDTO.builder()
                                .id(authUserId)
                                .username("jdoe")
                                .password("securePass123")
                                .employeeId(employeeId)
                                .roles(Set.of())
                                .tenant(TenantDTO.builder().id(tenantId).build())
                                .build())
                        .build())
                .build();

        when(registrationService.registerCustomer(any())).thenReturn(result);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyDTO.name").value("Tech Solutions"))
                .andExpect(jsonPath("$.companyDTO.email").value("info@techsolutions.com"))
                .andExpect(jsonPath("$.authUserResult.employeeDTO.firstName").value("John"))
                .andExpect(jsonPath("$.authUserResult.authUserDTO.username").value("jdoe"));
    }
}
