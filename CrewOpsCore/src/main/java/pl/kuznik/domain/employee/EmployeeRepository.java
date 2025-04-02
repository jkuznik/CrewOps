package pl.kuznik.domain.employee;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.joinTable.EmployeeQualification;

@Repository
interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Page<Employee> findAll(Pageable pageable);

    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}

@Repository
interface EmployeeQualificationRepository extends JpaRepository<EmployeeQualification, UUID> {
    @Query("SELECT eq FROM EmployeeQualification eq " + "WHERE eq.id.employeeId = :employeeId "
            + "AND eq.id.qualificationId = :qualificationId")
    Optional<EmployeeQualification> findByEmployeeQualificationId(
            @Param("employeeId") UUID employeeId, @Param("qualificationId") UUID qualificationId);
}
