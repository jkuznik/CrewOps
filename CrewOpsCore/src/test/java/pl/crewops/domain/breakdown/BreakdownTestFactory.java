package pl.crewops.domain.breakdown;

import java.util.Set;
import java.util.UUID;
import pl.crewops.auth.RoleDTO;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.enums.VehicleType;
import pl.crewops.model.Breakdown;
import pl.crewops.model.Employee;
import pl.crewops.model.Vehicle;

class BreakdownTestFactory {

    static final UUID breakdownId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID vehicleId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID reportedByEmployeeId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID repairedByEmployeeId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final UUID roleId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    static final VehicleDTO vehicleDTO = getVehicleDTO();
    static final EmployeeDTO reportedByEmployee = getReporetdByEmployeeDTO();
    static final EmployeeDTO repairedByEmployee = getRepairedByEmployeeDTO();
    static final Set<RoleDTO> roles = roleDTOSet();

    static Breakdown getBreakdown() {
        return Breakdown.builder()
                .vehicle(getVehicle())
                .reportedBy(getEmployee())
                .repairedBy(getEmployee())
                .description("description")
                .critical(true)
                .solved(true)
                .solvedAt(null)
                .build();
    }

    static BreakdownDTO getBreakdownDTO() {
        return BreakdownDTO.builder()
                .id(breakdownId)
                .vehicle(vehicleDTO)
                .reportedBy(reportedByEmployee)
                .repairedBy(repairedByEmployee)
                .critical(true)
                .solved(false)
                .solvedAt(null)
                .build();
    }

    static CreateBreakdownDTO createBreakdownDTO() {
        return CreateBreakdownDTO.builder()
                .vehicleId(vehicleId)
                .reportedByEmployeeId(repairedByEmployeeId)
                .critical(true)
                .description("description")
                .build();
    }

    static UpdateBreakdownDTO getUpdateBreakdownDTO() {
        return UpdateBreakdownDTO.builder()
                .breakdownId(breakdownId)
                .repairedByEmployeeId(repairedByEmployeeId)
                .solved(true)
                .build();
    }

    static Vehicle getVehicle() {
        return Vehicle.builder()
                .make("make")
                .model("model")
                .vehicleType(VehicleType.EXCAVATOR)
                .registerNumber("registerNumber")
                .year(2025)
                .vin("vin")
                .broken(false)
                .build();
    }

    static Employee getEmployee() {
        return Employee.builder()
                .firstName("firstName")
                .lastName("lastName")
                .department("department")
                .build();
    }

    private static VehicleDTO getVehicleDTO() {
        return VehicleDTO.builder()
                .id(vehicleId)
                .make("make")
                .model("model")
                .vehicleType(VehicleType.EXCAVATOR)
                .registerNumber("registerNumber")
                .vin("vin")
                .broken(false)
                .build();
    }

    private static EmployeeDTO getReporetdByEmployeeDTO() {
        return EmployeeDTO.builder()
                .id(reportedByEmployeeId)
                .roles(roles)
                .department("department")
                .build();
    }

    private static EmployeeDTO getRepairedByEmployeeDTO() {
        return EmployeeDTO.builder()
                .id(repairedByEmployeeId)
                .roles(roles)
                .department("department")
                .build();
    }

    private static Set<RoleDTO> roleDTOSet() {
        return Set.of(RoleDTO.builder().id(roleId).name("name").build());
    }
}
