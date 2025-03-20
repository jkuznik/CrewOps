package pl.kuznik.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kuznik.entity.Employee;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
