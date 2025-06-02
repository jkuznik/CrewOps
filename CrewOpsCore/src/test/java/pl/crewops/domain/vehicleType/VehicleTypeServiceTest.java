package pl.crewops.domain.vehicleType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.vehicleType.VehicleTypeTestFactory.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.VehicleType;

@SpringJUnitConfig(classes = {VehicleTypeService.class, VehicleTypeRepository.class})
class VehicleTypeServiceTest {

    @Autowired
    private VehicleTypeService vehicleTypeService;

    @MockitoBean
    private VehicleTypeRepository vehicleTypeRepository;

    private VehicleType vehicleType;
    private VehicleTypeDTO vehicleTypeDTO;
    private CreateVehicleTypeDTO createVehicleTypeDTO;

    @BeforeEach
    void setUp() {
        vehicleType = createVehicleType();
        vehicleTypeDTO = createVehicleTypeDTO();
        createVehicleTypeDTO = createCreateVehicleTypeDTO();
    }

    @Test
    void create_shouldReturnVehicleTypeDTO_whenCreateVehicleTypeDTOIsValid() {
        // when
        when(vehicleTypeRepository.save(any())).thenReturn(vehicleType);

        VehicleType result = vehicleTypeService.create(createVehicleTypeDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("name");
    }

    @Test
    void getVehicleType_shouldReturnVehicleType_whenEntityExists() {
        // when
        when(vehicleTypeRepository.findByName(any())).thenReturn(Optional.of(vehicleType));

        var result = vehicleTypeService.getVehicleTypeByName("name");

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo("name");
    }

    @Test
    void getAllVehicleTypes_shouldReturnSetOfVehicleTypeDTO_whenEntityExists() {
        // given
        var vehicleTypes = List.of(vehicleType);

        // when
        when(vehicleTypeRepository.findAll()).thenReturn(vehicleTypes);

        List<VehicleTypeDTO> result = vehicleTypeService.getAllVehicleTypes();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(vehicleTypeDTO)).isTrue();
    }

    @Test
    void delete_shouldTriggerDeleteMethod_whenEntityInNotNull() {
        // when
        Mockito.doNothing().when(vehicleTypeRepository).delete(any());
        vehicleTypeService.delete(vehicleType);

        // then
        Mockito.verify(vehicleTypeRepository, Mockito.times(1)).delete(vehicleType);
    }
}
