package pl.kuznik.employee;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kuznik.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {}
