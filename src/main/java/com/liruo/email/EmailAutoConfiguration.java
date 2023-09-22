package com.liruo.email;

import com.liruo.email.service.MailService;
import com.liruo.email.service.MailServiceImpl;
import com.liruo.email.template.TemplateEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@ConditionalOnProperty(prefix = "spring.mail", name = "username")
@AutoConfiguration(
    after = {
        MailSenderAutoConfiguration.class
    }
)
@EnableConfigurationProperties(SimpleEmailProperties.class)
public class EmailAutoConfiguration {

  private final SimpleEmailProperties config;

  public EmailAutoConfiguration(SimpleEmailProperties config) {
    this.config = config;
  }

  @Bean
  public TemplateEngine templateEngine() {
    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.init(config);
    return templateEngine;
  }

  @Bean
  public MailService mailService(
      @Value("${spring.mail.username}") String from,
      JavaMailSender mailSender) {
    return new MailServiceImpl(from, mailSender, templateEngine(), config);
  }
}
