package pl.crewops.domain.company;

import static pl.crewops.domain.company.CompanyMapper.mapToDTO;
import static pl.crewops.domain.company.CompanyMapper.mapToEntity;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.address.AddressAPI;
import pl.crewops.exception.domain.company.CompanyNotFoundException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.tenantSchema.Address;
import pl.crewops.model.tenantSchema.Company;

@Slf4j
@Service
@RequiredArgsConstructor
class CompanyService implements CompanyAPI {

    private final CompanyRepository companyRepository;
    private final AddressAPI addressAPI;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompanyDTO createCompany(
            CreateAddressDTO createAddressDTO, CreateCompanyDTO createCompanyDTO, UUID companyId) {
        Address address = addressAPI.createAddress(createAddressDTO);
        var company = mapToEntity(createCompanyDTO);
        company.setId(companyId);
        company.setAddress(address);
        return mapToDTO(companyRepository.save(company));
    }

    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(UUID companyId) {
        Company company =
                companyRepository.findById(companyId).orElseThrow(() -> new CompanyNotFoundException(companyId));
        log.info("Get company by id: {}", companyId);
        return mapToDTO(company);
    }

    @Override
    @Transactional
    public void deleteAfterFailedCustomerRegister(UUID companyId, String schemaName) {
        String currentTenant = TenantContext.getCurrentTenant();
        TenantContext.setCurrentTenant(schemaName);
        Company company =
                companyRepository.findById(companyId).orElseThrow(() -> new CompanyNotFoundException(companyId));
        companyRepository.delete(company);
        TenantContext.setCurrentTenant(currentTenant);
    }
}
