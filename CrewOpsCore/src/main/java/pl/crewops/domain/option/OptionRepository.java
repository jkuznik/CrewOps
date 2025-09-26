package pl.crewops.domain.option;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.joinTable.AuthUserOption;
import pl.crewops.model.publicSchema.Option;

interface OptionRepository extends JpaRepository<Option, UUID> {}

interface AuthUserOptionRepository extends JpaRepository<AuthUserOption, UUID> {}
