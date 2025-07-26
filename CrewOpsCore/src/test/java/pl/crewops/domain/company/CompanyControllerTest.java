package pl.crewops.domain.company;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.exception.CoreExceptionHandler;
import pl.crewops.exception.domain.company.CompanyNotFoundException;
import pl.crewops.security.config.TestSecuriityConfig;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CompanyController.class)
@ContextConfiguration(classes = {TestSecuriityConfig.class, CompanyController.class, CoreExceptionHandler.class})
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    private static final UUID COMPANY_ID = UUID.randomUUID();

    @Test
    @WithMockUser
    @DisplayName("GET /companies/{companyId} should return 200 and company")
    void getCompanyById_ShouldReturn200() throws Exception {
        CompanyDTO companyDTO = CompanyDTO.builder()
                .id(COMPANY_ID)
                .name("Test Company")
                .address(null) // or mock AddressDTO if needed
                .email("test@company.com")
                .status(CompanyStatus.ACTIVE)
                .build();

        when(companyService.getCompanyById(COMPANY_ID)).thenReturn(companyDTO);

        mockMvc.perform(get("/companies/" + COMPANY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMPANY_ID.toString()))
                .andExpect(jsonPath("$.name").value("Test Company"))
                .andExpect(jsonPath("$.email").value("test@company.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /companies/{companyId} should return 404 when company not found")
    void getCompanyById_ShouldReturn404WhenNotFound() throws Exception {
        when(companyService.getCompanyById(any())).thenThrow(new CompanyNotFoundException(COMPANY_ID));

        mockMvc.perform(get("/companies/" + COMPANY_ID)).andExpect(status().isNotFound());
    }
}
