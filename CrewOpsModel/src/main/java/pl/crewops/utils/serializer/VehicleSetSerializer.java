package pl.crewops.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import pl.crewops.model.Vehicle;

public class VehicleSetSerializer extends JsonSerializer<Set<Vehicle>> {
    @Override
    public void serialize(Set<Vehicle> vehicles, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(vehicles.stream().map(Vehicle::getId).collect(Collectors.toSet()));
    }
}
