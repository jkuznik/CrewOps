package pl.crewops.infrastructure.emailSender;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
class EmailSenderService implements EmailSenderAPI {

    // todo: use env placeholder
    private static final String EMAIL_SENDER = "crewops@devsmith.eu";
    private static final String EMAIL_PERSONAL = "no-reply-crewops";

    @Autowired(required = false)
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(SendEmailRequest sendEmailRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(EMAIL_SENDER, EMAIL_PERSONAL);
            mimeMessageHelper.setTo(sendEmailRequest.toEmailAddress());
            mimeMessageHelper.setSubject(sendEmailRequest.subject());
            mimeMessageHelper.setText(sendEmailRequest.body(), false);

            javaMailSender.send(mimeMessage);

            log.info("Email sent");

        } catch (Exception e) {
            log.error("Send message error: " + e.getMessage());
        }
    }
}
