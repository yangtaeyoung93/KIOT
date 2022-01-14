package com.airguard.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.airguard.service.common.CheckService;

@Component
public class HandlerAuthInterceptor extends HandlerInterceptorAdapter {

  private static final Logger logger = LoggerFactory.getLogger(HandlerAuthInterceptor.class);

  @Autowired
  public CheckService service;

  @Override
  public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
      @NotNull Object obj) throws Exception {

    logger.info("Call API : {}", request.getRequestURL().toString());
    Cookie[] cookies = request.getCookies();

    try {

      for (Cookie c : cookies) {
        if ((c.getName().equals("ADMIN_AUTH"))) {
          logger.info("==== COOKIE AUTH : {}", c.getName());
          return true;
        }
        if ((c.getName().equals("MEMBER_AUTH"))) {
          logger.info("==== COOKIE AUTH : {}", c.getName());
          return true;
        }
        if ((c.getName().equals("GROUP_AUTH"))) {
          logger.info("==== COOKIE AUTH : {}", c.getName());
          return true;
        }
        if ((c.getName().equals("AIR365_ADMIN"))) {
          logger.info("==== COOKIE AUTH : {}", c.getName());
          return true;
        }
      }

    } catch (Exception e) {
      logger.error("Exception, Browser no Auth");
      response.sendRedirect("/");
      return false;
    }

    response.sendRedirect("/");
    return false;
  }
}
