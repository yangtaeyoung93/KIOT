package com.airguard.controller.app;

import com.airguard.model.app.*;
import com.airguard.service.app.UserService;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(CommonConstant.URL_API_APP)
public class UserRestController {

  private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

  @Autowired
  private UserService service;

  @Autowired
  private PlatformService platformService;

  @ApiOperation(value = "회원 가입", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/adduser", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel addUser(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getId().isEmpty() || reqUser.getPw().isEmpty() || reqUser.getStation_no().isEmpty()
        || reqUser.getStation_name().isEmpty() || reqUser.getLat().isEmpty()
        || reqUser.getLon().isEmpty() || reqUser.getRegion().isEmpty()
        || reqUser.getRegion_name().isEmpty() || reqUser.getStation_shared().isEmpty()
        || reqUser.getDevice_type().isEmpty() || reqUser.getDevice_imei().isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setUserId(reqUser.getId());
    user.setUserPw(reqUser.getPw());
    user.setStationNo(reqUser.getStation_no());
    user.setStationName(reqUser.getStation_name());
    user.setLat(reqUser.getLat());
    user.setLon(reqUser.getLon());
    user.setRegion(reqUser.getRegion());
    user.setRegionName(reqUser.getRegion_name());
    user.setStationShared(reqUser.getStation_shared());
    user.setAppDeviceType(reqUser.getDevice_type());
    user.setAppDeviceImei(reqUser.getDevice_imei());

    ResponseModel res = service.addUser(user);

    if (res.getResult() == CommonConstant.R_FAIL_CODE) {
      return res;
    }

    String domain = request.getLocalName();
    String iaqSerial = user.getStationNo();
    String userId = user.getUserId();
    String groupId = service.getUserIdToGroupId(userId);

    platformService.postPlatformRequestConnect(iaqSerial, domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, true, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, false, domain);

    res.setDataUrl(request.getRequestURL().toString());

    return res;
  }

  @ApiOperation(value = "회원 탈퇴", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/deluser", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel delUser(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getId().isEmpty() || reqUser.getPw().isEmpty() || reqUser.getStation_no()
        .isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setUserId(reqUser.getId());
    user.setUserPw(reqUser.getPw());
    user.setStationNo(reqUser.getStation_no());

    ResponseModel res = service.delUser(user);
    if (res.getResult() == CommonConstant.R_FAIL_CODE) {
      return res;
    }

    String domain = request.getLocalName();
    String iaqSerial = user.getStationNo();
    String userId = user.getUserId();
    String groupId = service.getUserIdToGroupId(userId);

    platformService.postPlatformRequestConnect(iaqSerial, domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, true, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, false, domain);

    res.setDataUrl(request.getRequestURL().toString());

    return res;
  }

  @ApiOperation(value = "로그인", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/login", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = "application/xml")
  public ResponseLoginModel login(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getId().isEmpty() || reqUser.getPw().isEmpty() || reqUser.getDevice_type().isEmpty()
        || reqUser.getDevice_imei().isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setUserId(reqUser.getId());
    user.setUserPw(reqUser.getPw());
    user.setAppDeviceType(reqUser.getDevice_type());
    user.setAppDeviceImei(reqUser.getDevice_imei());

    ResponseLoginModel res = service.appLogin(user, request);
    res.setDataUrl(request.getRequestURL().toString());

    return res;
  }

  @ApiOperation(value = "로그인 (GROUP)", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/login/group", method = {RequestMethod.GET, RequestMethod.POST})
  public ResponseGroupLoginModel loginGroupXml(@ModelAttribute RequestModel reqUser,
      HttpServletRequest request) throws Exception {

    if (reqUser.getId().isEmpty() || reqUser.getPw().isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setUserId(reqUser.getId());
    user.setUserPw(reqUser.getPw());

    ResponseGroupLoginModel res = service.appGroupLogin(user, request);
    res.setDataUrl(request.getRequestURL().toString());

    return res;
  }

  @ApiOperation(value = "로그아웃", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel logout(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getStation_no().isEmpty() || reqUser.getDevice_type().isEmpty()
        || reqUser.getDevice_code().isEmpty() || reqUser.getDevice_imei().isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setStationNo(reqUser.getStation_no());
    user.setAppDeviceType(reqUser.getDevice_type());
    user.setAppDeviceCode(reqUser.getDevice_code());
    user.setAppDeviceImei(reqUser.getDevice_imei());

    ResponseModel res = service.appLogout(user);
    res.setDataUrl(request.getRequestURL().toString());
    return res;
  }

  @ApiOperation(value = "아이디 찿기", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/findId", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel findId(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getStation_no().isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setStationNo(reqUser.getStation_no());

    ResponseModel res = service.findId(user, request);
    res.setDataUrl(request.getRequestURL().toString());
    return res;
  }

  @ApiOperation(value = "비밀번호 찿기", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/findPwd", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel findPwd(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getId().isEmpty() || reqUser.getStation_no().isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setUserId(reqUser.getId());
    user.setStationNo(reqUser.getStation_no());

    ResponseModel res = service.findPwd(user, request);
    res.setDataUrl(request.getRequestURL().toString());
    return res;
  }

  @ApiOperation(value = "비밀번호 변경", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/chgPwd", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel chgPassword(@ModelAttribute RequestModel reqUser, HttpServletRequest request)
      throws Exception {

    if (reqUser.getId().isEmpty() || reqUser.getStation_no().isEmpty() || reqUser.getPw()
        .isEmpty()) {
      throw new Exception();
    }

    AppUser user = new AppUser();
    user.setUserId(reqUser.getId());
    user.setStationNo(reqUser.getStation_no());
    user.setUserPw(reqUser.getPw());

    ResponseModel res = service.chgPasswd(user, request);
    res.setDataUrl(request.getRequestURL().toString());
    return res;
  }

  @ApiOperation(value = "업데이트 확인용 API", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/update/check", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public Object updateCheck() throws Exception {

    Map<String, Object> res = new HashMap<>();
    res.put("result", 0);
    res.put("message", "Waiting for updates .");
    return res;
  }

  @ExceptionHandler(value = NullPointerException.class)
  public ExceptionModel handleNullPointerException() {
    logger.error(CommonConstant.NULL_TARGET_EX_MSG);
    ExceptionModel res = new ExceptionModel();
    res.setError_message(CommonConstant.NULL_TARGET_EX_MSG);
    res.setError_code(91L);
    res.setResult(0);
    return res;
  }

  @ExceptionHandler(value = SQLException.class)
  public ExceptionModel handleSQLException() {
    logger.error(CommonConstant.SQL_EX_MSG);
    ExceptionModel res = new ExceptionModel();
    res.setError_message(CommonConstant.SQL_EX_MSG);
    res.setError_code(92L);
    res.setResult(0);
    return res;
  }

  @ExceptionHandler(value = RuntimeException.class)
  public ExceptionModel handleRuntimeException() {
    logger.error(CommonConstant.RUNTIME_EX_MSG);
    ExceptionModel res = new ExceptionModel();
    res.setError_message(CommonConstant.RUNTIME_EX_MSG);
    res.setError_code(9L);
    res.setResult(0);
    return res;
  }

  @ExceptionHandler(value = Exception.class)
  public ExceptionModel handleException() {
    logger.error(CommonConstant.SERVER_EX_MSG);
    ExceptionModel res = new ExceptionModel();
    res.setError_message(CommonConstant.SERVER_EX_MSG);
    res.setError_code(99L);
    res.setResult(0);
    return res;
  }
}
