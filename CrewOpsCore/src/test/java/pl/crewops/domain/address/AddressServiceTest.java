package pl.crewops.domain.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.crewops.model.dto.address.CreateAddressDTO;

@SpringJUnitConfig(
        classes = {
            AddressService.class,
            AddressRepository.class,
            AddressMapper.class,
            MethodValidationPostProcessor.class
        })
class AddressServiceTest {

    @Autowired
    private AddressAPI addressService;

    @MockitoBean
    private AddressRepository addressRepository;

    @MockitoBean
    private AddressMapper addressMapper;

    @Test
    void shouldCreateAddress_whenCreateAddressDTOIsValid() {
        // given
        var createAddressDTO = AddressTestFactory.validCreateAddressDTO();
        var addressEntity = AddressTestFactory.addressEntity();

        // when
        when(addressMapper.toEntity(any(CreateAddressDTO.class))).thenReturn(addressEntity);
        when(addressRepository.save(addressEntity)).thenReturn(addressEntity);

        // then
        var resutl = addressService.createAddress(createAddressDTO);

        assertThat(resutl).isNotNull();
        assertThat("city".equals(resutl.getCity()));
    }

    @Test
    void shouldThrowException_whenCreateAddressDTOIsInvalid() {
        // given
        var createAddressDTO = AddressTestFactory.invalidCreateAddressDTO();
        var addressEntity = AddressTestFactory.addressEntity();

        // when
        when(addressMapper.toEntity(any(CreateAddressDTO.class))).thenReturn(addressEntity);
        var exception = catchException(() -> addressService.createAddress(createAddressDTO));

        // then
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(ConstraintViolationException.class);
    }
}
