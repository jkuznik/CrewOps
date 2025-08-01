package pl.crewops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import pl.crewops.enums.CompanyStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company {

    @Size(max = 63)
    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status;

    // the reason why this entity not extends AbstractEntity is caused by different id column configuration
    @Id
    @Column(name = "id", nullable = false)
    protected UUID id;

    @Version
    private Integer version;

    @Column(insertable = false, updatable = false)
    private Instant createdAt;

    @Column(insertable = false, updatable = false)
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Company company)) return false;
        return Objects.equals(getId(), company.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
