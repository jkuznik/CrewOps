package pl.crewops.domain.company;

import pl.crewops.model.dto.address.AddressDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.tenantSchema.Company;

class CompanyMapper {

    static Company mapToEntity(CreateCompanyDTO createCompanyDTO) {
        return Company.builder()
                .name(createCompanyDTO.name())
                .email(createCompanyDTO.email())
                .build();
    }

    static CompanyDTO mapToDTO(Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .address(AddressDTO.builder()
                        .postalCode(company.getAddress().getPostalCode())
                        .city(company.getAddress().getCity())
                        .street(company.getAddress().getStreet())
                        .localNumber(company.getAddress().getLocalNumber())
                        .build())
                .email(company.getEmail())
                .build();
    }
}
