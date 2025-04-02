package pl.kuznik.domain.employee;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kuznik.entity.Employee;
import pl.kuznik.entity.joinTable.EmployeeQualification;

@Repository
interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}

@Repository
interface EmployeeQualificationRepository extends JpaRepository<EmployeeQualification, UUID> {
    Optional<EmployeeQualification> findByEmployeeId(UUID employeeId);
}
