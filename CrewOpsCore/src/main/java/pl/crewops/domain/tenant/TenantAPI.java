package pl.crewops.domain.tenant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;

@Validated
public interface TenantAPI {

    TenantDTO createTenant(@NotNull @Valid CreateTenantDTO createTenantDTO);
}
