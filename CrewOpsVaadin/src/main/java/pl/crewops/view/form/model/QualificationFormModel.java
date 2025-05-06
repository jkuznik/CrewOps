package pl.crewops.view.form.model;

import java.util.UUID;
import lombok.*;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualificationFormModel {
    private UUID id;
    private String description;

    // TODO: maybe this is proper place to store employee amount of each qualification for the purposes of the grid in
    //  QualificationView

    public static QualificationFormModel toQualificationFormModel(QualificationDTO qualificationDTO) {
        return QualificationFormModel.builder()
                .id(qualificationDTO.id())
                .description(qualificationDTO.description())
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
