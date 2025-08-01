package pl.crewops.dto.address;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AddressDTO(UUID id, String postalCode, String city, String street, String localNumber)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AddressDTO that)) return false;
        return Objects.equals(city(), that.city())
                && Objects.equals(street(), that.street())
                && Objects.equals(postalCode(), that.postalCode())
                && Objects.equals(localNumber(), that.localNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(postalCode(), city(), street(), localNumber());
    }
}
