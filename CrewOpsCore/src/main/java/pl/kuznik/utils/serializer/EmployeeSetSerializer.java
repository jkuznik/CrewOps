package pl.kuznik.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import pl.kuznik.entity.Employee;

public class EmployeeSetSerializer extends JsonSerializer<Set<Employee>> {

    @Override
    public void serialize(Set<Employee> employees, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeObject(employees.stream().map(Employee::getId).collect(Collectors.toSet()));
    }
}
