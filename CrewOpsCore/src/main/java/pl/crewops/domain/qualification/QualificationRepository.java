package pl.crewops.domain.qualification;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.model.Qualification;

interface QualificationRepository extends JpaRepository<Qualification, UUID> {

    Page<Qualification> findAll(Pageable pageable);

    Set<Qualification> findAllByIdIn(Set<UUID> ids);

    @Query(
            """
    SELECT new pl.crewops.dto.qualification.QualificationDTO(
        q.id,
        q.description,
        eq.expiredAt,
        SIZE(q.employees)
    )
    FROM EmployeeQualification eq
    JOIN eq.qualification q
    WHERE eq.employee.id = :employeeId
""")
    Set<QualificationDTO> findAllQualificationsWithExpiredAtByEmployeeId(@Param("employeeId") UUID employeeId);
}
