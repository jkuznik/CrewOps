package pl.crewops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

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

    @Id
    @Column(name = "id", nullable = false)
    protected UUID id;

    @Version
    private Integer version;

    @Column(insertable = false, updatable = false)
    private Instant createdAt;

    @Column(insertable = false, updatable = false)
    private Instant updatedAt;
}
