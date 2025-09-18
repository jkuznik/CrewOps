package pl.crewops.model.dto.tenant;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.NonNull;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;

@Builder
public record CreateTenantDTO(
        @NonNull @Valid CreateCompanyDTO createCompanyDTO, @NonNull @Valid CreateAddressDTO createAddressDTO) {}
