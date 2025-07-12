package pl.crewops.domain.tenant;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.publicSchema.Tenant;

@Validated
public interface TenantAPI {

    Tenant getByCompanyId(@NotNull UUID companyId);

    Tenant saveTenant(@NotNull Tenant tenant);
}
