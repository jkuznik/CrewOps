package pl.crewops.domain.dailyNote;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.dailyEntry.DailyEntryAPI;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.model.dto.dailyNote.CreateDailyNoteDTO;
import pl.crewops.model.dto.dailyNote.DailyNoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.Employee;

@Slf4j
@Service
@RequiredArgsConstructor
class DailyNoteService implements DailyNoteAPI {

    private final DailyNoteRepository dailyNoteRepository;
    private final DailyNoteMapper dailyNoteMapper;
    private final DailyEntryAPI dailyEntryAPI;
    private final EmployeeAPI employeeAPI;

    @Override
    @Transactional
    public DailyNoteDTO createDailyNote(CreateDailyNoteDTO createDailyNoteDTO) {

        DailyEntry dailyEntry = (createDailyNoteDTO.dailyEntryId() != null)
                ? dailyEntryAPI.getById(createDailyNoteDTO.dailyEntryId())
                : null;

        Employee employeeById = employeeAPI.getEmployeeById(createDailyNoteDTO.reportedByEmployeeId());

        var newDailyNote = dailyNoteMapper.toEntity(createDailyNoteDTO, dailyEntry, employeeById);

        return dailyNoteMapper.toDTO(dailyNoteRepository.save(newDailyNote));
    }

    @Override
    @Transactional
    public List<DailyNoteDTO> getNotesByDailyEntryId(UUID dailyEntryId) {
        return dailyNoteRepository.findAllByDailyEntryId(dailyEntryId).stream()
                .map(dailyNoteMapper::toDTO)
                .toList();
    }
}
