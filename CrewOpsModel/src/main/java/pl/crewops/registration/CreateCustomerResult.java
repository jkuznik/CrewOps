package pl.crewops.registration;

import lombok.Builder;
import pl.crewops.dto.auth.CreateAuthUserResult;
import pl.crewops.dto.company.CompanyDTO;

@Builder
public record CreateCustomerResult(CreateAuthUserResult authUserResult, CompanyDTO companyDTO) {}
