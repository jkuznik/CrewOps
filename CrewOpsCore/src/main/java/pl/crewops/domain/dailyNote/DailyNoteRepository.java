package pl.crewops.domain.dailyNote;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.DailyNote;

interface DailyNoteRepository extends JpaRepository<DailyNote, UUID> {

    List<DailyNote> findAllByDailyEntryId(UUID dailyEntryId);
}
