package pl.crewops.domain.qualification;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.Qualification;

interface QualificationRepository extends JpaRepository<Qualification, UUID> {

    Page<Qualification> findAll(Pageable pageable);

    Set<Qualification> findAllByIdIn(Set<UUID> ids);
}
