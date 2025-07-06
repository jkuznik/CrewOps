package pl.crewops.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.publicSchema.AuthUser;

@Transactional
public class AuthAPITest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AuthAPITest.class);

    @Autowired
    private AuthAPI authAPI;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Test
    void deleteById_shouldDeleteAuthUser_whenAuthUserExists() {
        // given
        var authUser = AuthUser.builder().username("admin").password("admin").build();
        authUser.setId(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));

        // when
        authAPI.deleteById(authUser.getId());

        Optional<AuthUser> result = authUserRepository.findById(authUser.getId());

        // then
        result.ifPresent(resultValue -> log.warn("AuthUser with id {} still exists", resultValue.getId()));
        assertThat(result.isPresent()).isFalse();
    }
}
