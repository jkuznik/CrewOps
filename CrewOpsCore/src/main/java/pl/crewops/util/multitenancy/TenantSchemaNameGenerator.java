package pl.crewops.util.multitenancy;

import java.util.UUID;
import pl.crewops.exception.multitenancy.CreateSchemaException;

public class TenantSchemaNameGenerator {

    private TenantSchemaNameGenerator() {}

    public static String generateTenantSchemaName(String name, UUID tenantId) throws CreateSchemaException {

        var prefix = name.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        var uuidPart = tenantId.toString().replaceAll("-", "").substring(0, 12);
        var schemaName = prefix + "_" + uuidPart;

        if (schemaName.length() > 63) {
            throw new CreateSchemaException(
                    "Tenant schema name is too long - check if tenant name validation is still max 50 characters");
        }

        return schemaName;
    }
}
