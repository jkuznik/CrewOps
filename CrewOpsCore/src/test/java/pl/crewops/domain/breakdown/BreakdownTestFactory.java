package pl.crewops.domain.breakdown;

import java.util.Set;
import java.util.UUID;
import pl.crewops.model.*;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;

class BreakdownTestFactory {

    static final UUID breakdownId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID machineId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID repairedByEmployeeId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    static Breakdown breakdown() {
        return Breakdown.builder()
                .machine(machine())
                .reportedBy(employee())
                .repairedBy(employee())
                .description("description")
                .critical(true)
                .solved(true)
                .solvedAt(null)
                .build();
    }

    static CreateBreakdownDTO createBreakdownDTO() {
        return CreateBreakdownDTO.builder()
                .machineId(machineId)
                .reportedByEmployeeId(repairedByEmployeeId)
                .critical(true)
                .description("description")
                .build();
    }

    static UpdateBreakdownDTO updateBreakdownDTO() {
        return UpdateBreakdownDTO.builder()
                .breakdownId(breakdownId)
                .repairedByEmployeeId(repairedByEmployeeId)
                .solved(true)
                .build();
    }

    static Machine machine() {
        return Machine.builder()
                .make("make")
                .model("model")
                .machineType(MachineType.builder().name("ImplementThis").build())
                .registerNumber("registerNumber")
                .year(2025)
                .vin("vin")
                .broken(false)
                .build();
    }

    static Employee employee() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .departments(departments())
                .build();
    }

    private static Set<RoleDTO> roleDTOSet() {
        return Set.of(RoleDTO.builder().name("name").build());
    }

    static Set<Department> departments() {
        return Set.of(Department.builder().name("departments").build());
    }
}
