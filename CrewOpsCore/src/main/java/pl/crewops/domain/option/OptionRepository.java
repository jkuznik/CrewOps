package pl.crewops.domain.option;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.crewops.model.compositePK.AUOID;
import pl.crewops.model.joinTable.AuthUserOption;
import pl.crewops.model.publicSchema.Option;

@Repository
interface OptionRepository extends JpaRepository<Option, UUID> {

    Optional<Option> findByName(String name);
}

@Repository
interface AuthUserOptionRepository extends JpaRepository<AuthUserOption, AUOID> {

    @Modifying
    @Query(
            """
    UPDATE AuthUserOption auo
    SET auo.enable = :enable
    WHERE auo.id.authUserId = :authUserId
      AND auo.id.optionId = :optionId
""")
    int updateEnableById(
            @Param("authUserId") UUID authUserId, @Param("optionId") UUID optionId, @Param("enable") boolean enable);

    Set<AuthUserOption> findAllByAuthUserId(UUID authUserId);
}
