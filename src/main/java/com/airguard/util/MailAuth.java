package com.airguard.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import org.springframework.stereotype.Component;

@Component
public class MailAuth extends Authenticator {

  PasswordAuthentication pa;

  public MailAuth() {
    String authId = "kw_khelp@daum.net";
    String authPw = "kweather1997!";

    pa = new PasswordAuthentication(authId, authPw);
  }

  public PasswordAuthentication getPasswordAuthentication() {
    return pa;
  }
}
