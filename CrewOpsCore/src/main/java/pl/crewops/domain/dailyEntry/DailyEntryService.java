package pl.crewops.domain.dailyEntry;

import static pl.crewops.domain.dailyEntry.DailyEntryMapper.mapToDTO;
import static pl.crewops.domain.dailyEntry.DailyEntryMapper.mapToEntity;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.enums.DailyAttendanceStatus;
import pl.crewops.enums.DailyEntryAuditType;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyEntryAudit;

@Slf4j
@Component
@RequiredArgsConstructor
class DailyEntryService implements DailyEntryAPI {

    private final DailyEntryRepository dailyEntryRepository;
    private final DailyEntryRepository.DailyEntryAuditRepository dailyEntryAuditRepository;

    @Override
    @Transactional
    public DailyEntryDTO createDailyEntry(CreateDailyEntryDTO createDailyEntryDTO) {
        var dailyEntry = mapToEntity(createDailyEntryDTO);

        DailyAttendanceStatus attendance = dailyEntry.getAttendance();

        // todo: separate logic to decide if vacation has different types
        //  like (wypoczynkowy/okolicznosciowy/bezpłatny) - wydaje sie że dobrym rozwiazaniem bedzie zmiana struktury
        //  db tak zeby DailyAttendanceStatus nie było enum a osobną tabelą w db, to pozwoli na rozbudowanie i
        //  edytowanie statusów w przyszłości, albo zostawic ten enum tak jak jest tylko dodac tabele w ktore bedzie
        //  mozliwosc tej rozbudowy i edycji
        switch (attendance) {
            case VACATION -> {
                DailyEntryAudit event = DailyEntryAudit.builder()
                        .dailyEntry(dailyEntry)
                        .actionByEmployeeId(createDailyEntryDTO.actionByEmployeeId())
                        .eventType(DailyEntryAuditType.ATTENDANCE_STATUS_CHANGED)
                        .details("Set vacation status by employee: " + createDailyEntryDTO.actionByEmployeeId())
                        .build();
                dailyEntryAuditRepository.save(event);
            }
            case SICK_LEAVE -> {
                DailyEntryAudit event = DailyEntryAudit.builder()
                        .dailyEntry(dailyEntry)
                        .actionByEmployeeId(createDailyEntryDTO.actionByEmployeeId())
                        .eventType(DailyEntryAuditType.ATTENDANCE_STATUS_CHANGED)
                        .details("Set sick leave status by employee: " + createDailyEntryDTO.actionByEmployeeId())
                        .build();
                dailyEntryAuditRepository.save(event);
            }
            case OTHER -> {
                DailyEntryAudit event = DailyEntryAudit.builder()
                        .dailyEntry(dailyEntry)
                        .actionByEmployeeId(createDailyEntryDTO.actionByEmployeeId())
                        .eventType(DailyEntryAuditType.ATTENDANCE_STATUS_CHANGED)
                        .details("Set other status by employee: " + createDailyEntryDTO.actionByEmployeeId())
                        .build();
                dailyEntryAuditRepository.save(event);
            }

            default -> {}
        }

        return mapToDTO(dailyEntryRepository.save(dailyEntry));
    }

    @Override
    @Transactional
    public DailyEntryDTO getByEmployeeIdAndEntryDate(UUID employeeId, LocalDate entryDate) {
        DailyEntry dailyEntry = dailyEntryRepository
                .findByEmployeeIdAndEntryDate(employeeId, entryDate)
                // todo: custom exception
                .orElseThrow(() -> new NoSuchElementException());

        return mapToDTO(dailyEntry);
    }
}
