package pl.crewops.domain.note;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.enums.NoteType;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Note;

interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findAllByReportedByEmployeeIdAndDate(Employee reportedByEmployeeId, LocalDate date);

    List<Note> findAllByDateAndType(LocalDate date, NoteType noteType);
}
