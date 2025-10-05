package pl.crewops.model.tenantSchema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.crewops.model.AbstractEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address extends AbstractEntity {

    @Size(max = 31)
    @Column(nullable = false)
    private String postalCode;

    @Size(max = 31)
    @Column(nullable = false)
    private String city;

    @Size(max = 31)
    @Column(nullable = false)
    private String street;

    @Size(max = 31)
    @Column(nullable = false)
    private String localNumber;
}
