package pl.crewops.domain.vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.domain.vehicle.VehicleTestFactory.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.vehicleType.VehicleTypeAPI;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

@SpringJUnitConfig(classes = {VehicleService.class})
class VehicleServiceTest {

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private VehicleTypeAPI vehicleTypeAPI;

    @Autowired
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private CreateVehicleDTO createVehicleDTO;
    private UpdateVehicleDTO updateVehicleDTOValid;
    private VehicleType vehicleType;
    private final UUID vehicleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        vehicle = createVehicle();
        createVehicleDTO = createVehicleDTO();
        updateVehicleDTOValid = updateVehicleDTO();
        vehicleType = createVehicleType();
    }

    @Test
    void shouldCreateVehicle_whenCreateVehicleDTOIsValid() {
        when(vehicleTypeAPI.getVehicleTypeByName(any())).thenReturn(Optional.of(vehicleType));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleDTO result = vehicleService.createVehicle(createVehicleDTO);

        assertThat(result).isNotNull();
        assertThat(result.make()).isEqualTo("make");
        assertThat(result.model()).isEqualTo("model");
        assertThat(result.vehicleType().name()).isEqualTo("name");
    }

    @Test
    void shouldReturnListOfVehicles_whenVehiclesExist() {
        Page<Vehicle> vehicles = new PageImpl<>(Collections.singletonList(vehicle));
        when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(vehicles);

        List<VehicleDTO> result = vehicleService.getAllVehicles(0, 5);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).make()).isEqualTo("make");
    }

    @Test
    void shouldReturnVehicle_whenVehicleExists() {
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.getVehicleById(vehicleId);

        assertThat(result).isNotNull();
        assertThat(result.getRegisterNumber()).isEqualTo("registerNumber");
    }

    @Test
    void shouldThrowException_whenVehicleDoesNotExist() {
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception result = catchException(() -> vehicleService.getVehicleById(vehicleId));

        assertThat(result).isInstanceOf(VehicleNotFoundException.class);
    }

    @Test
    void shouldUpdateVehicle_whenUpdateVehicleDTOIsValid() {
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleDTO result = vehicleService.updateVehicle(updateVehicleDTOValid);

        assertThat(result).isNotNull();
        assertThat(result.registerNumber()).isEqualTo(updateVehicleDTOValid.registerNumber());
    }

    @Test
    void shouldThrowException_whenVehicleToUpdateDoesNotExist() {
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception result = catchException(() -> vehicleService.updateVehicle(updateVehicleDTOValid));

        assertThat(result).isInstanceOf(VehicleNotFoundException.class);
    }

    @Test
    void shouldDeleteVehicle_whenIdIsValid() {
        // when
        when(vehicleRepository.findById(any(UUID.class))).thenReturn(Optional.of(vehicle));
        when(vehicleTypeAPI.getVehicleTypeByName(any())).thenReturn(Optional.of(vehicleType));
        doNothing().when(vehicleRepository).deleteById(vehicleId);

        vehicleService.deleteVehicle(vehicleId);

        verify(vehicleRepository, times(1)).deleteById(vehicleId);
    }
}
