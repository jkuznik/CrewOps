package pl.crewops.domain.dailyEntry;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryAuditDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyNoteDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyEntryAudit;
import pl.crewops.model.tenantSchema.DailyNote;

class DailyEntryMapper {

    static DailyEntry mapToEntity(CreateDailyEntryDTO createDailyEntryDTO) {
        return DailyEntry.builder()
                .employeeId(createDailyEntryDTO.employeeId())
                .entryDate(createDailyEntryDTO.entryDate())
                .startTime(createDailyEntryDTO.startTime())
                .endTime(createDailyEntryDTO.endTime())
                .overtime(createDailyEntryDTO.overTime())
                .attendance(createDailyEntryDTO.attendance())
                .status(createDailyEntryDTO.status())
                .build();
    }

    static DailyEntryDTO mapToDTO(DailyEntry dailyEntry) {
        JobPositionDTO jobPositionDTO = null;
        if (dailyEntry.getJobPosition() != null) {
            jobPositionDTO = JobPositionDTO.builder()
                    .id(dailyEntry.getJobPosition().getId())
                    .name(dailyEntry.getJobPosition().getName())
                    // todo: implement rest (machineDTO and set<qualificationDTO>
                    .build();
        }

        return DailyEntryDTO.builder()
                .id(dailyEntry.getId())
                .employeeId(dailyEntry.getEmployeeId())
                .entryDate(dailyEntry.getEntryDate())
                .startTime(dailyEntry.getStartTime())
                .endTime(dailyEntry.getEndTime())
                .overTime(dailyEntry.getOvertime())
                .jobPosition(jobPositionDTO)
                .dailyNotes(getMappedDailyNotes(dailyEntry))
                .auditEvents(getMappedAuditEvents(dailyEntry))
                .attendance(dailyEntry.getAttendance())
                .status(dailyEntry.getStatus())
                .build();
    }

    static DailyNoteDTO mapToDTO(DailyNote dailyNote) {
        return DailyNoteDTO.builder()
                .id(dailyNote.getId())
                .dailyEntryId(dailyNote.getDailyEntry().getId())
                .reportedByEmployeeId(dailyNote.getReportedByEmployeeId())
                .type(dailyNote.getType())
                .content(dailyNote.getContent())
                .createdAt(dailyNote.getCreatedAt())
                .updatedAt(dailyNote.getUpdatedAt())
                .build();
    }

    static DailyEntryAuditDTO mapToDTO(DailyEntryAudit dailyEntryAudit) {
        return DailyEntryAuditDTO.builder()
                .id(dailyEntryAudit.getId())
                .dailyEntryId(dailyEntryAudit.getDailyEntry().getId())
                .eventType(dailyEntryAudit.getEventType())
                .payload(dailyEntryAudit.getPayload())
                .comment(dailyEntryAudit.getComment())
                .createdAt(dailyEntryAudit.getCreatedAt())
                .build();
    }

    private static Set<DailyNoteDTO> getMappedDailyNotes(DailyEntry dailyEntry) {
        Set<DailyNote> dailyNotes = dailyEntry.getDailyNotes();
        if (dailyNotes == null) {
            return Collections.emptySet();
        }

        return dailyNotes.stream().map(DailyEntryMapper::mapToDTO).collect(Collectors.toSet());
    }

    private static Set<DailyEntryAuditDTO> getMappedAuditEvents(DailyEntry dailyEntry) {
        Set<DailyEntryAudit> auditEvents = dailyEntry.getAuditEvents();
        if (auditEvents == null) {
            return Collections.emptySet();
        }

        return auditEvents.stream().map(DailyEntryMapper::mapToDTO).collect(Collectors.toSet());
    }
}
