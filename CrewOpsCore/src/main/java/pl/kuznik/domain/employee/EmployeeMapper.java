package pl.kuznik.domain.employee;

import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
import pl.kuznik.domain.employee.dto.EmployeeDTO;
import pl.kuznik.entity.Employee;

class EmployeeMapper {

    static Employee mapToEntity(CreateEmployeeDTO createEmployeeDTO) {
        return Employee.builder()
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .birthDate(createEmployeeDTO.birthDate())
                .phoneNumber(createEmployeeDTO.phoneNumber())
                .department(createEmployeeDTO.department())
                .qualifications(createEmployeeDTO.qualifications())
                .vehicles(createEmployeeDTO.vehicles())
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
                .qualifications(employee.getQualifications())
                .vehicles(employee.getVehicles())
                .build();
    }
}
