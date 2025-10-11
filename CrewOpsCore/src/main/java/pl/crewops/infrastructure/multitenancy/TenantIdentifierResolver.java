package pl.crewops.infrastructure.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    public static final String DEFAULT_TENANT = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String resolvedTenantId = TenantContext.getCurrentTenant();

        if (resolvedTenantId == null) {
            resolvedTenantId = DEFAULT_TENANT;
        }

        // todo: consider logger instead of sout
        System.out.println("[TenantResolver] Some action for tenant " + resolvedTenantId);

        return resolvedTenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
