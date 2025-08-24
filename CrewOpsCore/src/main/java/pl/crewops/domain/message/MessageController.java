package pl.crewops.domain.message;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.dto.message.SendMessageCommand;

@RestController
@RequiredArgsConstructor
@Validated
class MessageController {

    private final MessageAPI messageAPI;

    @PostMapping(MESSAGES)
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid @NotNull SendMessageCommand sendMessageCommand) {
        messageAPI.sendMessage(sendMessageCommand);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(MESSAGES_EID)
    public ResponseEntity<List<MessageDTO>> getMessagesByRecipientEmployeeId(
            @PathVariable(EMPLOYEE_ID) UUID recipientEmployeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(messageAPI.getAllMessagesByRecipientEmployeeId(recipientEmployeeId, page, size));
    }

    @PatchMapping(MESSAGES_MID)
    public ResponseEntity<MessageDTO> setMessageReadStatus(
            @PathVariable(MESSAGE_ID) UUID messageId, @RequestBody boolean status) {
        return ResponseEntity.ok(messageAPI.setMessageReadStatus(messageId, status));
    }
}
