package pl.crewops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.*;

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
