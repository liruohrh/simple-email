package com.liruo.email.service;

import com.liruo.email.EmailException;
import com.liruo.email.SimpleEmailProperties;
import com.liruo.email.template.TemplateEngine;
import com.liruo.email.utli.EmailUtil;
import com.liruo.email.utli.PathUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Slf4j
public class MailServiceImpl implements MailService {

  private final String senderEmail;
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final Encode encode;
  private final String charset;

  public MailServiceImpl(
      String senderEmail,
      JavaMailSender mailSender,
      TemplateEngine templateEngine,
      SimpleEmailProperties config
  ) {
    this.senderEmail = senderEmail;
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
    this.encode = config.getEncode();
    this.charset = mailSender instanceof JavaMailSenderImpl ?
        ((JavaMailSenderImpl) mailSender).getDefaultEncoding()
        : "UTF-8";
  }

  @Override
  public void send(String to, String subject,  ContentType contentType,
      String template, Map<String, String> values) {
    try {
      String content = templateEngine.render(template, values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();

      EmailUtil.fillEmail(mimeMessage,
          senderEmail, to, subject,
          content, encode, contentType);

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }


  @Override
  public void sendWithAttachments(String to, String subject, ContentType contentType,
      List<String> springLikePaths,
      String template, Map<String, String> values) {
    try {
      String content = templateEngine.render(template, values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();

      EmailUtil.fillAttachments(
          mimeMessage,
          senderEmail, to, subject,
          content, springLikePaths,
          encode, contentType
      );

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }


  @Override
  public void sendWithInlines(String to, String subject,
      Map<String, String> contentIdAndSpringLikePathMap,
      String template, Map<String, String> values) {
    if(!".html".equals(PathUtil.ext(template))){
      throw new EmailException("template must a html, you template is " + template);
    }
    try {
      String content = templateEngine.render(template, values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();

      EmailUtil.fillInlines(
          mimeMessage,
          senderEmail, to, subject,
          content, contentIdAndSpringLikePathMap,
          encode
      );

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }

  @Override
  public void sendBothTextHtml(String to, String subject, String template,
      Map<String, String> values) {
    try {
      String textContent = templateEngine.render(template + ".txt", values);
      String htmlContent = templateEngine.render(template + ".html", values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      EmailUtil.fillBothTextHtml(mimeMessage,
          senderEmail, to, subject,
          textContent, htmlContent,
          encode, charset);

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }

  @Override
  public void sendBothTextHtmlWithAttachments(String to, String subject,
      List<String> springLikePaths, String template, Map<String, String> values) {
    try {
      String textContent = templateEngine.render(template + ".txt", values);
      String htmlContent = templateEngine.render(template + ".html", values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      EmailUtil.fillBothTextHtmlAndAttachments(mimeMessage,
          senderEmail, to, subject,
          textContent, htmlContent,
          springLikePaths,
          encode
      );

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException | IOException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }

  @Override
  public void sendBothTextHtmlWithInlines(String to, String subject,
      Map<String, String> contentIdAndSpringLikePathMap,
      String template, Map<String, String> values) {
    try {
      String textContent = templateEngine.render(template + ".txt", values);
      String htmlContent = templateEngine.render(template + ".html", values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      EmailUtil.fillBothTextHtmlAndInlines(mimeMessage,
          senderEmail, to, subject,
          textContent, htmlContent,
          contentIdAndSpringLikePathMap,
          encode
      );

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException | IOException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }

  @Override
  public void sendAll(String to, String subject, List<String> springLikePaths,
      Map<String, String> contentIdAndSpringLikePathMap, String template,
      Map<String, String> values) {
    try {
      String textContent = templateEngine.render(template + ".txt", values);
      String htmlContent = templateEngine.render(template + ".html", values);
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      EmailUtil.fillAll(mimeMessage,
          senderEmail, to, subject,
          textContent, htmlContent,
          springLikePaths, contentIdAndSpringLikePathMap,
          encode
      );

      mailSender.send(mimeMessage);
      if (log.isDebugEnabled()) {
        log.debug("subject={}, to={}", subject, to);
      }
    } catch (MessagingException | IOException e) {
      throw new EmailException(e.getMessage(), e);
    }
  }
}
