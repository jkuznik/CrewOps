package pl.crewops.domain.breakdown;

import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.model.Breakdown;

class BreakdownMapper {

    public static BreakdownDTO toDTO(Breakdown breakdown) {
        var machine = breakdown.getMachine();
        var reportedBy = breakdown.getReportedBy();
        var repairedBy = breakdown.getRepairedBy();

        return BreakdownDTO.builder()
                .id(breakdown.getId())
                .description(breakdown.getDescription())
                .machine(MachineDTO.builder()
                        .id(machine.getId())
                        .make(machine.getMake())
                        .model(machine.getModel())
                        .machineType(MachineTypeDTO.builder()
                                .id(machine.getMachineType().getId())
                                .name(machine.getMachineType().getName())
                                .build())
                        .year(machine.getYear())
                        .vin(machine.getVin())
                        .registerNumber(machine.getRegisterNumber())
                        .build())
                .reportedBy(EmployeeDTO.builder()
                        .id(reportedBy.getId())
                        .firstName(reportedBy.getFirstName())
                        .lastName(reportedBy.getLastName())
                        .build())
                .repairedBy(
                        breakdown.getRepairedBy() != null
                                ? EmployeeDTO.builder()
                                        .id(repairedBy.getId())
                                        .firstName(repairedBy.getFirstName())
                                        .lastName(repairedBy.getLastName())
                                        .build()
                                : null)
                .critical(breakdown.isCritical())
                .solved(breakdown.isSolved())
                .solvedAt(breakdown.getSolvedAt())
                .build();
    }
}
