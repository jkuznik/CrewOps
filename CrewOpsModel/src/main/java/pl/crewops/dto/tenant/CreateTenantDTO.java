package pl.crewops.dto.tenant;

import lombok.Builder;
import lombok.NonNull;
import pl.crewops.dto.address.CreateAddressDTO;
import pl.crewops.dto.company.CreateCompanyDTO;

@Builder
public record CreateTenantDTO(@NonNull CreateCompanyDTO createCompanyDTO, @NonNull CreateAddressDTO createAddressDTO) {}
