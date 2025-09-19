package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.registration.CreateRegistrationDTO;
import pl.crewops.model.dto.registration.RegistrationDTO;

@Validated
public interface RegistrationAPI {

    RegistrationDTO getRegistrationById(@NotNull UUID id);

    RegistrationDTO saveRegistration(@NotNull @Valid CreateRegistrationDTO createRegistrationDTO);
}
