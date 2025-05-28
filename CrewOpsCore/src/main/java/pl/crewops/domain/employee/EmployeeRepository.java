package pl.crewops.domain.employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.crewops.model.Employee;
import pl.crewops.model.joinTable.EmployeeQualification;

@Repository
interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @EntityGraph(attributePaths = {"qualifications", "vehicles"})
    Page<Employee> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"qualifications", "vehicles"})
    Page<Employee> findAllByActiveIsTrue(Pageable pageable);

    Optional<Employee> findById(UUID id);

    @Query("SELECT e FROM Employee e JOIN e.qualifications q WHERE q.id = :qualificationId")
    Page<Employee> findByQualificationId(@Param("qualificationId") UUID qualificationId, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN e.vehicles v WHERE v.id = :vehiclesId")
    Page<Employee> findByVehiclesId(@Param("vehiclesId") UUID vehicleId, Pageable pageable);

    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}

@Repository
interface EmployeeQualificationRepository extends JpaRepository<EmployeeQualification, UUID> {
    @Query("SELECT eq FROM EmployeeQualification eq " + "WHERE eq.id.employeeId = :employeeId "
            + "AND eq.id.qualificationId = :qualificationId")
    Optional<EmployeeQualification> findByEmployeeQualificationId(
            @Param("employeeId") UUID employeeId, @Param("qualificationId") UUID qualificationId);
}
