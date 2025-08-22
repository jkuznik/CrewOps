package pl.crewops.domain.auth;

import java.util.Set;
import pl.crewops.dto.auth.CreateAuthUserDTO;
import pl.crewops.dto.auth.RoleDTO;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;
import pl.crewops.model.Qualification;
import pl.crewops.model.auth.RoleType;

class AuthTestFactory {

    public static CreateAuthUserDTO createAuthUserDTO() {
        return CreateAuthUserDTO.builder()
                .username("username")
                .password("password")
                .roles(Set.of(RoleDTO.builder().name(RoleType.MANAGER.name()).build()))
                .build();
    }

    public static Qualification qualification() {
        return Qualification.builder().description("description").build();
    }

    public static Machine machine() {
        return Machine.builder()
                .machineType(MachineType.builder().name("ImplementThis").build())
                .make("make")
                .model("model")
                .year(2020)
                .broken(false)
                .build();
    }
}
