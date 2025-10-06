package pl.crewops.model.publicSchema;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.crewops.enums.RegistrationStatus;
import pl.crewops.model.AbstractEntity;

@Entity
@Table(name = "registration", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration extends AbstractEntity {

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "registration_status", nullable = false)
    private RegistrationStatus status;

    @Column(nullable = false)
    private int verificationCode;

    // company info
    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String taxId;

    @Column(nullable = false)
    private String email;

    // company address info
    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String localNumber;

    // employee info
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Instant birthDate;

    private String phoneNumber;
}
