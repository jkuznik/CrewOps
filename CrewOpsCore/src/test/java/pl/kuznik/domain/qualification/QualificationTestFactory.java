package pl.kuznik.domain.qualification;

import pl.kuznik.entity.Qualification;

class QualificationTestFactory {

    public static Qualification createQualificationWithEmployees() {
        return Qualification.builder().description("description").build();
    }
}
