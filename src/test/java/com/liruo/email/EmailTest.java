package com.liruo.email;

import com.liruo.email.service.ContentType;
import com.liruo.email.service.MailService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @Author:liruo
 * @Date:2023-09-15-19:10:47
 * @Desc
 */
@SpringBootTest(
    classes = {
        EmailAutoConfiguration.class,
        MailSenderAutoConfiguration.class
    }
)
@TestPropertySource("file:A:\\code\\backend\\java\\component\\simple-email\\private\\config-email-163.properties")
public class EmailTest {

  //🚀 Your GitHub launch code
  Map<String, String> args1 = new HashMap<>() {
    {
      put("username", "test");
      put("verificationCode", "123456abcdefg");
      put("verifyEmailByClickLinkUrl",
          "https://github.com/users/test/emails/278617715/confirm_verification/01384671?via_launch_code_email=true");
    }
  };
  List<String> attachments = Arrays.asList(
      "file:github/readme.md",
      "classpath:/test.md"
  );
  HashMap<String, String> inlineMap = new HashMap<>(){
    {
      put("testPng", "classpath:/test.png");
    }
  };
  @Inject
  MailService mailService;
  @Test
  public void testSendAll() {
    mailService.sendAll(
        "2372221537@qq.com", "🚀 testSendAll",
        attachments, inlineMap,
        "test-inline", Collections.emptyMap()
    );
  }
  @Test
  public void testSendBothTextHtmlWithInlines() {
    mailService.sendBothTextHtmlWithInlines(
        "2372221537@qq.com", "🚀 testSendBothTextHtmlWithInlines",
        inlineMap,
        "test-inline", Collections.emptyMap()
    );
  }
  @Test
  public void testSendBothTextHtmlWithAttachments() {
    mailService.sendBothTextHtmlWithAttachments(
        "2372221537@qq.com", "🚀 testSendBothTextHtmlWithAttachments",
        attachments,
        "verificationCodeDefault1", Collections.emptyMap()
    );
  }
  @Test
  public void testSendBothTextHtml() {
    mailService.sendBothTextHtml("2372221537@qq.com", "🚀 testSendBothTextHtml",
        "verificationCodeDefault1", args1);
  }


  @Test
  public void testSendWithInlines() {
    mailService.sendWithInlines(
        "2372221537@qq.com", "🚀 testSendWithInlines",
        inlineMap,
        "test-inline.html", Collections.emptyMap()
    );
  }

  @Test
  public void testSendHtmlWithAttachments() {
    mailService.sendWithAttachments(
        "2372221537@qq.com", "🚀 sendWithAttachments",
        ContentType.TEXT_HTML, attachments,
        "verificationCodeDefault1.html", args1
    );
  }

  /**
   * 自动设置
   * Content-Type: application/octet-stream; name=readme
   * Content-Transfer-Encoding: base64
   * Content-Disposition: attachment; filename=readme
   */
  @Test
  public void testSendTextWithAttachments() {
    mailService.sendWithAttachments(
        "2372221537@qq.com", "🚀 testSendTextWithAttachments",
        ContentType.TEXT_PLAIN, attachments,
        "verificationCodeDefault1.txt", args1
    );
  }


  @Test
  public void testSendHtml() {
    mailService.send("liruo_hrh@163.com", "🚀 testSendHtml",
        ContentType.TEXT_HTML,
        "verificationCodeDefault1.html", args1);
  }

  @Test
  public void testSendText() {
    mailService.send("liruo_hrh@163.com", "🚀 testSendText",
        ContentType.TEXT_PLAIN,
        "verificationCodeDefault1.html", args1);
  }
}
