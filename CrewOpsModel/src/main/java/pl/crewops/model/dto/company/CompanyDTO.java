package pl.crewops.model.dto.company;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.model.dto.address.AddressDTO;

@Builder
public record CompanyDTO(UUID id, String name, AddressDTO address, String email, CompanyStatus status)
        implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompanyDTO that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
