package pl.crewops.domain.auth;

import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.IntegrationTest;
import pl.crewops.model.auth.AuthUser;

@Transactional
public class AuthAPITest extends IntegrationTest {

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
        Assertions.assertThat(result.isPresent()).isFalse();
    }
}
