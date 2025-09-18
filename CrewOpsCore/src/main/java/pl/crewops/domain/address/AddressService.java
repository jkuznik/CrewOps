package pl.crewops.domain.address;

import static pl.crewops.domain.address.AddressMapper.mapToEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.model.Address;
import pl.crewops.model.dto.address.CreateAddressDTO;

@Slf4j
@Service
@RequiredArgsConstructor
class AddressService implements AddressAPI {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Address createAddress(CreateAddressDTO createAddressDTO) {
        var address = mapToEntity(createAddressDTO);
        System.out.println("update");
        return addressRepository.save(address);
    }
}
