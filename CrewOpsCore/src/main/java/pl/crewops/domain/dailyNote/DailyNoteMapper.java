package pl.crewops.domain.dailyNote;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.model.dto.dailyNote.CreateDailyNoteDTO;
import pl.crewops.model.dto.dailyNote.DailyNoteDTO;
import pl.crewops.model.tenantSchema.DailyEntry;
import pl.crewops.model.tenantSchema.DailyNote;
import pl.crewops.model.tenantSchema.Employee;

@Mapper(componentModel = "spring")
public interface DailyNoteMapper {

    @Mapping(target = "dailyEntryId", source = "dailyEntry.id")
    @Mapping(target = "reportedByEmployeeId", source = "reportedByEmployeeId.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    DailyNoteDTO toDTO(DailyNote dailyNote);

    @Mapping(target = "dailyEntry", ignore = true)
    @Mapping(target = "reportedByEmployeeId", ignore = true)
    DailyNote toEntity(CreateDailyNoteDTO dto);

    default DailyNote toEntity(CreateDailyNoteDTO dto, DailyEntry resolvedDailyEntry, Employee resolvedEmployee) {
        DailyNote dailyNote = toEntity(dto);

        dailyNote.setDailyEntry(resolvedDailyEntry);
        dailyNote.setReportedByEmployeeId(resolvedEmployee);

        return dailyNote;
    }
}
