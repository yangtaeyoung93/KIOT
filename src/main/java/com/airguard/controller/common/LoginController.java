package com.airguard.controller.common;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.airguard.model.common.Admin;
import com.airguard.model.common.ResLoginModel;
import com.airguard.model.system.Group;
import com.airguard.model.system.Member;
import com.airguard.service.common.AdminService;
import com.airguard.service.system.GroupService;
import com.airguard.service.system.MemberService;
import com.airguard.util.AES256Util;
import com.airguard.util.Sha256EncryptUtil;
import io.swagger.annotations.ApiOperation;

@Controller
public class LoginController {

  Logger logger = LoggerFactory.getLogger(LoginController.class);

  @Autowired
  private AdminService adminService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private GroupService groupService;

  /**
   * 로그인 페이지
   *
   * @param model model
   * @param error error
   * @return String "login"
   */
  @ApiOperation(value = "로그인 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/")
  public String main(Model model, @RequestParam(value = "error", required = false) String error) {
    model.addAttribute("loginError", error);
    return "thymeleaf/login";
  }

  @ApiOperation(value = "관리자 로그인 페이지", tags = "웹 페이지 Url")
  @RequestMapping(value = "/adminLogin")
  public String adminLoginPage(Model model,
      @RequestParam(value = "error", required = false) String error) {
    model.addAttribute("loginError", error);
    return "thymeleaf/login2";
  }

  @ApiOperation(value = "로그인 페이지 (Error)", tags = "웹 페이지 Url")
  @RequestMapping(value = "/login")
  public String login(Model model, @RequestParam(value = "error", required = false) String error) {
    model.addAttribute("loginError", error);
    return "thymeleaf/login";
  }

  @ApiOperation(value = "로그인 API", tags = "데이터 센터 공용 API")
  @RequestMapping(value = "/api/login", method = RequestMethod.GET)
  public ResponseEntity<Object> apiLogin(HttpServletRequest request, HttpServletResponse response) {
    Map<String, Object> result = new HashMap<>();
    final String paramUsername = request.getParameter("username");
    final String paramPassword = request.getParameter("password");
    final String paramLoginAuth = request.getParameter("loginAuth");
    long idx;

    logger.info("****************************************");
    logger.info("****************************************");
    logger.info("************ AUTH : " + paramLoginAuth + " ************");
    logger.info("************ ID : " + paramUsername + " ************");
    logger.info("************ PASSWORD : " + paramPassword + " ************");

    try {

      Cookie authCookie = new Cookie("_USER_AUTH", paramLoginAuth);
      response.addCookie(authCookie);

      if (paramLoginAuth.equals("admin")) {
        Admin pAdmin = new Admin();
        pAdmin.setUserId(paramUsername);
        pAdmin.setUserPw(Sha256EncryptUtil.ShaEncoder(paramPassword));

        int checkCode = adminService.loginCheckAdminId(pAdmin);

        if (checkCode != 3) {
          result.put("resultCode", 0);
          result.put("checkCode", checkCode);
          return new ResponseEntity<>(result, HttpStatus.OK);
        }

        idx = adminService.findAdminIdx(paramUsername);
        Cookie cookie = new Cookie("ADMIN_AUTH", AES256Util.encrypt(paramUsername));
        cookie.setPath("/");
        response.addCookie(cookie);

        Admin adminDto = new Admin();
        adminDto.setUserId(pAdmin.getUserId());
        adminDto.setUseYn("Y");
        adminDto.setLoginIp(request.getHeader("X-Forwarded-For") == null ? request.getRemoteAddr()
            : request.getHeader("X-Forwarded-For"));
        adminDto.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        adminService.adminLoginInfoUpdate(adminDto);

      } else if (paramLoginAuth.equals("member")) {
        Member pMember = new Member();
        pMember.setUserId(paramUsername);
        pMember.setUserPw(Sha256EncryptUtil.ShaEncoder(paramPassword));

        int checkCode = memberService.loginCheckMemberId(pMember);

        if (checkCode != 3) {
          result.put("resultCode", 0);
          result.put("checkCode", checkCode);
          return new ResponseEntity<>(result, HttpStatus.OK);
        }

        idx = memberService.findMemberIdx(paramUsername);
        Cookie cookie = new Cookie("MEMBER_AUTH", AES256Util.encrypt(paramUsername));
        logger.info("MEMBER_AUTH :: " + AES256Util.encrypt(paramUsername));
        cookie.setPath("/");
        cookie.setDomain("kweather.co.kr");
        response.addCookie(cookie);

        Member member = memberService.findMemberByLoginId(paramUsername);
        Member memberDto = new Member();
        memberDto.setIdx(member.getIdx());
        memberDto.setUserId(member.getUserId());
        memberDto.setUseYn(member.getUseYn());

        memberDto.setLoginIp(request.getHeader("X-Forwarded-For") == null ? request.getRemoteAddr()
            : request.getHeader("X-Forwarded-For"));
        memberDto.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        memberService.memberLoginInfoUpdate(memberDto);

      } else {
        Group pGroup = new Group();
        pGroup.setGroupId(paramUsername);
        pGroup.setGroupPw(Sha256EncryptUtil.ShaEncoder(paramPassword));

        int checkCode = groupService.loginCheckGroupId(pGroup);

        if (checkCode != 3) {
          result.put("resultCode", 0);
          result.put("checkCode", checkCode);
          return new ResponseEntity<>(result, HttpStatus.OK);
        }

        idx = groupService.findGroupIdx(paramUsername);
        Cookie cookie = new Cookie("GROUP_AUTH", AES256Util.encrypt(paramUsername));
        logger.info("GROUP_AUTH :: " + AES256Util.encrypt(paramUsername));
        cookie.setPath("/");
        cookie.setDomain("kweather.co.kr");
        response.addCookie(cookie);
      }

      result.put("idx", idx);
      result.put("resultCode", 1L);

    } catch (Exception e) {
      result.put("resultCode", 0);
      result.put("checkCode", 9L);
    }

    logger.info("****************************************");

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ApiOperation(value = "로그인 API", tags = "데이터 센터 공용 API")
  @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
  public String doLogin(HttpServletRequest request, HttpServletResponse response)
      throws LoginErrorException, UnsupportedEncodingException, GeneralSecurityException {
    logger.info("************ LOGIN ************");

    final String username = request.getParameter("username");
    final String password = request.getParameter("password");
    final String loginAuth = request.getParameter("loginAuth");

    logger.info("************ AUTH : " + loginAuth + " ************");
    logger.info("************ ID : " + username + " ************");

    Cookie authCookie = new Cookie("_USER_AUTH", loginAuth);
    response.addCookie(authCookie);

    if (loginAuth.equals("admin")) {
      Admin pAdmin = new Admin();
      pAdmin.setUserId(username);
      pAdmin.setUserPw(Sha256EncryptUtil.ShaEncoder(password));
      int checkCode = adminService.loginCheckAdminId(pAdmin);

      if (checkCode == 1) {
        return "redirect:/adminLogin?error=1";
      } else if (checkCode == 2) {
        return "redirect:/adminLogin?error=2";
      }
      Admin admin = adminService.findAdminByLoginId(username);

      if (admin == null || !admin.getUserPw().equals(Sha256EncryptUtil.ShaEncoder(password))) {
        return "redirect:/adminLogin";
      }

      Cookie cookie = new Cookie("ADMIN_AUTH", AES256Util.encrypt(username));
      Cookie air365Cookie = new Cookie("AIR365_ADMIN", AES256Util.encrypt(username));

      cookie.setPath("/");
      air365Cookie.setPath("/");

      air365Cookie.setDomain("kweather.co.kr");

      response.addCookie(cookie);
      response.addCookie(air365Cookie);

      Admin adminDto = new Admin();
      adminDto.setUserId(admin.getUserId());
      adminDto.setUseYn(admin.getUseYn());

      String connectIp = request.getHeader("X-Forwarded-For");
        if (connectIp == null) {
            connectIp = request.getRemoteAddr();
        }

      adminDto.setLoginIp(connectIp);
      adminDto.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      adminService.adminLoginInfoUpdate(adminDto);

    } else if (loginAuth.equals("member")) {
      Member pMember = new Member();
      pMember.setUserId(username);
      pMember.setUserPw(Sha256EncryptUtil.ShaEncoder(password));
      int checkCode = memberService.loginCheckMemberId(pMember);
      Member member = memberService.findMemberByLoginId(username);

      if (checkCode == 1) {
        memberService.updateMemberLoginCount(member.getUserId(),0);
        return "redirect:/?error=1";
      } else if (checkCode == 2) {
        memberService.updateMemberLoginCount(member.getUserId(),0);
        return "redirect:/?error=2";
      } else if (checkCode == 4) {
        memberService.updateMemberLoginCount(member.getUserId(),0);
        return "redirect:/?error=4";
      } else if (checkCode == 5) {
        memberService.updateMemberLoginCount(member.getUserId(),0);
        return "redirect:/?error=5";
      }


      if (member == null || !member.getUserPw().equals(Sha256EncryptUtil.ShaEncoder(password))) {
        return "redirect:/";
      }

      Cookie cookie = new Cookie("MEMBER_AUTH", AES256Util.encrypt(member.getUserId()));
      cookie.setPath("/");
      cookie.setDomain("kweather.co.kr");
      response.addCookie(cookie);

      Member memberDto = new Member();
      memberDto.setIdx(member.getIdx());
      memberDto.setUserId(member.getUserId());
      memberDto.setUseYn(member.getUseYn());

      String connectIp = request.getHeader("X-Forwarded-For");
      if (connectIp == null) {
        connectIp = request.getRemoteAddr();
      }

      memberDto.setLoginIp(connectIp);
      memberDto.setLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      memberService.memberLoginInfoUpdate(memberDto);
      memberService.updateMemberLoginCount(member.getUserId(),1);

    } else {
      Group pGroup = new Group();
      pGroup.setGroupId(username);
      pGroup.setGroupPw(Sha256EncryptUtil.ShaEncoder(password));
      int checkCode = groupService.loginCheckGroupId(pGroup);

      if (checkCode == 1) {
        return "redirect:/?error=1";
      } else if (checkCode == 2) {
        return "redirect:/?error=2";
      }
      Group group = groupService.findGroupByLoginId(username);

        if (group == null || !group.getGroupPw().equals(Sha256EncryptUtil.ShaEncoder(password))) {
            return "redirect:/";
        }

      Cookie cookie = new Cookie("GROUP_AUTH", AES256Util.encrypt(group.getGroupId()));
      cookie.setPath("/");
      cookie.setDomain("kweather.co.kr");
      response.addCookie(cookie);

    }

    return "redirect:/loginSuccess";
  }

  /**
   * 로그인 성공 처리
   *
   * @param request request
   * @return String "redirect:/monitoring/dashboard"
   */
  @ApiOperation(value = "로그인 성공 시 처리", tags = "웹 페이지 Url")
  @RequestMapping(value = "/loginSuccess")
  public String loginSuccess(HttpServletRequest request) {
    String authCookieValue = "";
      for (Cookie c : request.getCookies()) {
          if (c.getName().equals("_USER_AUTH")) {
              authCookieValue = c.getValue();
          }
      }

    if (authCookieValue.equals("admin")) {
      return "redirect:/dashboard/receive";
    }

    if (authCookieValue.equals("group")) {
      return "redirect:/datacenter/list";
    } else if (authCookieValue.equals("member")) {
      return "redirect:/datacenter/did";
    }

    return "redirect:/";
  }

  @ApiOperation(value = "개인 사용자 로그인 성공 시 처리", tags = "데이터 센터 공용 API")
  @RequestMapping(value = "/memberLoginSuccess")
  public ResponseEntity<Object> memberLoginSuccess() {
    ResLoginModel res = new ResLoginModel();

    try {
      res.setResult(1L);
      res.setMsg("Member User Login Success");

    } catch (Exception e) {
      res.setResult(0L);
      res.setMsg(e.getMessage());
    }

    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @ApiOperation(value = "그룹 사용자 로그인 성공 시 처리", tags = "데이터 센터 공용 API")
  @RequestMapping(value = "/groupLoginSuccess")
  public ResponseEntity<Object> groupLoginSuccess() {
    ResLoginModel res = new ResLoginModel();

    try {
      res.setResult(1L);
      res.setMsg("Group User Login Success");

    } catch (Exception e) {
      res.setResult(0L);
      res.setMsg(e.getMessage());
    }

    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  /**
   * 로그아웃 처리
   *
   * @return String "redirect:/logout"
   */
  @ApiOperation(value = "로그아웃 처리", tags = "데이터 센터 공용 API")
  @RequestMapping(value = "/logoutTry")
  public String logout(HttpServletResponse response) {
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

    cookie.setPath("/");
    adCookie.setPath("/");
    grCookie.setPath("/");
    mbCookie.setPath("/");
    airAdCookie.setPath("/");

    cookie.setDomain("kweather.co.kr");
    grCookie.setDomain("kweather.co.kr");
    mbCookie.setDomain("kweather.co.kr");
    airAdCookie.setDomain("kweather.co.kr");

    response.addCookie(cookie);
    response.addCookie(adCookie);
    response.addCookie(grCookie);
    response.addCookie(mbCookie);
    response.addCookie(airAdCookie);

    return "redirect:/";
  }

  private static class LoginErrorException extends Exception {

    public LoginErrorException(String msg) {
      super(msg);
    }
  }
}
