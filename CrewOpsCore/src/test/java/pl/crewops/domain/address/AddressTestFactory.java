package pl.crewops.domain.address;

import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.tenantSchema.Address;

class AddressTestFactory {

    public static Address addressEntity() {
        return Address.builder()
                .postalCode("postalCode")
                .city("city")
                .street("street")
                .localNumber("localNumber")
                .build();
    }

    public static CreateAddressDTO validCreateAddressDTO() {
        return CreateAddressDTO.builder()
                .postalCode("postalCode")
                .city("city")
                .street("street")
                .localNumber("localNumber")
                .build();
    }

    public static CreateAddressDTO invalidCreateAddressDTO() {
        return CreateAddressDTO.builder()
                .postalCode("postalCode")
                .city("city")
                .street("street")
                .localNumber(null)
                .build();
    }
}
