package pl.crewops.domain.note;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.enums.NoteType;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.tenantSchema.Employee;

@Slf4j
@Service
@RequiredArgsConstructor
class NoteService implements NoteAPI {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final EmployeeAPI employeeAPI;

    @Override
    @Transactional
    public NoteDTO createDailyNote(CreateNoteDTO createNoteDTO) {

        Employee employeeById = employeeAPI.getEmployeeById(createNoteDTO.reportedByEmployeeId());

        var newDailyNote = noteMapper.toEntity(createNoteDTO, employeeById);

        return noteMapper.toDTO(noteRepository.save(newDailyNote));
    }

    // todo: simplify this solution (3 query should be 1)

    @Override
    @Transactional
    public List<NoteDTO> getPublicAndPrincipalPrivateNotesByDate(@NotNull FetchNotesRequest fetchNotesRequest) {

        Employee employeeById = employeeAPI.getEmployeeById(fetchNotesRequest.employeeId());

        List<NoteDTO> privateNotes =
                noteRepository.findAllByReportedByEmployeeIdAndDate(employeeById, fetchNotesRequest.date()).stream()
                        .map(noteMapper::toDTO)
                        .toList();

        List<NoteDTO> publicNotes =
                noteRepository.findAllByDateAndType(fetchNotesRequest.date(), NoteType.PUBLIC).stream()
                        .map(noteMapper::toDTO)
                        .toList();

        // 💡 KLUCZOWA ZMIANA: Połączenie list, usunięcie duplikatów i sortowanie

        Comparator<NoteDTO> noteDTOComparator = Comparator.comparing(NoteDTO::createdAt)
                .reversed(); // Pamiętaj o sortowaniu malejącym, aby najnowsze były na górze

        return Stream.of(privateNotes, publicNotes)
                .flatMap(Collection::stream)
                .distinct()
                .sorted(noteDTOComparator)
                .collect(Collectors.toList());
    }
}
