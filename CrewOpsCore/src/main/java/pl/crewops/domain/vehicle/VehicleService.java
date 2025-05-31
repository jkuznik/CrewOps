package pl.crewops.domain.vehicle;

import static pl.crewops.domain.vehicle.VehicleMapper.mapToDTO;
import static pl.crewops.domain.vehicle.VehicleMapper.mapToEntity;
import static pl.crewops.utils.pagination.PageRequestFactory.createPageRequest;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.vehicleType.VehicleTypeAPI;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.CreateVehicleTypeDTO;
import pl.crewops.exception.VehicleNotFoundException;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

@Slf4j
@Service
@RequiredArgsConstructor
class VehicleService implements VehicleAPI {

    private final VehicleRepository vehicleRepository;
    private final VehicleTypeAPI vehicleTypeAPI;

    public VehicleDTO createVehicle(CreateVehicleDTO createVehicleDTO) {
        VehicleType vehicleType = vehicleTypeAPI
                .getVehicleTypeByName(createVehicleDTO.vehicleType().name())
                .orElseGet(() -> vehicleTypeAPI.create(
                        new CreateVehicleTypeDTO(createVehicleDTO.vehicleType().name())));
        Vehicle vehicle = mapToEntity(createVehicleDTO);
        vehicle.setVehicleType(vehicleType);

        log.info("Create vehicle: {}", vehicle);
        return mapToDTO(vehicleRepository.save(vehicle));
    }

    public List<VehicleDTO> getAllVehicles(int page, int size) {
        PageRequest pageRequest =
                createPageRequest(page, size, Sort.by(Sort.Order.asc("make"), Sort.Order.asc("model")));
        log.info("Get all vehicles with paginaition. Page: {}, size {} ", page, size);

        return vehicleRepository.findAll(pageRequest).stream()
                .map(VehicleMapper::mapToDTO)
                .toList();
    }

    public Vehicle getVehicle(UUID vehicleId) throws VehicleNotFoundException {
        return vehicleRepository.findById(vehicleId).orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    public Vehicle getVehicleById(UUID id) throws VehicleNotFoundException {
        log.info("Get vehicle by id {}", id);
        return vehicleRepository.findById(id).orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public VehicleDTO getVehicleByRegistrationNumber(String registerNumber) {
        log.info("Get vehicle by registration number {}", registerNumber);
        return mapToDTO(
                vehicleRepository.findByRegisterNumber(registerNumber).orElseThrow(NoSuchElementException::new));
    }

    public List<VehicleDTO> getVehiclesIn(Set<UUID> ids) {
        log.info("Get vehicles in amount {}, each ids: {}", ids.size(), ids);
        return vehicleRepository.findAllByIdIn(ids).stream()
                .map(VehicleMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public VehicleDTO updateVehicle(UpdateVehicleDTO updateVehicleDTO) {
        Vehicle vehicle = vehicleRepository
                .findById(updateVehicleDTO.vehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(updateVehicleDTO.vehicleId()));

        if (updateVehicleDTO.registerNumber() != null) {
            vehicle.setRegisterNumber(updateVehicleDTO.registerNumber());
        }

        if (updateVehicleDTO.broken() != null) {
            vehicle.setBroken(updateVehicleDTO.broken());
        }

        log.info("Update vehicle {}", vehicle);
        return mapToDTO(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(UUID vehicleId) {
        log.info("Delete vehicle {}", vehicleId);
        vehicleRepository.deleteById(vehicleId);
    }
}
