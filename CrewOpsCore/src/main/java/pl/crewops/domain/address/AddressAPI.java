package pl.crewops.domain.address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.tenantSchema.Address;

@Validated
public interface AddressAPI {

    // todo: unit test + integration tests

    Address createAddress(@NotNull @Valid CreateAddressDTO createAddressDTO);
}
