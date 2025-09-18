package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.dto.registration.CreateRegistrationDTO;
import pl.crewops.model.dto.registration.RegistrationDTO;

@Validated
public interface RegistrationAPI {

    RegistrationDTO saveRegistration(@NotNull @Valid CreateRegistrationDTO createRegistrationDTO);
}
