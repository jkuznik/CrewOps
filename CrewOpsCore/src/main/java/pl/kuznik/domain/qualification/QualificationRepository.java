package pl.kuznik.domain.qualification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kuznik.entity.Qualification;

@Repository
interface QualificationRepository extends JpaRepository<Qualification, Long> {}
