package com.airguard.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MailSendUtil {

  static final Logger logger = LoggerFactory.getLogger(MailSendUtil.class);

  public static int mailSend(String email, String subject, String content) {
    int result;
    Properties prop = System.getProperties();
    prop.put("mail.smtp.host", "smtp.daum.net");
    prop.put("mail.smtp.port", "465");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true");
    prop.put("mail.smtp.ssl.enable", "true");
    prop.put("mail.smtp.ssl.trust", "smtp.daum.net");

    Authenticator auth = new MailAuth();

    Session session = Session.getDefaultInstance(prop, auth);

    MimeMessage msg = new MimeMessage(session);

    try {

      msg.setFrom(new InternetAddress("kw_khelp@daum.net", "케이웨더 Air365"));
      InternetAddress to = new InternetAddress(email);
      msg.setRecipient(Message.RecipientType.TO, to);
      msg.setSubject(subject, "UTF-8");
      msg.setContent(content, "text/html; charset=euc-kr"); // HTML 형식

      Transport.send(msg);
      result = 1;

    } catch (AddressException ae) {
      result = 0;
      logger.error("AddressException : " + ae.getMessage());
    } catch (MessagingException me) {
      result = 0;
      logger.error("MessagingException : " + me.getMessage());
    } catch (UnsupportedEncodingException e) {
      result = 0;
      logger.error("UnsupportedEncodingException : " + e.getMessage());
    }

    return result;
  }
}
