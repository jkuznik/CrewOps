package pl.crewops.domain.employee;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.EmployeeQualificationDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.exception.domain.employee.EmployeeNotFoundException;
import pl.crewops.model.Employee;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.util.spring.SpringContextBridge;

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
        AuthAPI authAPI = SpringContextBridge.getBean(AuthAPI.class);
        AuthUser authUser = authAPI.getByEmployeeId(employee.getId())
                .orElseThrow(() -> new EmployeeNotFoundException(employee.getId()));

        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .active(employee.isActive())
                .qualifications(getQualifications(employee))
                .machines(getMachines(employee))
                .roles(authUser.getRoles().stream()
                        .map(role -> RoleDTO.builder().name(role.getName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }

    static EmployeeQualificationDTO mapToEMDTO(EmployeeQualification employeeQualification) {
        return EmployeeQualificationDTO.builder()
                .employeeId(employeeQualification.getId().getEmployeeId())
                .qualificationId(employeeQualification.getId().getQualificationId())
                .expiredAt(LocalDate.from(employeeQualification.getExpiredAt()))
                .build();
    }

    private static Set<MachineDTO> getMachines(Employee employee) {
        return employee.getMachines().stream()
                .map(machine -> MachineDTO.builder()
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
                        .broken(machine.getBroken())
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
