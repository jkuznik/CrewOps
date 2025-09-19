package pl.crewops.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.*;
import pl.crewops.model.dto.department.DepartmentDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentFormModel {

    private UUID id;

    @NotNull
    @Size(min = 2, max = 63, message = "Length required between 2-63")
    private String name;

    public static Set<DepartmentDTO> mapToDepartmentDTOs(Set<DepartmentFormModel> formDepartments) {
        return formDepartments.stream()
                .map(dep -> new DepartmentDTO(dep.getId(), dep.getName()))
                .collect(Collectors.toSet());
    }

    public static Set<DepartmentFormModel> mapToDepartmentForms(Set<DepartmentDTO> dtoDepartments) {
        return dtoDepartments.stream()
                .map(dep -> new DepartmentFormModel(dep.id(), dep.name()))
                .collect(Collectors.toSet());
    }

    public static List<DepartmentFormModel> mapToDepartmentFormsOrderedResult(List<DepartmentDTO> dtoDepartments) {
        return dtoDepartments.stream()
                .map(dep -> new DepartmentFormModel(dep.id(), dep.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DepartmentFormModel that)) return false;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
