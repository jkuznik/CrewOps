package pl.crewops.dto.qualification;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualificationFormModel {
    private String description;

    // TODO: maybe this is proper place to store employee amount of each qualification for the purposes of the grid in
    // QualificationView

    public static CreateQualificationDTO toCreateQualificationDTO(QualificationFormModel qualificationFormModel) {
        return CreateQualificationDTO.builder()
                .description(qualificationFormModel.getDescription())
                .build();
    }
}
