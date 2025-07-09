package pl.crewops.domain.company;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.Company;

interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByEmail(String email);

    Optional<Company> findById(UUID id);
}
