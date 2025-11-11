package pl.crewops.domain.shift;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.compositePK.SJPID;
import pl.crewops.model.joinTable.ShiftJobPosition;
import pl.crewops.model.tenantSchema.Shift;

interface ShiftRepository extends JpaRepository<Shift, UUID> {}

interface SJPRepository extends JpaRepository<ShiftJobPosition, SJPID> {}
