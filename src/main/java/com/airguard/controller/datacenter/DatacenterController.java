package com.airguard.controller.datacenter;

import com.airguard.util.CommonConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(CommonConstant.URL_DATACENTER)
public class DatacenterController {

  @RequestMapping(value = "/did")
  public String dataCenterDid(HttpServletRequest request) {
    if (request.getCookies() == null)
      return "redirect:/";
    for (Cookie c : request.getCookies()) {
      if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")
          || c.getName().equals("MEMBER_AUTH")) {
        return "/kidc/dataCenterDid";
      }
    }

    return "redirect:/";
  }

  @RequestMapping(value = "/list")
  public String dataCenterList(HttpServletRequest request) {
    if (request.getCookies() == null)
      return "redirect:/";
    for (Cookie c : request.getCookies()) {
      if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")
          || c.getName().equals("MEMBER_AUTH")) {
        return "/kidc/dataCenterList";
      }
    }

    return "redirect:/";
  }

  @RequestMapping(value = "/popup")
  public String dataCenterPopup(HttpServletRequest request) {
    if (request.getCookies() == null)
      return "redirect:/";
    for (Cookie c : request.getCookies())
      if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")
          || c.getName().equals("MEMBER_AUTH"))
        return "/kidc/dataCenterPopup";

    return "redirect:/";
  }

  @RequestMapping(value = "/vent")
  public String dataCenterVentPopup(HttpServletRequest request) {
    if (request.getCookies() == null)
      return "redirect:/";
    for (Cookie c : request.getCookies()) {
      if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")
          || c.getName().equals("MEMBER_AUTH")) {
        return "/kidc/dataCenterVentPopup";
      }
    }

    return "redirect:/";
  }

  @RequestMapping(value = "/custom/popup")
  public String dataCenterCustomPopup() {
    return "/kidc/dataCenterPopup";
  }

  @RequestMapping(value = "/logout")
  public String logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("_USER_AUTH", null);
    Cookie grCookie = new Cookie("GROUP_AUTH", null);
    Cookie mbCookie = new Cookie("MEMBER_AUTH", null);

    cookie.setMaxAge(0);
    grCookie.setMaxAge(0);
    mbCookie.setMaxAge(0);

    cookie.setPath("/");
    grCookie.setPath("/");
    mbCookie.setPath("/");

    cookie.setDomain("kweather.co.kr");
    grCookie.setDomain("kweather.co.kr");
    mbCookie.setDomain("kweather.co.kr");

    response.addCookie(cookie);
    response.addCookie(grCookie);
    response.addCookie(mbCookie);

    return "redirect:/";
  }
}
