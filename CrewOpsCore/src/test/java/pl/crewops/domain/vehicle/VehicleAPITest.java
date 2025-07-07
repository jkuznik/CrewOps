package pl.crewops.domain.vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.crewops.domain.vehicle.VehicleTestFactory.createVehicleDTONotValid;
import static pl.crewops.domain.vehicle.VehicleTestFactory.updateVehicleDTONotValid;

import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.exception.domain.vehicle.VehicleNotFoundException;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

@Transactional
class VehicleAPITest extends IntegrationTest {

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

    @Test
    void deleteVehicle_shouldDeleteVehicle_whenVehicleExists() {
        // given
        var vehicleId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // when
        vehicleAPI.deleteVehicle(vehicleId);

        Exception result = catchException(() -> vehicleAPI.getVehicle(vehicleId));

        // then
        assertThat(result).isInstanceOf(VehicleNotFoundException.class);
    }

    @Test
    void deleteVehicle_shouldDeleteVehicleAndVehicleType_whenExistOnlyOneVehicleWithCurrentVehicleType() {
        // given
        var vehicleId = UUID.fromString("77777777-cccc-cccc-cccc-cccccccccccc"); // only one vehicle with 'loader' type
        Optional<VehicleType> before = vehicleTypeAPI.getVehicleTypeByName("LOADER");

        // when
        vehicleAPI.deleteVehicle(vehicleId);

        Optional<VehicleType> after = vehicleTypeAPI.getVehicleTypeByName("LOADER");
        Exception result = catchException(() -> vehicleAPI.getVehicle(vehicleId));

        // then
        assertThat(before.isPresent()).isTrue();
        assertThat(after.isPresent()).isFalse();
        assertThat(result).isInstanceOf(VehicleNotFoundException.class);
    }
}
