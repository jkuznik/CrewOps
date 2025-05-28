package pl.crewops.domain.employee;

import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.model.Employee;

class EmployeeMapper {

    static Employee mapToEntity(CreateEmployeeDTO createEmployeeDTO) {
        return Employee.builder()
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .birthDate(createEmployeeDTO.birthDate())
                .phoneNumber(createEmployeeDTO.phoneNumber())
                .department(createEmployeeDTO.department())
                .active(true)
                .build();
    }

    static EmployeeDTO mapToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .qualifications(getQualifications(employee))
                .vehicles(getVehicles(employee))
                .build();
    }

    private static Set<VehicleDTO> getVehicles(Employee employee) {
        return employee.getVehicles().stream()
                .map(vehicle -> VehicleDTO.builder()
                        .id(vehicle.getId())
                        .make(vehicle.getMake())
                        .model(vehicle.getModel())
                        .vehicleType(vehicle.getVehicleType().toDTO())
                        .year(vehicle.getYear())
                        .vin(vehicle.getVin())
                        .registerNumber(vehicle.getRegisterNumber())
                        .broken(vehicle.getBroken())
                        .build())
                .collect(Collectors.toSet());
    }

    private static Set<QualificationDTO> getQualifications(Employee employee) {
        return employee.getQualifications().stream()
                .map(role -> QualificationDTO.builder()
                        .id(role.getId())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toSet());
    }
}
