package pl.crewops.domain.registration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.crewops.enums.ControllerURL.REGISTER;
import static pl.crewops.enums.ControllerURL.VERIFY_EMAIL;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.crewops.enums.CompanyStatus;
import pl.crewops.model.dto.address.AddressDTO;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.auth.AuthUserDTO;
import pl.crewops.model.dto.auth.CreateAuthUserResult;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;
import pl.crewops.model.dto.tenant.CreateTenantDTO;
import pl.crewops.model.dto.tenant.TenantDTO;
import pl.crewops.security.config.TestSecuriityConfig;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RegistrationController.class)
@ContextConfiguration(
        classes = {TestSecuriityConfig.class, RegistrationController.class, MethodValidationPostProcessor.class})
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistrationService registrationService;

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /register should return 400 when postalCode is blank")
    void registerCustomer_ShouldReturn400_BlankPostalCode() throws Exception {
        var command = CreateCustomerCommand.builder()
                .createTenantDTO(CreateTenantDTO.builder()
                        .createCompanyDTO(CreateCompanyDTO.builder()
                                .name("Valid Company")
                                .email("contact@company.com")
                                .build())
                        .createAddressDTO(CreateAddressDTO.builder()
                                .postalCode("  ") // invalid
                                .city("Warsaw")
                                .street("Main St")
                                .localNumber("10A")
                                .build())
                        .build())
                .createEmployeeDTO(CreateEmployeeDTO.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .phoneNumber("+48123123123")
                        .build())
                .build();

        mockMvc.perform(post(REGISTER).contentType(MediaType.APPLICATION_JSON).content(toJson(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register should return 200 and PreRegisterResponse when valid")
    void registerCustomer_ShouldReturn200_PreRegisterResponse_WhenDataIsValid() throws Exception {
        UUID registrationId = UUID.randomUUID();
        var command = CreateCustomerCommand.builder()
                .createTenantDTO(CreateTenantDTO.builder()
                        .createCompanyDTO(CreateCompanyDTO.builder()
                                .name("Tech Solutions")
                                .email("info@techsolutions.com")
                                .taxId("1234567890") // ✅ valid tax ID format
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
                        .phoneNumber("+48123456789")
                        .companyId(UUID.randomUUID())
                        .build())
                .build();

        var response = new PreRegisterResponse(
                registrationId, PreRegisterResponse.PreRegisterResponseCode.EMAIL_VERIFICATION_REQUIRED);

        when(registrationService.registerCustomer(command)).thenReturn(response);

        mockMvc.perform(post(REGISTER).contentType(MediaType.APPLICATION_JSON).content(toJson(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationId").value(registrationId.toString()))
                .andExpect(jsonPath("$.code").value("EMAIL_VERIFICATION_REQUIRED"));

        verify(registrationService).registerCustomer(any());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    @DisplayName("POST /verify should return 200 and CreateCustomerResult when valid")
    void finalizeRegisterCustomer_ShouldReturn200_WhenVerificationIsValid() throws Exception {
        UUID registrationId = UUID.randomUUID();
        var verifyEmailRequest = new VerifyEmailRequest(registrationId, 12345, "subject", "body");

        UUID companyId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID authUserId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

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
                                .phoneNumber("123456789")
                                .departments(Set.of(DepartmentDTO.builder()
                                        .name("department")
                                        .build()))
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

        when(registrationService.finalizeRegisterCustomer(any())).thenReturn(result);

        mockMvc.perform(post(VERIFY_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(verifyEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyDTO.name").value("Tech Solutions"))
                .andExpect(jsonPath("$.authUserResult.authUserDTO.username").value("jdoe"));

        verify(registrationService).finalizeRegisterCustomer(any());
    }
}
