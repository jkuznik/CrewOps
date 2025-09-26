package pl.crewops.domain.option;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.compositePK.AUOID;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.publicSchema.Option;

@Validated
public interface OptionAPI {

    Set<AuthUserOptionDTO> getAuthUserOptionsByAuthUserId(@NotNull UUID authUserId);

    Optional<Option> getOptionByName(@NotNull String name);

    int updateAuthUserOptionById(@NotNull AUOID id, @NotNull boolean update);
}
