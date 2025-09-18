package pl.crewops.domain.employee;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.model.Employee;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeQualificationDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
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
                .active(true)
                .build();
    }

    static EmployeeDTO mapToDTO(Employee employee, boolean create) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .departments(getDepartments(employee))
                .active(employee.isActive())
                .qualifications(getQualifications(employee))
                .machines(getMachines(employee))
                .build();
    }

    static EmployeeDTO mapToDTO(Employee employee) {
        Set<RoleDTO> roles = new HashSet<>();

        AuthAPI authAPI = SpringContextBridge.getBean(AuthAPI.class);
        Optional<AuthUser> byEmployeeId = authAPI.getByEmployeeId(employee.getId());

        if (byEmployeeId.isPresent()) {
            roles = byEmployeeId.get().getRoles().stream()
                    .map(role -> RoleDTO.builder().name(role.getName()).build())
                    .collect(Collectors.toSet());
        }

        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .departments(getDepartments(employee))
                .active(employee.isActive())
                .qualifications(getQualifications(employee))
                .machines(getMachines(employee))
                .roles(roles)
                .build();
    }

    static EmployeeQualificationDTO mapToEQDTO(EmployeeQualification employeeQualification) {
        return EmployeeQualificationDTO.builder()
                .employeeId(employeeQualification.getId().getEmployeeId())
                .qualificationId(employeeQualification.getId().getQualificationId())
                .expiredAt(LocalDate.ofInstant(employeeQualification.getExpiredAt(), ZoneId.systemDefault()))
                .build();
    }

    private static Set<DepartmentDTO> getDepartments(Employee employee) {
        return employee.getDepartments().stream()
                .map(department -> DepartmentDTO.builder()
                        .id(department.getId())
                        .name(department.getName())
                        .build())
                .collect(Collectors.toSet());
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
