package pl.crewops.domain.machine;

import static pl.crewops.domain.machine.MachineMapper.mapToDTO;
import static pl.crewops.domain.machine.MachineMapper.mapToEntity;
import static pl.crewops.util.pagination.PageRequestFactory.createPageRequest;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.machineType.MachineTypeAPI;
import pl.crewops.exception.domain.machine.MachineNotFoundException;
import pl.crewops.exception.domain.machine.MachineTypeNotFoundException;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;

@Slf4j
@Service
@RequiredArgsConstructor
class MachineService implements MachineAPI {

    private final MachineRepository machineRepository;
    private final MachineTypeAPI machineTypeAPI;

    @Transactional
    public MachineDTO createMachine(CreateMachineDTO createMachineDTO) {
        MachineType machineType = machineTypeAPI
                .getMachineTypeByName(createMachineDTO.machineType().name())
                .orElseGet(() -> machineTypeAPI.create(
                        new CreateMachineTypeDTO(createMachineDTO.machineType().name())));
        Machine machine = mapToEntity(createMachineDTO);
        machine.setMachineType(machineType);

        log.info("Create machine: {}", machine);
        return mapToDTO(machineRepository.save(machine));
    }

    @Transactional(readOnly = true)
    public List<MachineDTO> getAllMachines(int page, int size) {
        PageRequest pageRequest =
                createPageRequest(page, size, Sort.by(Sort.Order.asc("make"), Sort.Order.asc("model")));
        log.info("Get all machines with paginaition. Page: {}, size {} ", page, size);

        return machineRepository.findAll(pageRequest).stream()
                .map(MachineMapper::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Machine getMachine(UUID machineId) throws MachineNotFoundException {
        return machineRepository.findById(machineId).orElseThrow(() -> new MachineNotFoundException(machineId));
    }

    @Transactional(readOnly = true)
    public Machine getMachineById(UUID id) throws MachineNotFoundException {
        log.info("Get machine by id {}", id);
        return machineRepository.findById(id).orElseThrow(() -> new MachineNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public MachineDTO getMachineByRegistrationNumber(String registerNumber) {
        log.info("Get machine by registration number {}", registerNumber);
        return mapToDTO(
                machineRepository.findByRegisterNumber(registerNumber).orElseThrow(NoSuchElementException::new));
    }

    @Transactional(readOnly = true)
    public List<MachineDTO> getMachinesIn(Set<UUID> ids) {
        log.info("Get machines in amount {}, each ids: {}", ids.size(), ids);
        return machineRepository.findAllByIdIn(ids).stream()
                .map(MachineMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public MachineDTO updateMachine(UpdateMachineDTO updateMachineDTO) {
        Machine machine = machineRepository
                .findById(updateMachineDTO.machineId())
                .orElseThrow(() -> new MachineNotFoundException(updateMachineDTO.machineId()));

        if (updateMachineDTO.registerNumber() != null) {
            machine.setRegisterNumber(updateMachineDTO.registerNumber());
        }

        if (updateMachineDTO.broken() != null) {
            machine.setBroken(updateMachineDTO.broken());
        }

        log.info("Update machine {}", machine);
        return mapToDTO(machineRepository.save(machine));
    }

    @Transactional
    public void deleteMachine(UUID machineId) {
        log.info("Delete machine {}", machineId);
        var machine = machineRepository.findById(machineId).orElseThrow(() -> new MachineNotFoundException(machineId));
        var machineType = machineTypeAPI
                .getMachineTypeByName(machine.getMachineType().getName())
                .orElseThrow(() -> new MachineTypeNotFoundException(
                        machine.getMachineType().getId()));

        if (machineRepository.countByMachineType(machineType) == 1) {
            machineRepository.deleteById(machineId);
            machineTypeAPI.delete(machineType);
        } else {
            machineRepository.deleteById(machineId);
        }
    }
}
