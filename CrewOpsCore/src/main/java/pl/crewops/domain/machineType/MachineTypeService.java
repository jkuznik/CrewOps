package pl.crewops.domain.machineType;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.tenantSchema.MachineType;

@Service
@Slf4j
@RequiredArgsConstructor
class MachineTypeService implements MachineTypeAPI {

    private final MachineTypeRepository machineTypeRepository;
    private final MachineTypeMapper mapper;

    @Override
    @Transactional
    public MachineType create(CreateMachineTypeDTO createMachineTypeDTO) {
        return machineTypeRepository.save(mapper.toEntity(createMachineTypeDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MachineType> getMachineTypeByName(String name) {
        return machineTypeRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineTypeDTO> getAllMachineTypes() {
        return machineTypeRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional
    public void delete(MachineType machineType) {
        machineTypeRepository.delete(machineType);
    }
}
