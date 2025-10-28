package pl.crewops.infrastructure.emailSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@EnableConfigurationProperties(EmailSenderConfigProperties.class)
class EmailSenderConfig {

    @Bean
    public EmailSenderService mailSenderService(
            @Autowired(required = false) JavaMailSender javaMailSender,
            EmailSenderConfigProperties emailSenderConfigProperties) {
        return new EmailSenderService(javaMailSender, emailSenderConfigProperties);
    }
}

@ConfigurationProperties("email")
record EmailSenderConfigProperties(String sender, String profile) {}
