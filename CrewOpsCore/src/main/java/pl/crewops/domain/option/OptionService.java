package pl.crewops.domain.option;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.exception.domain.auth.AuthUserNotFoundException;
import pl.crewops.model.dto.option.OptionDTO;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.publicSchema.Option;

@Log4j2
@Service
@RequiredArgsConstructor
class OptionService implements OptionAPI {

    private final OptionRepository optionRepository;
    private final AuthAPI authAPI;

    @Override
    public Set<OptionDTO> getOptionsByEmployeeId(UUID employeeId) {
        AuthUser authUser =
                authAPI.getByEmployeeId(employeeId).orElseThrow(() -> new AuthUserNotFoundException(employeeId));

        return authUser.getOptions().stream().map(this::mapToDTO).collect(Collectors.toSet());
    }

    private OptionDTO mapToDTO(Option option) {
        return OptionDTO.builder().id(option.getId()).name(option.getName()).build();
    }
}
