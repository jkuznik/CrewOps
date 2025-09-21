package pl.crewops.model;

import java.time.LocalDate;
import lombok.*;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.security.custom.UserPrincipal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileFormModel {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private String username;
    private String password;

    public static ProfileFormModel create(UserPrincipal principal, EmployeeDTO employeeDTO) {
        return ProfileFormModel.builder()
                .firstName(employeeDTO.firstName())
                .lastName(employeeDTO.lastName())
                .birthDate(employeeDTO.birthDate())
                .phoneNumber(employeeDTO.phoneNumber())
                .username(principal.getUsername())
                .build();
    }
}
