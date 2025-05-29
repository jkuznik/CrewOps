package pl.crewops.domain.vehicleType;

import static pl.crewops.domain.vehicleType.VehicleTypeMapper.mapToDTO;
import static pl.crewops.domain.vehicleType.VehicleTypeMapper.mapToEntity;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.exception.VehicleTypeNotFoundException;
import pl.crewops.model.VehicleType;

@Service
@Slf4j
@RequiredArgsConstructor
class VehicleTypeService implements VehicleTypeAPI {
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public VehicleTypeDTO create(CreateVehicleTypeDTO createVehicleTypeDTO) {
        return mapToDTO(vehicleTypeRepository.save(mapToEntity(createVehicleTypeDTO)));
    }

    @Override
    public VehicleType getById(UUID id) {
        return vehicleTypeRepository.findById(id).orElseThrow(() -> new VehicleTypeNotFoundException(id));
    }
}
