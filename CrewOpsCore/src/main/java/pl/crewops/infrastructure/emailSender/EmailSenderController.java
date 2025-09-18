package pl.crewops.infrastructure.emailSender;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailSenderController {

    private final EmailSenderService emailSenderService;

    @PostMapping("/testMail")
    public void sendEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        emailSenderService.sendEmail(sendEmailRequest);
    }
}
