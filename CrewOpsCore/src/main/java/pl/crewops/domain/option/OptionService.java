package pl.crewops.domain.option;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.model.compositePK.AUOID;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.dto.option.OptionDTO;
import pl.crewops.model.joinTable.AuthUserOption;
import pl.crewops.model.publicSchema.Option;

@Log4j2
@Service
@RequiredArgsConstructor
class OptionService implements OptionAPI {

    private final OptionRepository optionRepository;
    private final AuthUserOptionRepository authUserOptionRepository;

    @Override
    @Transactional
    public Set<AuthUserOptionDTO> getAuthUserOptionsByAuthUserId(UUID authUserId) {

        return authUserOptionRepository.findAllByAuthUserId(authUserId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Optional<Option> getOptionByName(String name) {
        return optionRepository.findByName(name);
    }

    @Override
    @Transactional
    public int updateAuthUserOptionById(AUOID id, boolean update) {
        return authUserOptionRepository.updateEnableById(id.getAuthUserId(), id.getOptionId(), update);
    }

    private OptionDTO mapToDTO(Option option) {
        return OptionDTO.builder().id(option.getId()).name(option.getName()).build();
    }

    private AuthUserOptionDTO mapToDTO(AuthUserOption authUserOption) {
        return AuthUserOptionDTO.builder()
                .optionId(authUserOption.getOption().getId())
                .employeeId(authUserOption.getAuthUser().getEmployeeId())
                .name(authUserOption.getOption().getName())
                .enabled(authUserOption.isEnable())
                .build();
    }
}
