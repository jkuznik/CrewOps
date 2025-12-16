package pl.crewops.domain.message;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.crewops.model.dto.message.CreateMessageDTO;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.tenantSchema.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "read", ignore = true) // jeśli ustawiane przez biznes/DB
    Message toEntity(CreateMessageDTO dto);

    MessageDTO toDTO(Message entity);
}
