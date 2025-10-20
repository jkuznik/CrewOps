package pl.crewops.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.model.tenantSchema.JobPosition;

public class JobPositionSetSerializer extends JsonSerializer<Set<JobPosition>> {

    @Override
    public void serialize(Set<JobPosition> jobPositions, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeObject(jobPositions.stream().map(JobPosition::getId).collect(Collectors.toSet()));
    }
}
