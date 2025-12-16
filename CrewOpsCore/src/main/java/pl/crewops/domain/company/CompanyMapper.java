package pl.crewops.domain.company;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.domain.address.AddressMapper;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.tenantSchema.Company;

@Mapper(
        componentModel = "spring",
        uses = {AddressMapper.class})
public interface CompanyMapper {

    @Mapping(target = "address", ignore = true)
    Company toEntity(CreateCompanyDTO dto);

    CompanyDTO toDTO(Company company);
}
