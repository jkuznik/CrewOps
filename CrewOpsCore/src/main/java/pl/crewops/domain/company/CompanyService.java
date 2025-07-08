package pl.crewops.domain.company;

import static pl.crewops.domain.company.CompanyMapper.mapToDTO;
import static pl.crewops.domain.company.CompanyMapper.mapToEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.address.AddressAPI;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.exception.domain.company.NoUniqueCompanyEmailException;
import pl.crewops.model.Address;

@Slf4j
@Service
@RequiredArgsConstructor
class CompanyService implements CompanyAPI {

    private final CompanyRepository companyRepository;
    private final AddressAPI addressAPI;

    @Override
    @Transactional
    public CompanyDTO createCompany(
            CreateCompanyDTO createCompanyDTO, @NotNull @Valid CreateAddressDTO createAddressDTO)
            throws NoUniqueCompanyEmailException {
        if (companyRepository.findByEmail(createCompanyDTO.email()).isPresent()) {
            throw new NoUniqueCompanyEmailException(createCompanyDTO.email());
        }
        Address address = addressAPI.createAddress(createAddressDTO);
        var company = mapToEntity(createCompanyDTO);
        company.setAddress(address);
        return mapToDTO(companyRepository.save(company));
    }
}
