package com.airguard.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RestApiCookieManageUtil {

  private static final String[] COOKIE_NAMES =
      {"AIR365_ADMIN", "ADMIN_AUTH", "MEMBER_AUTH", "GROUP_AUTH"};
  private static final String[] COOKIE_NAMES_ADMIN = {"AIR365_ADMIN", "ADMIN_AUTH"};

  private static final Logger logger = LoggerFactory.getLogger(RestApiCookieManageUtil.class);

  public static void makeAuthCookie(String profile, String userName, String userType,
      HttpServletResponse response) throws UnsupportedEncodingException, GeneralSecurityException {
    String cKey = "";
    switch (userType) {
      case "admin":
        cKey = "AIR365_ADMIN";
        break;
      case "member":
        cKey = "MEMBER_AUTH";
        break;
      case "group":
        cKey = "GROUP_AUTH";
        break;
    }

    Cookie cookie = new Cookie(cKey, AES256Util.encrypt(userName));

    logger.error("profile :: {}", profile);

    if ("prod".equals(profile)) {
      cookie.setDomain("kweather.co.kr");
    }

    cookie.setPath("/");

    response.addCookie(cookie);
  }

  public static boolean adminCookieCheck(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    try {

      for (Cookie c : cookies) {
        if (Arrays.asList(COOKIE_NAMES_ADMIN).contains(c.getName())) {
          return true;
        }
      }

    } catch (Exception e) {
      return false;
    }

    return false;
  }

  public static boolean userCookieCheck(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    try {

      for (Cookie c : cookies) {
        if (Arrays.asList(COOKIE_NAMES).contains(c.getName())) {
          return true;
        }
      }

    } catch (Exception e) {
      return false;
    }

    return false;
  }

  public static void deleteCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("_USER_AUTH", null);
    Cookie adCookie = new Cookie("ADMIN_AUTH", null);
    Cookie mbCookie = new Cookie("MEMBER_AUTH", null);
    Cookie grCookie = new Cookie("GROUP_AUTH", null);
    Cookie airAdCookie = new Cookie("AIR365_ADMIN", null);

    cookie.setMaxAge(0);
    adCookie.setMaxAge(0);
    grCookie.setMaxAge(0);
    mbCookie.setMaxAge(0);
    airAdCookie.setMaxAge(0);

    response.addCookie(cookie);
    response.addCookie(adCookie);
    response.addCookie(grCookie);
    response.addCookie(mbCookie);
    response.addCookie(airAdCookie);

  }
}
