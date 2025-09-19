package pl.crewops.domain.tenant;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import pl.crewops.model.publicSchema.Tenant;

@Validated
public interface TenantAPI {

    Tenant getByCompanyId(@NotNull UUID companyId);

    Optional<Tenant> getOptionalByTaxId(@NotNull String taxId);

    Tenant saveTenant(@NotNull Tenant tenant);

    void delete(@NotNull UUID tenantId);
}
