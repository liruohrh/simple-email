package com.liruo.email.utli;

import com.liruo.email.service.ContentType;
import com.liruo.email.service.Encode;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import org.springframework.core.io.AbstractResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @Author:liruo
 * @Date:2023-09-15-21:09:32
 * @Desc <p>
 * <h2>MimeMessageHelper, MimeMessage, MimePart</h2>
 * <p>
 * 邮件由MimePart组成, 最终形成MimeMessage(MimeMessage本身也是一个MimePart, spring则设计了SmartMimeMessage自动选择某个协议的MimeMessage实现)
 * 如果仅仅是普通的一个文本或者html, 则内容设置在MimeMessage
 * 否则MimeMessage是一个multipart/xxx, 由多个MimePart组成
 * 比如multipart/alternative是指即发送了html, 也发送了普通文本
 * 此时就不会关注其本身的内容如何, 而是关注其MimePart集合
 * </p>
 * <p>
 * 在创建MimeMessageHelper时设置multipart为true就会:
 * <p>1. 设置MimeMessage的content为一个MimeMultipart(multipart/mixed) </p>
 * <p>2. MimeMultipart(multipart/mixed)添加了一个MimeBodyPart(content为一个MimeMultipart(multipart/related)) </p>
 * <p>3. 再设置: rootMimeMultipart=rootMixedMultipart, mimeMultipart=nestedRelatedMultipart </p>
 * 总结: 这样的意图是--可以添加附件与内容同级,  内容是text和html, 且可以添加资源给html(与html,text同级).
 *  等于设置MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED
 *  但是一般情况下, 适合需要丰富html浏览, 又需要附件的, 否则不需要这样写
 *
 * </p>
 * <p>
 * rootMimeMultipart是ROOT, mimeMultipart是MAIN, 现在是MimeMessage含一个rootMimeMultipart, rootMimeMultipart含一个mimeMultipart
 * <p>1. 对helper的基本设置 会设置到 mimeMessage(比如from,to)</p>
 * <p>
 * 2. 对helper的内容设置, 比如text, 会给MainPart中的一个有filename的bodyPart或者创建新的bodyPart设置content,
 * 为MimeMultipart(multipart/alternative或者text/plain,text/html)
 * </p>
 * <p>
 * 3. 添加attachment会给rootMimeMultipart添加一个part(即和MainPart同级), 添加Inline会添加到MainPart.
 * Inline是用于嵌入到html中的, 与attachment不同
 * </p>
 * </p>
 * </p>
 *
 * <p>
 * <h2>updateHeaders</h2>
 * {@link JavaMailSenderImpl#doSend(MimeMessage[], Object[])}
 * {@link MimeMessage#saveChanges()}
 * {@link MimeBodyPart#updateHeaders()}
 * 设置(没有才设置其他的)
 * <p>contentType: setContent时, DataHandler保存的</p>
 * <p>
 * Content-Transfer-Encoding: {@link MimeUtility#getEncoding(DataHandler)}
 * 使用了AsciiOutputStream来根据输入判断Encoding
 * </p>
 * <h2>content和header的设置</h2>
 * <p>设置content后会自动删除所有header, 因此必须在设置content设置header</p>
 * </p>
 *
 * <p>
 * <h2>写入Message对象, 比如MimeMessage</h1>
 * <p>
 * 在 {@link JavaMailSenderImpl#doSend(MimeMessage[], Object[])},
 * 先mimeMessage.saveChanges() 写入, 再指向下面的sendMessage
 * </p>
 *
 * <p>{@link com.sun.mail.smtp.SMTPTransport#sendMessage(javax.mail.Message, javax.mail.Address[])}</p>
 * <p>
 * this.message.writeTo(outputStream(with serverOutput))==>{@link javax.mail.internet.MimeMessage#writeTo(java.io.OutputStream, java.lang.String[])}
 * <p>
 * 如果modified,就调用{@link MimeBodyPart#writeTo(MimePart, OutputStream, String[])}进行写入.
 * 获取header的枚举: {@link javax.mail.internet.MimePart#getNonMatchingHeaderLines(java.lang.String[]) }
 * {@link javax.mail.internet.InternetHeaders.MatchHeaderEnum}, 遍历非空header并写入
 * </p>
 * <p>
 * 写入content, 如果需要就encode, 如果已经写好content了就直接复制并写入.
 * <p>1. 包装output {@link MimeUtility#encode(OutputStream, String)}. </p>
 * <p>2. 获取DataHandler写入output</p>
 * <p>
 * 比如同时设置text和html的
 * <p>
 * 1. MimeMessage的dh的object是一个MimeMultipart(mixed), dh写就是让MimeMultipart写.
 * dh在写的时候会创建DataContentHandler来写,通常是handler_base的子类,
 * Multipart一般都是multipart_mixed,
 * 内容通常是text_plain
 * </p>
 * <p>2. MimeMultipart(mixed)就是让所有bodyPart进行写, 一般这个只有一个bodyPart,
 * 而且其dh的object是MimeMultipart(related), 做法同1
 * </p>
 * <p>3. MimeMultipart(related)同理有MimeMultipart(alternative)</p>
 * <p>4. MimeMultipart(alternative)有2个bodyPart, dh的object分别是text和html</p>
 *
 * <p>但是不知道为什么, 发送时是text/html是Content-Transfer-Encoding: quoted-printable, 如果使用网易邮箱导出, 会自动导出都是base64的, qq不会</p>
 * <p>
 * <p>
 * 如果想发送base64的text和html, 不要用helper进行设置, 自己设置MimeMultipart(alternative)并添加text和html的body,
 * 不用自己编码为base64, 会自动根据Content-Transfer-Encoding设置.
 * </p>
 * <p>
 * 另外, qq对txt支持可能较差, 只有是text+html时text显示会自动生成很多br标签,但是html正常, 其他编码或者组合也都正常
 * </p>
 * </p>
 * </p>
 * </p>
 * </p>
 * <p></p>
 * </p>
 */
public class EmailUtil {

  public static void fillAll(
      MimeMessage mimeMessage,
      String from, String to, String subject, String textContent,String htmlContent,
      List<String> springLikePaths, Map<String, String> contentIdAndSpringLikePathMap,
      Encode encode
  ) throws MessagingException, IOException {
    MimeMessageHelper helper = beforeBoth(mimeMessage,
        MimeMessageHelper.MULTIPART_MODE_RELATED,
        from, to, subject, textContent, htmlContent, encode);

    addInlines(contentIdAndSpringLikePathMap, helper);
    addAttachments(springLikePaths, helper);
  }



  public static void fillBothTextHtmlAndInlines(
      MimeMessage mimeMessage,
      String from, String to, String subject, String textContent,String htmlContent,
      Map<String, String> contentIdAndSpringLikePathMap,
      Encode encode
  ) throws MessagingException, IOException {
    MimeMessageHelper helper = beforeBoth(mimeMessage,
        MimeMessageHelper.MULTIPART_MODE_RELATED,
        from, to, subject, textContent, htmlContent, encode);
    addInlines(contentIdAndSpringLikePathMap, helper);
  }


  public static void fillBothTextHtmlAndAttachments(MimeMessage mimeMessage,
      String from, String to, String subject,
      String textContent, String htmlContent,
      List<String> springLikePaths, Encode encode) throws MessagingException, IOException {
    MimeMessageHelper helper = beforeBoth(mimeMessage,
        MimeMessageHelper.MULTIPART_MODE_MIXED, from, to, subject, textContent, htmlContent,
        encode);

    //attachment
    addAttachments(springLikePaths, helper);
  }
  private static MimeMessageHelper beforeBoth(MimeMessage mimeMessage, int multipartModeRelated,
      String from, String to, String subject, String textContent, String htmlContent, Encode encode)
      throws MessagingException, IOException {
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipartModeRelated);
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(textContent, htmlContent);

    if (encode == Encode.BASE64) {
      MimeMultipart mixedMultipart = helper.getMimeMultipart();
      BodyPart nestPart = mixedMultipart.getBodyPart(0);
      MimeMultipart relatedMultipart = (MimeMultipart)nestPart.getDataHandler().getContent();
      setBase64(relatedMultipart.getBodyPart(0));
      setBase64(relatedMultipart.getBodyPart(1));
    }
    return helper;
  }
  public static void fillBothTextHtml(
      MimeMessage mimeMessage, String from, String to, String subject,
      String textContent, String htmlContent,
      Encode encode, String charset
  ) throws MessagingException {
   /*
   just the multipart/mixed is base64, it's multipart/related's
      multipart/alternative 's bodyPart of text and html is not, is by content
      typically is Content-Transfer-Encoding: quoted-printable
    */
//    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//    helper.setFrom(from);
//    helper.setTo(to);
//    helper.setSubject(subject);
//    helper.setText(textContent, htmlContent);
//    if (encode == Encode.BASE64) {
//      mimeMessage.setHeader("Content-Transfer-Encoding", "base64");
//    }

    mimeMessage.setFrom(from);
    mimeMessage.setRecipient(RecipientType.TO, new InternetAddress(to));
    mimeMessage.setSubject(subject);
    MimeMultipart alternative = new MimeMultipart(Multipart.ALTERNATIVE.value);
    alternative.addBodyPart(getMimeBodyPart(
        textContent,
        encode, ContentType.TEXT_PLAIN, charset
    ));
    alternative.addBodyPart(getMimeBodyPart(
        htmlContent,
        encode, ContentType.TEXT_HTML, charset
    ));
    mimeMessage.setContent(alternative);
  }
  private static void addInlines(Map<String, String> contentIdAndSpringLikePathMap,
      MimeMessageHelper helper) throws MessagingException {
    HashMap<String, AbstractResource> resMap = new HashMap<>();
    for (Entry<String, String> entry : contentIdAndSpringLikePathMap.entrySet()) {
      AbstractResource resource = PathUtil.getSpringLikeResource(entry.getValue());
      if (!resource.exists()) {
        throw new IllegalArgumentException("has not the resource of " + resource);
      }
      resMap.put(entry.getKey(), resource);
    }

    for (Entry<String, AbstractResource> entry : resMap.entrySet()) {
      helper.addInline(entry.getKey(), entry.getValue());
    }
  }

  private static void addAttachments(List<String> springLikePaths, MimeMessageHelper helper) {
    springLikePaths.stream()
        .map(PathUtil::getSpringLikeResource)
        .peek(res -> {
          if (!res.exists()) {
            throw new IllegalArgumentException("has not the resource of " + res);
          }
        })
        .forEach(resource -> {
          try {
            helper.addAttachment(resource.getFilename(), resource);
          } catch (MessagingException e) {
            throw new RuntimeException(e);
          }
        });
  }

  public static void fillInlines(
      MimeMessage mimeMessage,
      String from, String to, String subject, String content,
      Map<String, String> contentIdAndSpringLikePathMap,
      Encode encode
  ) throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
        MimeMessageHelper.MULTIPART_MODE_RELATED);
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(content, true);
    if (encode == Encode.BASE64) {
      setBase64(helper.getMimeMultipart().getBodyPart(0));
    }

    addInlines(contentIdAndSpringLikePathMap, helper);
  }

  public static void fillAttachments(
      MimeMessage mimeMessage,
      String from, String to, String subject, String content, List<String> springLikePaths,
      Encode encode, ContentType contentType
  ) throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
        MimeMessageHelper.MULTIPART_MODE_MIXED);
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(content, contentType == ContentType.TEXT_HTML);
    if (encode == Encode.BASE64) {
      setBase64(helper.getMimeMultipart().getBodyPart(0));
    }

    //attachment
    addAttachments(springLikePaths, helper);
  }

  public static MimeBodyPart getMimeBodyPart(
      String content, Encode encode,
      ContentType contentType, String charset
  ) throws MessagingException {
    MimeBodyPart bodyPart = new MimeBodyPart();
    bodyPart.setContent(content, contentType.withCharset(charset));
    if (encode == Encode.BASE64) {
      setBase64(bodyPart);
    }
    return bodyPart;
  }

  public static void fillEmail(
      MimeMessage mimeMessage,
      String from, String to, String subject, String content,
      Encode encode, ContentType contentType
  ) throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject(subject);

    helper.setText(content, contentType == ContentType.TEXT_HTML);
    if (encode == Encode.BASE64) {
      setBase64(mimeMessage);
    }
  }
  public static void setBase64(BodyPart part) throws MessagingException {
    part.setHeader("Content-Transfer-Encoding", "base64");
  }

  public static void setBase64(Message message) throws MessagingException {
    message.setHeader("Content-Transfer-Encoding", "base64");
  }

}
