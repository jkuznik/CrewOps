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
import pl.crewops.domain.address.AddressMapper;
import pl.crewops.exception.domain.company.CompanyNotFoundException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.dto.address.AddressDTO;
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

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // CREATE COMPANY
    // ---------------------------------------------------------

    @Test
    void createCompany_shouldReturnCompanyDTO_whenDataIsValid() {

        // given
        UUID companyId = UUID.randomUUID();

        CreateAddressDTO createAddressDTO = CreateAddressDTO.builder().build();
        CreateCompanyDTO createCompanyDTO = CreateCompanyDTO.builder()
                .name("New Company")
                .email("mail@test.com")
                .build();

        Address createdAddress = new Address();
        when(addressAPI.createAddress(createAddressDTO)).thenReturn(createdAddress);

        Company mappedEntity = new Company();
        mappedEntity.setName("New Company");
        mappedEntity.setEmail("mail@test.com");

        when(companyMapper.toEntity(createCompanyDTO)).thenReturn(mappedEntity);

        Company savedCompany = new Company();
        savedCompany.setId(companyId);
        savedCompany.setAddress(createdAddress);
        savedCompany.setName("New Company");
        savedCompany.setEmail("mail@test.com");

        when(companyRepository.save(mappedEntity)).thenReturn(savedCompany);

        AddressDTO addressDTO = AddressDTO.builder().build();
        when(addressMapper.toDTO(createdAddress)).thenReturn(addressDTO);

        CompanyDTO resultDTO = CompanyDTO.builder()
                .id(companyId)
                .name("New Company")
                .email("mail@test.com")
                .address(addressDTO)
                .build();

        when(companyMapper.toDTO(savedCompany)).thenReturn(resultDTO);

        // when
        CompanyDTO result = companyService.createCompany(createAddressDTO, createCompanyDTO, companyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(companyId);
        assertThat(result.name()).isEqualTo("New Company");

        verify(addressAPI).createAddress(createAddressDTO);
        verify(companyMapper).toEntity(createCompanyDTO);
        verify(companyRepository).save(mappedEntity);
        verify(companyMapper).toDTO(savedCompany);
    }

    // ---------------------------------------------------------
    // GET COMPANY
    // ---------------------------------------------------------

    @Test
    void getCompanyById_shouldReturnCompanyDTO_whenCompanyExists() {

        // given
        UUID companyId = UUID.randomUUID();

        Company company = new Company();
        company.setId(companyId);

        Address address = new Address();
        address.setCity("Warsaw");
        company.setAddress(address);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        AddressDTO addressDTO = AddressDTO.builder().city("Warsaw").build();

        when(addressMapper.toDTO(address)).thenReturn(addressDTO);

        CompanyDTO companyDTO =
                CompanyDTO.builder().id(companyId).address(addressDTO).build();

        when(companyMapper.toDTO(company)).thenReturn(companyDTO);

        // when
        CompanyDTO result = companyService.getCompanyById(companyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(companyId);
        assertThat(result.address().city()).isEqualTo("Warsaw");

        verify(companyRepository).findById(companyId);
        verify(companyMapper).toDTO(company);
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

    // ---------------------------------------------------------
    // DELETE AFTER FAILED REGISTER
    // ---------------------------------------------------------

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
