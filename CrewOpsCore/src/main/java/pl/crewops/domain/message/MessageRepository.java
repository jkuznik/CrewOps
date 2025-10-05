package pl.crewops.domain.message;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.Message;

interface MessageRepository extends JpaRepository<Message, UUID> {

    Set<Message> findAllByRecipientEmployeeIdAndReadIsTrue(@NotNull UUID recipientEmployeeId);

    Page<Message> findAllByRecipientEmployeeIdAndReadIsFalse(@NotNull UUID recipientEmployeeId, Pageable pageable);

    Page<Message> findAllByRecipientEmployeeId(@NotNull UUID recipientEmployeeId, Pageable pageable);
}
