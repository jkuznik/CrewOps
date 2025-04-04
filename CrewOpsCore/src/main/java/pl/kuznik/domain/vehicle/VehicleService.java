package pl.kuznik.domain.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.kuznik.domain.vehicle.dto.CreateVehicleDTO;
import pl.kuznik.domain.vehicle.dto.VehicleDTO;

@Service
@RequiredArgsConstructor
@Validated
class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleDTO createVehicle(CreateVehicleDTO createVehicleDTO) {
        return VehicleMapper.mapToDTO(vehicleRepository.save(VehicleMapper.mapToEntity(createVehicleDTO)));
    }
}
