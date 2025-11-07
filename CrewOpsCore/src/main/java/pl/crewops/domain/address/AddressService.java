package pl.crewops.domain.address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.tenantSchema.Address;

@Slf4j
@Service
@RequiredArgsConstructor
class AddressService implements AddressAPI {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public Address createAddress(CreateAddressDTO createAddressDTO) {
        var address = addressMapper.toEntity(createAddressDTO);
        return addressRepository.save(address);
    }
}
