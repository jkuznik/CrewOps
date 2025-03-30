package pl.kuznik.domain.employee;

import pl.kuznik.domain.employee.dto.CreateEmployeeDTO;
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
}
