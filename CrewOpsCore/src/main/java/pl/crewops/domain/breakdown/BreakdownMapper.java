package pl.crewops.domain.breakdown;

import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.model.Breakdown;

class BreakdownMapper {

    public static BreakdownDTO toDTO(Breakdown breakdown) {
        return BreakdownDTO.builder()
                .id(breakdown.getId())
                .description(breakdown.getDescription())
                // TODO: implement rest, refactor mappers and move all mapped methods to entity classes
                //                .vehicle(breakdown.getVehicle())
                //                .reportedBy(breakdown.getReportedBy())
                //                .repairedBy(breakdown.getRepairedBy())
                .solved(breakdown.isSolved())
                .solvedAt(breakdown.getSolvedAt())
                .build();
    }
}
