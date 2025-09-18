package pl.crewops.domain.address;

import pl.crewops.model.Address;
import pl.crewops.model.dto.address.AddressDTO;
import pl.crewops.model.dto.address.CreateAddressDTO;

class AddressMapper {

    static Address mapToEntity(CreateAddressDTO createAddressDTO) {
        return Address.builder()
                .postalCode(createAddressDTO.postalCode())
                .city(createAddressDTO.city())
                .street(createAddressDTO.street())
                .localNumber(createAddressDTO.localNumber())
                .build();
    }

    static AddressDTO mapToDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .street(address.getStreet())
                .localNumber(address.getLocalNumber())
                .build();
    }
}
