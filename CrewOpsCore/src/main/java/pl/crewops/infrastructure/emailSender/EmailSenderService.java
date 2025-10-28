package pl.crewops.infrastructure.emailSender;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Log4j2
class EmailSenderService implements EmailSenderAPI {

    private final JavaMailSender javaMailSender;
    private final EmailSenderConfigProperties emailSenderConfigProperties;

    public EmailSenderService(JavaMailSender javaMailSender, EmailSenderConfigProperties emailSenderConfigProperties) {
        this.javaMailSender = javaMailSender;
        this.emailSenderConfigProperties = emailSenderConfigProperties;
    }

    @Override
    public void sendEmail(SendEmailRequest sendEmailRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(emailSenderConfigProperties.sender(), emailSenderConfigProperties.profile());
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
