package pl.crewops.domain.breakdown;

import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.model.Breakdown;

class BreakdownMapper {

    public static BreakdownDTO toDTO(Breakdown breakdown) {
        var vehicle = breakdown.getVehicle();
        var reportedBy = breakdown.getReportedBy();
        var repairedBy = breakdown.getRepairedBy();

        return BreakdownDTO.builder()
                .id(breakdown.getId())
                .description(breakdown.getDescription())
                .vehicle(VehicleDTO.builder()
                        .id(vehicle.getId())
                        .make(vehicle.getMake())
                        .model(vehicle.getModel())
                        .vehicleType(VehicleTypeDTO.builder()
                                .id(vehicle.getVehicleType().getId())
                                .name(vehicle.getVehicleType().getName())
                                .build())
                        .year(vehicle.getYear())
                        .vin(vehicle.getVin())
                        .registerNumber(vehicle.getRegisterNumber())
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
