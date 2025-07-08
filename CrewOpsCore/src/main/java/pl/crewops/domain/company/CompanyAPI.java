package pl.crewops.domain.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.company.CreateCompanyDTO;

@Validated
public interface CompanyAPI {

    CompanyDTO createCompany(
            @NotNull @Valid CreateCompanyDTO createCompanyDTO, @NotNull @Valid CreateAddressDTO createAddressDTO);
}
