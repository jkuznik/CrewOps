package pl.crewops.domain.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void createAuthUser() {
        System.out.println(passwordEncoder.encode("a"));
    }
}
