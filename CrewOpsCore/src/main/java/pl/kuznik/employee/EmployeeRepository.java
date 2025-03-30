package pl.kuznik.employee;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kuznik.entity.Employee;

@Repository
interface EmployeeRepository extends JpaRepository<Employee, UUID> {}
