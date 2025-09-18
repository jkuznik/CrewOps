package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import lombok.*;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualificationFormModel {
    private UUID id;
    private @NotNull @Size(min = 2, message = "Minimal length 2") String description;
    private Integer employeesAmount;
    private LocalDate expiredAt;

    public static QualificationFormModel toQualificationFormModel(QualificationDTO qualificationDTO) {
        LocalDate expiredAt = null;
        if (qualificationDTO.expiredAt() != null) {
            expiredAt =
                    qualificationDTO.expiredAt().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return QualificationFormModel.builder()
                .id(qualificationDTO.id())
                .description(qualificationDTO.description())
                .expiredAt(expiredAt)
                .employeesAmount(qualificationDTO.employeesAmount())
                .build();
    }

    public static CreateQualificationDTO toCreateQualificationDTO(QualificationFormModel qualificationFormModel) {
        return CreateQualificationDTO.builder()
                .description(qualificationFormModel.getDescription())
                .build();
    }

    public static UpdateQualificationDTO toUpdateQualificationDTO(QualificationFormModel qualificationFormModel) {
        return UpdateQualificationDTO.builder()
                .qualificationId(qualificationFormModel.getId())
                .description(qualificationFormModel.getDescription())
                .build();
    }
}
