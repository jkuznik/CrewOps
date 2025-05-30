package pl.crewops.domain.vehicleType;

import static pl.crewops.domain.vehicleType.VehicleTypeMapper.mapToEntity;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.VehicleType;

@Service
@Slf4j
@RequiredArgsConstructor
class VehicleTypeService implements VehicleTypeAPI {
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public VehicleType create(CreateVehicleTypeDTO createVehicleTypeDTO) {
        return vehicleTypeRepository.save(mapToEntity(createVehicleTypeDTO));
    }

    @Override
    public Optional<VehicleType> getVehicleTypeByName(String name) {
        return vehicleTypeRepository.findByName(name);
    }

    @Override
    public Set<VehicleTypeDTO> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll().stream()
                .map(VehicleTypeMapper::mapToDTO)
                .collect(Collectors.toSet());
    }
}
