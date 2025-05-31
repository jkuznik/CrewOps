package pl.crewops.domain.vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.crewops.domain.vehicle.VehicleTestFactory.createVehicleDTONotValid;
import static pl.crewops.domain.vehicle.VehicleTestFactory.updateVehicleDTONotValid;

import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.domain.vehicleType.VehicleTypeAPI;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.Vehicle;

@Transactional
class VehicleAPITest extends IntegrationTest {

    @Autowired
    private VehicleAPI vehicleAPI;

    @Autowired
    private VehicleTypeAPI vehicleTypeAPI;

    @Test
    void shouldReturnVehicle_whenVehicleExists() {
        // given
        var vehicleId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // when
        var vehicle = vehicleAPI.getVehicle(vehicleId);

        // then
        assertThat(vehicle).isNotNull();
        assertThat(vehicle.getId()).isEqualTo(vehicleId);
        assertThat(vehicle.getVehicleType().getName()).isEqualTo("BULLDOZER");
    }

    @Test
    void createVehicle_shouldReturnVehicle_whenCreateVehicleDTOIsValid_andVehicleTypeIsExist() {
        // given
        var createVehicleDTO = CreateVehicleDTO.builder()
                .make("make")
                .model("model")
                .vehicleType(VehicleTypeDTO.builder().name("LOADER").build())
                .year(2020)
                .registerNumber("number")
                .broken(false)
                .build();

        // when
        var vehicle = vehicleAPI.createVehicle(createVehicleDTO);

        Vehicle result = vehicleAPI.getVehicle(vehicle.id());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getVehicleType().getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    }

    @Test
    void createVehicle_shouldReturnVehicle_whenCreateVehicleDTOIsValid_andVehicleTypeIsNotExist() {
        // given
        var createVehicleDTO = CreateVehicleDTO.builder()
                .make("make")
                .model("model")
                .vehicleType(VehicleTypeDTO.builder().name("NEW TYPE").build())
                .year(2020)
                .registerNumber("number")
                .broken(false)
                .build();

        // when
        var vehicle = vehicleAPI.createVehicle(createVehicleDTO);

        Vehicle result = vehicleAPI.getVehicle(vehicle.id());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getVehicleType()).isNotNull();
        assertThat(result.getVehicleType().getName()).isEqualTo("NEW TYPE");
    }

    @Test
    void shouldThrowException_whenCreateVehicleDTOIsNull() {
        Exception result = catchException(() -> vehicleAPI.createVehicle(null));

        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenCreateVehicleDTOIsInvalid() {
        // given
        var createVehicleDTONotValid = createVehicleDTONotValid();

        // when
        Exception result = catchException(() -> vehicleAPI.createVehicle(createVehicleDTONotValid));

        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldThrowException_whenUpdateVehicleDTOIsInvalid() {
        // given
        var updateVehicleDTONotValid = updateVehicleDTONotValid();

        // when
        Exception result = catchException(() -> vehicleAPI.updateVehicle(updateVehicleDTONotValid));

        assertThat(result).isInstanceOf(ConstraintViolationException.class);
    }
}
