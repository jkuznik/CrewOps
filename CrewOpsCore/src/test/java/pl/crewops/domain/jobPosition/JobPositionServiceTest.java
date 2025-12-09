package pl.crewops.domain.jobPosition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.machine.MachineAPI;
import pl.crewops.domain.qualification.QualificationAPI;
import pl.crewops.exception.domain.jobPosition.JobPositionNotFoundException;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.tenantSchema.JobPosition;
import pl.crewops.model.tenantSchema.Machine;
import pl.crewops.model.tenantSchema.Qualification;

@SpringJUnitConfig(classes = {JobPositionService.class})
class JobPositionServiceTest {

    @Autowired
    JobPositionService jobPositionService;

    @MockitoBean
    JobPositionRepository jobPositionRepository;

    @MockitoBean
    MachineAPI machineAPI;

    @MockitoBean
    QualificationAPI qualificationAPI;

    @MockitoBean
    JobPositionMapper jobPositionMapper;

    private JobPosition jobPosition;
    private JobPosition jobPositionWithMachineAndQualifications;
    private Machine machine;
    private Qualification qualification;
    private CreateJobPositionDTO createDTO;
    private UpdateJobPositionDTO updateDTO;
    private final UUID jobPositionId = UUID.randomUUID();
    private final UUID machineId = UUID.randomUUID();
    private final UUID qualificationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        machine = Machine.builder().make("TestMake").model("TestModel").build();
        machine.setId(machineId);

        qualification =
                Qualification.builder().description("Test Qualification").build();
        qualification.setId(qualificationId);

        jobPosition = JobPosition.builder().name("Test JobPosition").build();
        jobPosition.setId(jobPositionId);

        jobPositionWithMachineAndQualifications = JobPosition.builder()
                .name("JobPosition With Machine & Qualification")
                .machine(machine)
                .qualifications(Set.of(qualification))
                .build();
        jobPositionWithMachineAndQualifications.setId(jobPositionId);

        createDTO = CreateJobPositionDTO.builder()
                .name("New JobPosition")
                .machineDTO(MachineDTO.builder().id(machineId).build())
                .qualificationDTOS(
                        Set.of(QualificationDTO.builder().id(qualificationId).build()))
                .build();

        updateDTO = UpdateJobPositionDTO.builder()
                .id(jobPositionId)
                .name("Updated JobPosition")
                .machineDTO(MachineDTO.builder().id(machineId).build())
                .qualifications(
                        Set.of(QualificationDTO.builder().id(qualificationId).build()))
                .build();

        when(jobPositionMapper.toEntity(any(CreateJobPositionDTO.class))).thenAnswer(invocation -> {
            CreateJobPositionDTO dto = invocation.getArgument(0);
            return JobPosition.builder().name(dto.name()).build();
        });

        when(jobPositionMapper.toDTO(any(JobPosition.class))).thenAnswer(invocation -> {
            JobPosition entity = invocation.getArgument(0);
            return JobPositionDTO.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .machine(
                            entity.getMachine() != null
                                    ? MachineDTO.builder()
                                            .id(entity.getMachine().getId())
                                            .build()
                                    : null)
                    .qualifications(
                            entity.getQualifications() != null
                                    ? entity.getQualifications().stream()
                                            .map(q -> QualificationDTO.builder()
                                                    .id(q.getId())
                                                    .build())
                                            .collect(Collectors.toSet())
                                    : null)
                    .build();
        });
    }

    @Test
    void createJobPosition_ShouldReturnDTO_WithMachineAndQualifications() {
        when(jobPositionRepository.save(any(JobPosition.class))).thenReturn(jobPositionWithMachineAndQualifications);

        when(machineAPI.getMachine(machineId)).thenReturn(machine);
        when(qualificationAPI.getQualification(qualificationId)).thenReturn(qualification);

        JobPositionDTO result = jobPositionService.createJobPosition(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(jobPositionWithMachineAndQualifications.getName());
        assertThat(result.machine().id()).isEqualTo(machineId);
        assertThat(result.qualifications()).hasSize(1);
        verify(jobPositionRepository, times(2))
                .save(any(JobPosition.class)); // save przed i po ustawieniu maszyn/kwalifikacji
    }

    @Test
    void findById_ShouldReturnOptional_WhenExists() {
        when(jobPositionRepository.findById(jobPositionId)).thenReturn(Optional.of(jobPosition));

        Optional<JobPosition> result = jobPositionService.findById(jobPositionId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(jobPositionId);
    }

    @Test
    void findByName_ShouldReturnOptional_WhenExists() {
        when(jobPositionRepository.findByName(jobPosition.getName())).thenReturn(Optional.of(jobPosition));

        Optional<JobPosition> result = jobPositionService.findByName(jobPosition.getName());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(jobPosition.getName());
    }

    @Test
    void getAllJobPositions_ShouldReturnDTOs() {
        List<JobPosition> positions = List.of(jobPosition, jobPositionWithMachineAndQualifications);
        when(jobPositionRepository.findAll()).thenReturn(positions);

        List<JobPositionDTO> result = jobPositionService.getAllJobPositions();

        assertThat(result).hasSize(2);
        verify(jobPositionMapper, times(2)).toDTO(any(JobPosition.class));
    }

    @Test
    void updateJobPosition_ShouldReturnDTO_WhenExists() {
        when(jobPositionRepository.findById(jobPositionId)).thenReturn(Optional.of(jobPosition));
        when(jobPositionRepository.save(any(JobPosition.class))).thenReturn(jobPositionWithMachineAndQualifications);
        when(machineAPI.getMachine(machineId)).thenReturn(machine);
        when(qualificationAPI.getQualification(qualificationId)).thenReturn(qualification);

        JobPositionDTO result = jobPositionService.updateJobPosition(updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(jobPositionWithMachineAndQualifications.getName());
        assertThat(result.machine().id()).isEqualTo(machineId);
        assertThat(result.qualifications()).hasSize(1);
        verify(jobPositionRepository).save(any(JobPosition.class));
    }

    @Test
    void updateJobPosition_ShouldThrowException_WhenNotFound() {
        when(jobPositionRepository.findById(jobPositionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobPositionService.updateJobPosition(updateDTO))
                .isInstanceOf(JobPositionNotFoundException.class)
                .hasMessageContaining(jobPositionId.toString());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(jobPositionRepository).deleteById(jobPositionId);

        jobPositionService.deleteById(jobPositionId);

        verify(jobPositionRepository).deleteById(jobPositionId);
    }
}
