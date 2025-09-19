package pl.crewops.domain.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;

@Validated
public interface CompanyAPI {

    CompanyDTO createCompany(
            @NotNull @Valid CreateAddressDTO createAddressDTO,
            @NotNull @Valid CreateCompanyDTO createCompanyDTO,
            @NotNull UUID companyId);

    void deleteAfterFailedCustomerRegister(@NotNull UUID companyId, @NotNull String schemaName);
}
