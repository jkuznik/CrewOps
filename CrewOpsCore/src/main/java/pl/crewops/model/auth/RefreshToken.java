package pl.crewops.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import pl.crewops.model.AbstractEntity;

@Entity
@Getter
@Setter
public class RefreshToken extends AbstractEntity {

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiresAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
