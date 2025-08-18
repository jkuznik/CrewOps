package pl.crewops.domain.message;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.Message;

interface MessageRepository extends JpaRepository<Message, UUID> {}
