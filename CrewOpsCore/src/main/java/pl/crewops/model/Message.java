package pl.crewops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message extends AbstractEntity {

    @Size(max = 255)
    private String title;

    @Size(min = 2, max = 32767)
    @NotNull
    private String description;

    @NotNull
    private UUID recipientEmployeeId;

    private UUID senderEmployeeId;

    @Column(name = "is_read")
    private boolean read;
}
