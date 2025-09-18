package pl.crewops.domain.machineType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.domain.machineType.MachineTypeTestFactory.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.model.MachineType;
import pl.crewops.model.dto.machineType.CreateMachineTypeDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;

@SpringJUnitConfig(classes = {MachineTypeService.class, MachineTypeRepository.class})
class MachineTypeServiceTest {

    @Autowired
    private MachineTypeService machineTypeService;

    @MockitoBean
    private MachineTypeRepository machineTypeRepository;

    private MachineType machineType;
    private MachineTypeDTO machineTypeDTO;
    private CreateMachineTypeDTO createMachineTypeDTO;

    @BeforeEach
    void setUp() {
        machineType = machineType();
        machineTypeDTO = machineTypeDTO();
        createMachineTypeDTO = createMachineTypeDTO();
    }

    @Test
    void create_shouldReturnMachineTypeDTO_whenCreateMachineTypeDTOIsValid() {
        // when
        when(machineTypeRepository.save(any())).thenReturn(machineType);

        MachineType result = machineTypeService.create(createMachineTypeDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("name");
    }

    @Test
    void getMachineType_shouldReturnMachineType_whenEntityExists() {
        // when
        when(machineTypeRepository.findByName(any())).thenReturn(Optional.of(machineType));

        var result = machineTypeService.getMachineTypeByName("name");

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo("name");
    }

    @Test
    void getAllMachineTypes_shouldReturnSetOfMachineTypeDTO_whenEntityExists() {
        // given
        var machineTypes = List.of(machineType);

        // when
        when(machineTypeRepository.findAll()).thenReturn(machineTypes);

        List<MachineTypeDTO> result = machineTypeService.getAllMachineTypes();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(machineTypeDTO)).isTrue();
    }

    @Test
    void delete_shouldTriggerDeleteMethod_whenEntityInNotNull() {
        // when
        Mockito.doNothing().when(machineTypeRepository).delete(any());
        machineTypeService.delete(machineType);

        // then
        Mockito.verify(machineTypeRepository, Mockito.times(1)).delete(machineType);
    }
}
