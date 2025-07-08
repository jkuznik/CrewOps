package pl.crewops.domain.company;

import pl.crewops.dto.address.AddressDTO;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.model.Company;

class CompanyMapper {

    static Company mapToEntity(CreateCompanyDTO createCompanyDTO) {
        return Company.builder()
                .id(createCompanyDTO.id())
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
