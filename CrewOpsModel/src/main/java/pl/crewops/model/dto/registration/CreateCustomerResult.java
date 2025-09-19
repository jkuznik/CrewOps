package pl.crewops.model.dto.registration;

import lombok.Builder;
import pl.crewops.model.dto.auth.CreateAuthUserResult;
import pl.crewops.model.dto.company.CompanyDTO;

@Builder
public record CreateCustomerResult(CreateAuthUserResult authUserResult, CompanyDTO companyDTO) {}
