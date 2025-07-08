package pl.crewops.dto.address;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AddressDTO(UUID id, String postalCode, String city, String street, String localNumber) {}
