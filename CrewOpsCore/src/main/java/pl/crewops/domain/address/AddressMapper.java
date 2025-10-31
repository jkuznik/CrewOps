package pl.crewops.domain.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.model.dto.address.AddressDTO;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.tenantSchema.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(CreateAddressDTO createAddressDTO);

    @Mapping(target = "id", source = "id")
    AddressDTO toDTO(Address address);
}
