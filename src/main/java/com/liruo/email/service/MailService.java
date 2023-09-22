package com.liruo.email.service;

import com.liruo.email.EmailException;
import java.util.List;
import java.util.Map;
/**
 *
 * base args:
 *  to: receiver
 *  subject
 *  template: template to rend
 *  values: template variables's value
 */
public interface MailService {

    /**
     * @throws EmailException
     */

    void send(String to, String subject, ContentType contentType,
        String template, Map<String, String> values);

    /**
     * @throws EmailException
     */
    void sendWithAttachments(String to, String subject, ContentType contentType,
        List<String> springLikePaths,
        String template, Map<String, String> values);


    /**
     * only with html
     *  @throws EmailException
     */
    void sendWithInlines(String to, String subject,
        Map<String, String> contentIdAndSpringLikePathMap,
        String template, Map<String, String> values);


    /**
     * @param template has not extension, template has some filename but extension is different
     * @param values both txt and html
     * @throws EmailException
     */
    void sendBothTextHtml(String to, String subject, String template, Map<String, String> values);

    /**
     * ==> sendWithInlines + sendBothTextHtml
     */
    void sendBothTextHtmlWithAttachments(String to, String subject,
        List<String> springLikePaths,
        String template, Map<String, String> values);

    /**
     * ==> sendWithInlines + sendBothTextHtml
     */
    void sendBothTextHtmlWithInlines(String to, String subject,
        Map<String, String> contentIdAndSpringLikePathMap,
        String template, Map<String, String> values);

    /**
     * must has text, html with inlines, attachments.
     * ==> sendWithInlines + sendWithAttachments + sendBothTextHtml
     */
    void sendAll(String to, String subject,
        List<String> springLikePaths, Map<String, String> contentIdAndSpringLikePathMap,
        String template, Map<String, String> values);
}
