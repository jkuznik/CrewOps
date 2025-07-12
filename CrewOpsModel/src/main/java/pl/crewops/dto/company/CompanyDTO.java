package pl.crewops.dto.company;

import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.address.AddressDTO;
import pl.crewops.enums.CompanyStatus;

@Builder
public record CompanyDTO(UUID id, String name, AddressDTO address, String email, CompanyStatus status) {}
