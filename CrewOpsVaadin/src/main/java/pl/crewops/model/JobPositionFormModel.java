package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPositionFormModel {
    private UUID id;

    @NotNull
    @Size(max = 255)
    private String name;

    private MachineDTO machine;

    private Set<QualificationDTO> qualifications;

    public static JobPositionFormModel toFormModel(JobPositionDTO jobPositionDTO) {
        return JobPositionFormModel.builder()
                .id(jobPositionDTO.id())
                .name(jobPositionDTO.name())
                .machine(jobPositionDTO.machine())
                .qualifications(jobPositionDTO.qualifications())
                .build();
    }

    public static CreateBreakdownDTO toCreateBreakdownDTO(pl.crewops.model.BreakdownFormModel breakdownFormModel) {
        return CreateBreakdownDTO.builder()
                .description(breakdownFormModel.getDescription())
                .machineId(breakdownFormModel.getMachine().id())
                .reportedByEmployeeId(breakdownFormModel.getReportedBy().id())
                .critical(breakdownFormModel.isCritical())
                .build();
    }
}
