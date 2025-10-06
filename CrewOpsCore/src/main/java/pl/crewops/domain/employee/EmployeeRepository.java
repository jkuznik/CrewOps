package pl.crewops.domain.employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.crewops.model.joinTable.EmployeeQualification;
import pl.crewops.model.tenantSchema.Employee;

interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    List<Employee> findAllByActiveIsTrue();

    @Query("SELECT e FROM Employee e JOIN e.departments d WHERE d.id = :departmentId AND e.active = true")
    List<Employee> findAllByDepartmentIdAndActiveIsTrue(@Param("departmentId") UUID departmentId);

    @EntityGraph(attributePaths = {"qualifications", "machines"})
    Page<Employee> findAllByActiveIsTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"qualifications", "machines"})
    Optional<Employee> findById(UUID id);

    @Query("SELECT e FROM Employee e JOIN e.qualifications q WHERE q.id = :qualificationId")
    Page<Employee> findByQualificationIdAndActiveIsTrue(
            @Param("qualificationId") UUID qualificationId, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.machines v WHERE v.id = :machinesId")
    Page<Employee> findByMachinesIdAndActiveIsTrue(@Param("machinesId") UUID machineId, Pageable pageable);

    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}

interface EmployeeQualificationRepository extends JpaRepository<EmployeeQualification, UUID> {
    @Query("SELECT eq FROM EmployeeQualification eq " + "WHERE eq.id.employeeId = :employeeId "
            + "AND eq.id.qualificationId = :qualificationId")
    Optional<EmployeeQualification> findByEmployeeQualificationId(
            @Param("employeeId") UUID employeeId, @Param("qualificationId") UUID qualificationId);

    @Query(
            """
    SELECT eq FROM EmployeeQualification eq
    WHERE eq.employee.id = :employeeId
      AND eq.expiredAt IS NOT NULL
""")
    Set<EmployeeQualification> findAllByEmployeeIdAndExpiredAtIsNotNull(@Param("employeeId") UUID employeeId);
}
