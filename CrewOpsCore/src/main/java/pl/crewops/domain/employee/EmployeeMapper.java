package pl.crewops.domain.employee;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.Qualification;
import pl.crewops.model.Vehicle;

class EmployeeMapper {

    static Employee mapToEntity(CreateEmployeeDTO createEmployeeDTO) {
        return Employee.builder()
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .birthDate(createEmployeeDTO.birthDate())
                .phoneNumber(createEmployeeDTO.phoneNumber())
                .department(createEmployeeDTO.department())
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
                .qualifications(getQualifications(employee.getQualifications()))
                .vehicles(getVehicles(employee.getVehicles()))
                .build();
    }

    private static Set<UUID> getQualifications(Set<Qualification> qualifications) {
        return qualifications.stream().map(Qualification::getId).collect(Collectors.toSet());
    }

    private static Set<UUID> getVehicles(Set<Vehicle> vehicles) {
        return vehicles.stream().map(Vehicle::getId).collect(Collectors.toSet());
    }
}
