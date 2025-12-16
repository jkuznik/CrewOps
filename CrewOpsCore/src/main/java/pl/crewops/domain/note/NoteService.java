package pl.crewops.domain.note;

import jakarta.persistence.EntityManager;
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
import pl.crewops.model.tenantSchema.Note;

@Slf4j
@Service
@RequiredArgsConstructor
class NoteService implements NoteAPI {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final EmployeeAPI employeeAPI;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public NoteDTO createDailyNote(CreateNoteDTO createNoteDTO) {

        var employeeById = employeeAPI.getEmployeeById(createNoteDTO.reportedByEmployeeId());

        var newDailyNote = noteMapper.toEntity(createNoteDTO, employeeById);

        Note saved = noteRepository.save(newDailyNote);

        entityManager.flush();
        entityManager.refresh(saved);

        System.out.println(saved.getCreatedAt() + " O TUTAJ MIALO BYC");

        return noteMapper.toDTO(noteRepository.save(newDailyNote));
    }

    // todo: simplify this solution (3 query should be 1)

    @Override
    @Transactional
    public List<NoteDTO> getAllPublicAndUserPrivateNotesByDate(FetchNotesRequest fetchNotesRequest) {

        Employee employeeById = employeeAPI.getEmployeeById(fetchNotesRequest.employeeId());

        List<NoteDTO> privateNotes =
                noteRepository.findAllByReportedByEmployeeIdAndDate(employeeById, fetchNotesRequest.date()).stream()
                        .map(noteMapper::toDTO)
                        .toList();

        List<NoteDTO> publicNotes =
                noteRepository.findAllByDateAndType(fetchNotesRequest.date(), NoteType.PUBLIC).stream()
                        .map(noteMapper::toDTO)
                        .toList();

        return Stream.of(privateNotes, publicNotes)
                .flatMap(Collection::stream)
                .distinct()
                .sorted(Comparator.comparing(NoteDTO::createdAt).reversed())
                .collect(Collectors.toList());
    }
}
