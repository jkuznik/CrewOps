package pl.crewops.domain.vehicle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;

@Transactional
class VehicleAPITest extends IntegrationTest {

    @Autowired
    private VehicleAPI vehicleAPI;

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
}
