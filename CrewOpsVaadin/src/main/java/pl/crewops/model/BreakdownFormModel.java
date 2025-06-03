package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.vehicle.VehicleDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakdownFormModel {
    private UUID id;

    @NotNull
    private VehicleDTO vehicle;

    @NotNull
    @Size(min = 5, max = 2047, message = "Description have to match between 5 and 2047 characters length")
    private String description;

    private EmployeeDTO reportedBy;
    private EmployeeDTO repairedBy;
    private boolean critical;
    private boolean solved;
    private Instant solvedAt;

    public static BreakdownFormModel toBreakdownFormModel(BreakdownDTO breakdown) {
        return BreakdownFormModel.builder()
                .id(breakdown.id())
                .description(breakdown.description())
                .vehicle(breakdown.vehicle())
                .reportedBy(breakdown.reportedBy())
                .repairedBy(breakdown.repairedBy())
                .critical(breakdown.critical())
                .solved(breakdown.solved())
                .solvedAt(breakdown.solvedAt())
                .build();
    }

    public static CreateBreakdownDTO toCreateBreakdownDTO(BreakdownFormModel breakdownFormModel) {
        return CreateBreakdownDTO.builder()
                .description(breakdownFormModel.getDescription())
                .vehicleId(breakdownFormModel.getVehicle().id())
                .reportedByEmployeeId(breakdownFormModel.getReportedBy().id())
                .critical(breakdownFormModel.isCritical())
                .build();
    }
}
