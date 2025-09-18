package pl.crewops.domain.company;

import static pl.crewops.enums.ControllerURL.COMPANIES_CID;
import static pl.crewops.enums.ControllerURL.COMPANY_ID;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.model.dto.company.CompanyDTO;

@RestController
@RequiredArgsConstructor
@Validated
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping(COMPANIES_CID)
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable(COMPANY_ID) UUID companyId) {
        return ResponseEntity.status(HttpStatus.OK).body(companyService.getCompanyById(companyId));
    }
}
