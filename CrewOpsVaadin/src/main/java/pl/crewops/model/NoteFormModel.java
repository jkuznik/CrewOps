package pl.crewops.model;

import java.time.Instant;
import lombok.*;
import pl.crewops.enums.NoteType;
import pl.crewops.model.dto.note.NoteDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteFormModel {

    private String content;
    private String reportedByEmployeeId;
    private Instant createdAt;
    private boolean isPrivate;

    public static NoteFormModel toNoteFormModel(NoteDTO noteDTO) {
        return NoteFormModel.builder()
                .content(noteDTO.content())
                .reportedByEmployeeId(noteDTO.reportedByEmployeeId().toString())
                .createdAt(noteDTO.createdAt())
                .isPrivate(noteDTO.type().equals(NoteType.PRIVATE))
                .build();
    }
}
