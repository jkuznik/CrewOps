package pl.crewops.domain.machineType;

import static pl.crewops.domain.machineType.MachineTypeMapper.mapToEntity;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.model.MachineType;

@Service
@Slf4j
@RequiredArgsConstructor
class MachineTypeService implements MachineTypeAPI {
    private final MachineTypeRepository machineTypeRepository;

    @Override
    @Transactional
    public MachineType create(CreateMachineTypeDTO createMachineTypeDTO) {
        return machineTypeRepository.save(mapToEntity(createMachineTypeDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MachineType> getMachineTypeByName(String name) {
        return machineTypeRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineTypeDTO> getAllMachineTypes() {
        return machineTypeRepository.findAll().stream()
                .map(MachineTypeMapper::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(MachineType machineType) {
        machineTypeRepository.delete(machineType);
    }
}
