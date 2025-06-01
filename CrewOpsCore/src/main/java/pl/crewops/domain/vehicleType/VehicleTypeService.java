package pl.crewops.domain.vehicleType;

import static pl.crewops.domain.vehicleType.VehicleTypeMapper.mapToEntity;

import java.util.List;
import java.util.Optional;
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
    public List<VehicleTypeDTO> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll().stream()
                .map(VehicleTypeMapper::mapToDTO)
                .toList();
    }

    @Override
    public void delete(VehicleType vehicleType) {
        vehicleTypeRepository.delete(vehicleType);
    }
}
