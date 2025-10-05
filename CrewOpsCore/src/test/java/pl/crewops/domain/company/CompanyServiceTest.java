package pl.crewops.domain.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.crewops.domain.address.AddressAPI;
import pl.crewops.exception.domain.company.CompanyNotFoundException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.tenantSchema.Address;
import pl.crewops.model.tenantSchema.Company;

class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AddressAPI addressAPI;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCompany_shouldReturnCompanyDTO_whenDataIsValid() {
        // given
        UUID companyId = UUID.randomUUID();
        CreateAddressDTO addressDTO = CreateAddressDTO.builder().build();
        CreateCompanyDTO companyDTO = CreateCompanyDTO.builder().build();
        Address createdAddress = new Address();
        when(addressAPI.createAddress(addressDTO)).thenReturn(createdAddress);

        Company savedCompany = new Company();
        savedCompany.setId(companyId);
        savedCompany.setAddress(createdAddress);
        when(companyRepository.save(any())).thenReturn(savedCompany);

        // when
        CompanyDTO result = companyService.createCompany(addressDTO, companyDTO, companyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(companyId);
        verify(addressAPI).createAddress(addressDTO);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void getCompanyById_shouldReturnCompanyDTO_whenCompanyExists() {
        // given
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);

        // create an Address to avoid NPE in mapper
        Address address = new Address();
        address.setPostalCode("12345");
        address.setCity("Warsaw");
        address.setStreet("Main Street");
        address.setLocalNumber("10A");
        company.setAddress(address);

        company.setName("Test Company");
        company.setEmail("test@example.com");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        // when
        CompanyDTO result = companyService.getCompanyById(companyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(companyId);
        assertThat(result.address().city()).isEqualTo("Warsaw");
        assertThat(result.name()).isEqualTo("Test Company");
        verify(companyRepository).findById(companyId);
    }

    @Test
    void getCompanyById_shouldThrowException_whenCompanyNotFound() {
        // given
        UUID companyId = UUID.randomUUID();
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.getCompanyById(companyId))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(companyId.toString());
        verify(companyRepository).findById(companyId);
    }

    @Test
    void deleteAfterFailedCustomerRegister_shouldDeleteCompany_andRestoreTenant() {
        // given
        UUID companyId = UUID.randomUUID();
        String originalTenant = "originalTenant";
        String schemaName = "testSchema";
        TenantContext.setCurrentTenant(originalTenant);

        Company company = new Company();
        company.setId(companyId);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        // when
        companyService.deleteAfterFailedCustomerRegister(companyId, schemaName);

        // then
        verify(companyRepository).delete(company);
        assertThat(TenantContext.getCurrentTenant()).isEqualTo(originalTenant);
    }

    @Test
    void deleteAfterFailedCustomerRegister_shouldThrowException_whenCompanyNotFound() {
        // given
        UUID companyId = UUID.randomUUID();
        String schemaName = "testSchema";
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.deleteAfterFailedCustomerRegister(companyId, schemaName))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining(companyId.toString());
    }
}
