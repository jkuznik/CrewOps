package pl.crewops.domain.breakdown;

import pl.crewops.model.Breakdown;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;

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
