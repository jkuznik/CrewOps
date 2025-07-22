package pl.crewops.domain.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.company.CreateCompanyDTO;
import pl.crewops.exception.domain.company.NoUniqueCompanyEmailException;

@Validated
public interface CompanyAPI {

    CompanyDTO createCompany(
            @NotNull @Valid CreateAddressDTO createAddressDTO,
            @NotNull @Valid CreateCompanyDTO createCompanyDTO,
            @NotNull UUID companyId)
            throws NoUniqueCompanyEmailException;

    void delete(@NotNull UUID companyId);
}
