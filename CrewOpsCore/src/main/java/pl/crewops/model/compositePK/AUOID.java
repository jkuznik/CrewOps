package pl.crewops.model.compositePK;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class AUOID implements Serializable {
    @Serial
    private static final long serialVersionUID = 1758502992767958280L;

    @NotNull
    @Column(name = "auth_user_id", nullable = false)
    private UUID authUserId;

    @NotNull
    @Column(name = "optionId", nullable = false)
    private UUID optionId;

    public AUOID(UUID authUserId, UUID optionId) {
        this.authUserId = authUserId;
        this.optionId = optionId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AUOID auoid = (AUOID) o;
        return getAuthUserId() != null
                && Objects.equals(getAuthUserId(), auoid.getAuthUserId())
                && getOptionId() != null
                && Objects.equals(getOptionId(), auoid.getOptionId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(authUserId, optionId);
    }
}
