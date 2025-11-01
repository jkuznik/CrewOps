package pl.crewops.domain.dailyEntry;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyEntryAudit;

interface DailyEntryRepository extends JpaRepository<DailyEntry, UUID> {

    /**
     * Znajduje DailyEntry dla danego identyfikatora pracownika i daty wpisu.
     * Używa konwencji nazewnictwa FindBy{Pole1}And{Pole2}.
     *
     * @param employeeId Identyfikator pracownika (UUID)
     * @param entryDate Data wpisu (LocalDate)
     * @return Optional zawierający DailyEntry lub pusty Optional
     */
    Optional<DailyEntry> findByEmployeeIdAndEntryDate(UUID employeeId, LocalDate entryDate);
}

interface DailyEntryAuditRepository extends JpaRepository<DailyEntryAudit, UUID> {}
