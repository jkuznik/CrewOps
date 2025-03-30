package pl.kuznik.employee;

import pl.kuznik.employee.dto.CreateEmployeeDTO;
import pl.kuznik.entity.Employee;

class EmployeeMapper {

    static Employee map(CreateEmployeeDTO createEmployeeDTO) {
        return Employee.builder()
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .phoneNumber(createEmployeeDTO.phoneNumer())
                .department(createEmployeeDTO.department())
                .qualifications(createEmployeeDTO.qualifications())
                .vehicles(createEmployeeDTO.vehicles())
                .build();
    }
}
