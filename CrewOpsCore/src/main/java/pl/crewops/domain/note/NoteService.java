package pl.crewops.domain.note;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.enums.NoteType;
import pl.crewops.model.dto.note.CreateNoteDTO;
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

    @Override
    @Transactional
    public List<NoteDTO> getPublicNotesByDate(LocalDate date) {
        return noteRepository.findAllByDateAndType(date, NoteType.PUBLIC).stream()
                .map(noteMapper::toDTO)
                .toList();
    }
}
