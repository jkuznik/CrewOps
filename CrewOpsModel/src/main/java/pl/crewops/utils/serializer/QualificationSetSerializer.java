package pl.crewops.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.model.Qualification;

public class QualificationSetSerializer extends JsonSerializer<Set<Qualification>> {

    @Override
    public void serialize(Set<Qualification> qualifications, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeObject(qualifications.stream().map(Qualification::getId).collect(Collectors.toSet()));
    }
}
