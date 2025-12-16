package pl.crewops.domain.employee;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.mapstruct.*;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.department.DepartmentMapper;
import pl.crewops.domain.machine.MachineMapper;
import pl.crewops.domain.qualification.QualificationMapper;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeQualificationDTO;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.util.spring.SpringContextBridge;

@Mapper(
        componentModel = "spring",
        uses = {DepartmentMapper.class, MachineMapper.class, QualificationMapper.class},
        imports = {LocalDate.class, ZoneId.class})
public interface EmployeeMapper {

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "machines", ignore = true)
    @Mapping(target = "qualifications", ignore = true)
    Employee toEntity(CreateEmployeeDTO dto);

    @Mapping(target = "roles", ignore = true)
    EmployeeDTO toDTO(Employee employee);

    @AfterMapping
    default void fillRoles(Employee employee, @MappingTarget EmployeeDTO.EmployeeDTOBuilder dto) {

        AuthAPI authAPI = SpringContextBridge.getBean(AuthAPI.class);
        Optional<AuthUser> authUser = authAPI.getByEmployeeId(employee.getId());

        Set<RoleDTO> roles = new HashSet<>();

        authUser.ifPresent(user -> user.getRoles()
                .forEach(
                        role -> roles.add(RoleDTO.builder().name(role.getName()).build())));

        dto.roles(roles);
    }

    @Mapping(target = "employeeId", expression = "java(eq.getId().getEmployeeId())")
    @Mapping(target = "qualificationId", expression = "java(eq.getId().getQualificationId())")
    @Mapping(target = "expiredAt", expression = "java(LocalDate.ofInstant(eq.getExpiredAt(), ZoneId.systemDefault()))")
    EmployeeQualificationDTO toDTO(EmployeeQualification eq);
}
