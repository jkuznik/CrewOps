package pl.crewops.infrastructure.multitenancy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    public static final String DEFAULT_TENANT = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String resolvedTenantId = TenantContext.getCurrentTenant();

        if (resolvedTenantId == null) {
            resolvedTenantId = DEFAULT_TENANT;
        }

        log.info("[TenantResolver] Some action for tenant " + resolvedTenantId);

        return resolvedTenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
