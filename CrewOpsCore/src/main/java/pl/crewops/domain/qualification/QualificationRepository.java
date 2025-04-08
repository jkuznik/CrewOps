package pl.crewops.domain.qualification;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.crewops.model.Qualification;

@Repository
interface QualificationRepository extends JpaRepository<Qualification, UUID> {

    Page<Qualification> findAll(Pageable pageable);

    Set<Qualification> findAllByIdIn(Set<UUID> ids);
}
