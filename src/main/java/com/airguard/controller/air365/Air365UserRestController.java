package com.airguard.controller.air365;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.airguard.service.app.Air365UserService;
import com.airguard.util.CommonConstant;
import com.airguard.util.RestApiCookieManageUtil;
import com.airguard.util.Sha256EncryptUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365UserRestController {

  private static final Logger logger = LoggerFactory.getLogger(Air365UserRestController.class);

  @Autowired
  private Air365UserService service;

  /*
   * AIR365 App, 로그인
   */
  @ApiOperation(value = "AIR365 App, 로그인", tags = "구, AIR 365 API")
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public HashMap<String, Object> appLogin(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String password =
        request.getParameter("password") == null ? "" : request.getParameter("password");
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType");
    String token = request.getParameter("token") == null ? "" : request.getParameter("token");

    if ("".equals(userId) || "".equals(password) || "".equals(userType))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("password", password);
    reqInfo.put("userType", userType);
    reqInfo.put("token", token);

    res = service.login(reqInfo, response);

    return res;
  }

  /*
   * AIR365 App, 로그아웃
   */
  @ApiOperation(value = "AIR365 App, 로그아웃", tags = "구, AIR 365 API")
  @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> appLogout(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String token = request.getParameter("token") == null ? "" : request.getParameter("token");

    if ("".equals(userId))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("token", token);

    res = service.appLogOut(reqInfo, response);

    return res;
  }

  /*
   * AIR365 App, 위치정보 찿기
   */
  @ApiOperation(value = "AIR365 App, 위치정보 찿기", tags = "구, AIR 365 API")
  @RequestMapping(value = "/find/region", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> findRegionInfo(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat");
    String lon = request.getParameter("lon") == null ? "" : request.getParameter("lon");

    if ("".equals(lat) || "".equals(lon))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("lat", lat);
    reqInfo.put("lon", lon);

    try {

      res = service.findRegionInfo(reqInfo);

    } catch (Exception e) {
      throw new RuntimeException();
    }

    return res;
  }

  /*
   * AIR365 App, 아이디 체크
   */
  @ApiOperation(value = "AIR365 App, 아이디 체크", tags = "구, AIR 365 API")
  @RequestMapping(value = "/check/userId", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> checkUserId(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType");

    if ("".equals(userId) || "".equals(userType))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("userType", userType);

    res = service.checkUserId(reqInfo);

    return res;
  }

  /*
   * AIR365 App, SMS 난수 발송
   */
  @ApiOperation(value = "AIR365 App, SMS 난수 발송", tags = "구, AIR 365 API")
  @RequestMapping(value = "/sms/send", method = RequestMethod.POST)
  public HashMap<String, Object> sendSMSData(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    String phoneNumber =
        request.getParameter("phoneNumber") == null ? "" : request.getParameter("phoneNumber");

    if ("".equals(phoneNumber))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));

    res = service.sendSMSData(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 회원 정보 조회
   */
  @ApiOperation(value = "AIR365 App, 회원 정보 조회", tags = "구, AIR 365 API")
  @RequestMapping(value = "/get/user", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> selectUserInfo(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request))
      throw new CookieAuthMatchException();

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType");

    if ("admin".equals(userType))
      if (!RestApiCookieManageUtil.adminCookieCheck(request))
        throw new CookieAuthMatchException();

    if ("".equals(userId) || "".equals(userType))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("userType", userType);

    res = service.selectUserInfo(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 회원 정보 일괄 조회
   */
  @ApiOperation(value = "AIR365 App, 회원 정보 일괄 조회", tags = "구, AIR 365 API")
  @RequestMapping(value = "/get/userAll", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> selectUserInfoAll(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request))
      throw new CookieAuthMatchException();

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType");

    if ("admin".equals(userType))
      if (!RestApiCookieManageUtil.adminCookieCheck(request))
        throw new CookieAuthMatchException();

    if ("".equals(userType))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userType", userType);

    res = service.selectUserInfoAll(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 회원 가입
   */
  @ApiOperation(value = "AIR365 App, 회원 가입", tags = "구, AIR 365 API")
  @RequestMapping(value = "/insert/user", method = RequestMethod.POST)
  public HashMap<String, Object> insertUserInfo(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String password =
        request.getParameter("password") == null ? "" : request.getParameter("password");
    String name = request.getParameter("name") == null ? "" : request.getParameter("name");
    String phoneNumber =
        request.getParameter("phoneNumber") == null ? "" : request.getParameter("phoneNumber");
    String region = request.getParameter("region") == null ? "" : request.getParameter("region");
    String regionName =
        request.getParameter("regionName") == null ? "" : request.getParameter("regionName");
    String email = request.getParameter("email") == null ? "" : request.getParameter("email");

    if ("".equals(userId) || "".equals(password) || "".equals(name) || "".equals(phoneNumber)
        || "".equals(region) || "".equals(regionName))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("userPw", Sha256EncryptUtil.ShaEncoder(password));
    reqInfo.put("userName", name);
    reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));
    reqInfo.put("region", region);
    reqInfo.put("regionName", regionName);
    reqInfo.put("userEmail", email);
    reqInfo.put("telephone", "");

    res = service.insertUserInfo(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 회원정보 수정
   */
  @ApiOperation(value = "AIR365 App, 회원정보 수정", tags = "구, AIR 365 API")
  @RequestMapping(value = "/update/user", method = {RequestMethod.PUT, RequestMethod.POST})
  public HashMap<String, Object> updateUserInfo(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request))
      throw new CookieAuthMatchException();

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String password =
        request.getParameter("password") == null ? null : request.getParameter("password");
    String name = request.getParameter("name") == null ? null : request.getParameter("name");
    String phoneNumber =
        request.getParameter("phoneNumber") == null ? null : request.getParameter("phoneNumber");
    String region = request.getParameter("region") == null ? null : request.getParameter("region");
    String regionName =
        request.getParameter("regionName") == null ? null : request.getParameter("regionName");
    String email = request.getParameter("email") == null ? null : request.getParameter("email");

    if ("".equals(userId))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("userPw", password == null ? null : Sha256EncryptUtil.ShaEncoder(password));
    reqInfo.put("userName", name);
    reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));
    reqInfo.put("region", region);
    reqInfo.put("regionName", regionName);
    reqInfo.put("userEmail", email);

    res = service.updateUserInfo(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 비밀번호 재설정
   */
  @ApiOperation(value = "AIR365 App, 비밀번호 재설정", tags = "구, AIR 365 API")
  @RequestMapping(value = "/update/password", method = {RequestMethod.PUT, RequestMethod.POST})
  public HashMap<String, Object> updateUserPassword(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String password =
        request.getParameter("password") == null ? null : request.getParameter("password");

    if ("".equals(userId) || "".equals(password))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);
    reqInfo.put("userPw", password == null ? null : Sha256EncryptUtil.ShaEncoder(password));

    res = service.updateUserInfo(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 회원 탈퇴
   */
  @ApiOperation(value = "AIR365 App, 회원 탈퇴", tags = "구, AIR 365 API")
  @RequestMapping(value = "/delete/user", method = {RequestMethod.DELETE, RequestMethod.POST})
  public HashMap<String, Object> deleteUserInfo(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request))
      throw new CookieAuthMatchException();

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");

    if ("".equals(userId))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userId", userId);

    res = service.deleteUserInfo(userId);

    return res;
  }

  /*
   * AIR365 App, 아이디 찿기
   */
  @ApiOperation(value = "AIR365 App, 아이디 찿기", tags = "구, AIR 365 API")
  @RequestMapping(value = "/find/userId", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> findUserId(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    String userName =
        request.getParameter("userName") == null ? "" : request.getParameter("userName");
    String phoneNumber =
        request.getParameter("phoneNumber") == null ? "" : request.getParameter("phoneNumber");

    if ("".equals(userName) || "".equals(phoneNumber))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("userName", userName);
    reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));

    res = service.findUserId(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 관리자 대시보드 조회 (관리자 이용)
   */
  @ApiOperation(value = "AIR365 App, 관리자 대시보드 조회 (관리자 이용)", tags = "구, AIR 365 API")
  @RequestMapping(value = "/statistics/data", method = RequestMethod.GET)
  public HashMap<String, Object> getStatisticsData(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.adminCookieCheck(request))
      throw new CookieAuthMatchException();

    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

    res = service.getAdminStatisticsData();

    return res;
  }

  /* ================================================ */

  private static class CookieAuthMatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public CookieAuthMatchException() {
      super();
    }
  }

  private static class RequestMatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestMatchException() {
      super();
    }
  }

  @ExceptionHandler(value = CookieAuthMatchException.class)
  public HashMap<String, Object> handleCookieAuthMatchException() {
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.COOKIE_EX_MSG);
    res.put("errorCode", 8L);
    res.put("result", 0);
    return res;
  }

  @ExceptionHandler(value = RequestMatchException.class)
  public HashMap<String, Object> handleRequestMatchException() {
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.WRONG_PARAM_EX_MSG);
    res.put("errorCode", 90L);
    res.put("result", 0);
    return res;
  }

  @ExceptionHandler(value = NullPointerException.class)
  public HashMap<String, Object> handleNullPointerException() {
    logger.error(CommonConstant.NULL_PARAM_EX_MSG);
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.NULL_PARAM_EX_MSG);
    res.put("errorCode", 91L);
    res.put("result", 0);
    return res;
  }

  @ExceptionHandler(value = DuplicateKeyException.class)
  public HashMap<String, Object> handleDuplicateKeyException() {
    logger.error(CommonConstant.DUPLICATE_EX_MSG);
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.DUPLICATE_EX_MSG);
    res.put("errorCode", 93L);
    res.put("result", 0);
    return res;
  }

  @ExceptionHandler(value = SQLException.class)
  public HashMap<String, Object> handleSQLException() {
    logger.error(CommonConstant.SQL_EX_MSG);
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.SQL_EX_MSG);
    res.put("errorCode", 92L);
    res.put("result", 0);
    return res;
  }

  @ExceptionHandler(value = RuntimeException.class)
  public HashMap<String, Object> handleRuntimeException() {
    logger.error(CommonConstant.RUNTIME_EX_MSG);
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.RUNTIME_EX_MSG);
    res.put("errorCode", 9L);
    res.put("result", 0);
    return res;
  }

  @ExceptionHandler(value = Exception.class)
  public HashMap<String, Object> handleException() {
    logger.error(CommonConstant.SERVER_EX_MSG);
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.SERVER_EX_MSG);
    res.put("errorCode", 99L);
    res.put("result", 0);
    return res;
  }
}
