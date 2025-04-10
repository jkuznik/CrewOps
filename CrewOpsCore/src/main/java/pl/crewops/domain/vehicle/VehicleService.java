package pl.crewops.domain.vehicle;

import static pl.crewops.domain.vehicle.VehicleMapper.mapToDTO;
import static pl.crewops.domain.vehicle.VehicleMapper.mapToEntity;
import static pl.crewops.utils.pagination.PageRequestFactory.createPageRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Vehicle;

@Service
@RequiredArgsConstructor
@Validated
class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleDTO createVehicle(@Valid @NotNull CreateVehicleDTO createVehicleDTO) {
        return mapToDTO(vehicleRepository.save(mapToEntity(createVehicleDTO)));
    }

    public List<VehicleDTO> getAllVehicles(int page, int size) {
        PageRequest pageRequest =
                createPageRequest(page, size, Sort.by(Sort.Order.asc("make"), Sort.Order.asc("model")));

        return vehicleRepository.findAll(pageRequest).stream()
                .map(VehicleMapper::mapToDTO)
                .toList();
    }

    public Vehicle getVehicleById(@NotNull UUID id) throws VehicleNotFoundException {
        return vehicleRepository.findById(id).orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public List<VehicleDTO> getVehiclesIn(Set<UUID> ids) {
        return vehicleRepository.findAllByIdIn(ids).stream()
                .map(VehicleMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public VehicleDTO updateVehicle(@Valid @NotNull UpdateVehicleDTO updateVehicleDTO) {
        Vehicle vehicle = vehicleRepository
                .findById(updateVehicleDTO.vehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(updateVehicleDTO.vehicleId()));

        if (updateVehicleDTO.registerNumber() != null) {
            vehicle.setRegisterNumber(updateVehicleDTO.registerNumber());
        }

        if (updateVehicleDTO.broken() != null) {
            vehicle.setBroken(updateVehicleDTO.broken());
        }

        return mapToDTO(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(@NotNull UUID vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }
}
