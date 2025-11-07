package pl.crewops.domain.note;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.tenantSchema.Employee;
import pl.crewops.model.tenantSchema.Note;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "reportedByEmployeeId", source = "reportedByEmployeeId.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    NoteDTO toDTO(Note note);

    @Mapping(target = "reportedByEmployeeId", ignore = true)
    Note toEntity(CreateNoteDTO dto);

    default Note toEntity(CreateNoteDTO dto, Employee resolvedEmployee) {
        Note note = toEntity(dto);

        note.setReportedByEmployeeId(resolvedEmployee);

        return note;
    }
}
