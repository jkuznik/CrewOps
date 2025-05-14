package pl.crewops.dto.breakdown;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.vehicle.VehicleDTO;

@Builder
public record BreakdownDTO(
        UUID id,
        String description,
        VehicleDTO vehicle,
        EmployeeDTO reportedBy,
        EmployeeDTO repairedBy,
        boolean critical,
        boolean solved,
        Instant solvedAt) {}
