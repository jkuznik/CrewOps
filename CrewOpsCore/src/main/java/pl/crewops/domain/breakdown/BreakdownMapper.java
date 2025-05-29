package pl.crewops.domain.breakdown;

import java.util.stream.Collectors;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
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
                // TODO: implement rest, refactor mappers and move all mapped methods to entity classes
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
                        .department(reportedBy.getDepartment())
                        .birthDate(reportedBy.getBirthDate())
                        .phoneNumber(reportedBy.getPhoneNumber())
                        .qualifications(reportedBy.getQualifications().stream()
                                .map(qualification -> QualificationDTO.builder()
                                        .id(qualification.getId())
                                        .description(qualification.getDescription())
                                        .build())
                                .collect(Collectors.toSet()))
                        .vehicles(reportedBy.getVehicles().stream()
                                .map(v -> VehicleDTO.builder()
                                        .id(v.getId())
                                        .make(v.getMake())
                                        .model(v.getModel())
                                        .vehicleType(VehicleTypeDTO.builder()
                                                .id(v.getVehicleType().getId())
                                                .name(v.getVehicleType().getName())
                                                .build())
                                        .year(v.getYear())
                                        .vin(v.getVin())
                                        .registerNumber(v.getRegisterNumber())
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
                .repairedBy(
                        breakdown.getRepairedBy() != null
                                ? EmployeeDTO.builder()
                                        .id(repairedBy.getId())
                                        .firstName(repairedBy.getFirstName())
                                        .lastName(repairedBy.getLastName())
                                        .department(repairedBy.getDepartment())
                                        .birthDate(repairedBy.getBirthDate())
                                        .phoneNumber(repairedBy.getPhoneNumber())
                                        .qualifications(repairedBy.getQualifications().stream()
                                                .map(qualification -> QualificationDTO.builder()
                                                        .id(qualification.getId())
                                                        .description(qualification.getDescription())
                                                        .build())
                                                .collect(Collectors.toSet()))
                                        .vehicles(repairedBy.getVehicles().stream()
                                                .map(v -> VehicleDTO.builder()
                                                        .id(v.getId())
                                                        .make(v.getMake())
                                                        .model(v.getModel())
                                                        .vehicleType(VehicleTypeDTO.builder()
                                                                .id(v.getVehicleType()
                                                                        .getId())
                                                                .name(v.getVehicleType()
                                                                        .getName())
                                                                .build())
                                                        .year(v.getYear())
                                                        .vin(v.getVin())
                                                        .registerNumber(v.getRegisterNumber())
                                                        .build())
                                                .collect(Collectors.toSet()))
                                        .build()
                                : null)
                .critical(breakdown.isCritical())
                .solved(breakdown.isSolved())
                .solvedAt(breakdown.getSolvedAt())
                .build();
    }
}
