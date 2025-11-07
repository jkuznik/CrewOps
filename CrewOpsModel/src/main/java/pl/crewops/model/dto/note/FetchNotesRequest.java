package pl.crewops.model.dto.note;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FetchNotesRequest(UUID employeeId, LocalDate date) {}
