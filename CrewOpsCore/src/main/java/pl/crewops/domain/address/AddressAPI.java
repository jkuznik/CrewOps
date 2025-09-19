package pl.crewops.domain.address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.Address;
import pl.crewops.model.dto.address.CreateAddressDTO;

@Validated
public interface AddressAPI {

    Address createAddress(@NotNull @Valid CreateAddressDTO createAddressDTO);
}
