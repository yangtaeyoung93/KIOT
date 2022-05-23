package com.airguard.controller.air365;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.airguard.util.NotifiTimeCheckUtil;
import com.airguard.util.RedisManageUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.airguard.service.app.Air365PushService;
import com.airguard.util.CommonConstant;
import com.airguard.util.RestApiCookieManageUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365, produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365PushRestController {

  private static final Logger logger = LoggerFactory.getLogger(Air365PushRestController.class);

  @Autowired
  private Air365PushService service;

  @Autowired
  private RedisManageUtil redisUtil;

  /*
   * AIR365 App, 알림 수신 설정 
   */
  @ApiOperation(value = "AIR365 App, 알림 수신 설정 ", tags = "구, AIR 365 API")
  @RequestMapping(value = "/push/control", method = RequestMethod.POST)
  public HashMap<String, Object> pushControlElement(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res;

    if (!RestApiCookieManageUtil.userCookieCheck(request))
      throw new CookieAuthMatchException();

    String token = request.getParameter("token") == null ? "" : request.getParameter("token");
    String target = request.getParameter("target") == null ? "all" : request.getParameter("target");
    String deviceType = request.getParameter("deviceType") == null ? "all" : request.getParameter("deviceType");

    String pm10Flag = request.getParameter("pm10") == null ? "0" : request.getParameter("pm10");
    String pm25Flag = request.getParameter("pm25") == null ? "0" : request.getParameter("pm25");
    String co2Flag = request.getParameter("co2") == null ? "0" : request.getParameter("co2");
    String vocs5Flag = request.getParameter("vocs") == null ? "0" : request.getParameter("vocs");
    String tempFlag = request.getParameter("temp") == null ? "0" : request.getParameter("temp");
    String humiFlag = request.getParameter("humi") == null ? "0" : request.getParameter("humi");

    String startTime = request.getParameter("startTime") == null ? "00:00" : request.getParameter("startTime");
    String endTime = request.getParameter("endTime") == null ? "23:59" : request.getParameter("endTime");

    if ("".equals(token) || ("".equals(target) && "".equals(deviceType)))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("token", token);
    reqInfo.put("target", target);
    reqInfo.put("deviceType", deviceType);
    reqInfo.put("pm10", pm10Flag);
    reqInfo.put("pm25", pm25Flag);
    reqInfo.put("co2", co2Flag);
    reqInfo.put("vocs", vocs5Flag);
    reqInfo.put("temp", tempFlag);
    reqInfo.put("humi", humiFlag);
    reqInfo.put("startTime", startTime);
    reqInfo.put("endTime", endTime);

    res = service.pushControl(reqInfo);

    return res;
  }

  /*
   * AIR365 App, 알림 수신 설정 조회 
   */
  @ApiOperation(value = "AIR365 App, 알림 수신 설정 조회 ", tags = "구, AIR 365 API")
  @RequestMapping(value = "/push/control/view", method = RequestMethod.POST)
  public HashMap<String, Object> pushControlView(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res;

    if (!RestApiCookieManageUtil.userCookieCheck(request))
      throw new CookieAuthMatchException();

    String token = request.getParameter("token") == null ? "" : request.getParameter("token");
    String target = request.getParameter("target") == null ? "all" : request.getParameter("target");

    if ("".equals(token) || "".equals(target))
      throw new RequestMatchException();

    HashMap<String, String> reqInfo = new HashMap<String, String>();
    reqInfo.put("token", token);
    reqInfo.put("target", target);

    res = service.pushControlView(reqInfo);

    return res;
  }

  @GetMapping(value = "/get/redis")
  public JSONObject getFlagList(List<String> memberTokenList, String userId, String serialNumber) throws Exception{
    logger.info("memberTokenList ={}",memberTokenList);
    logger.info("userId ={}",userId);
    logger.info("serialNumber ={}",serialNumber);
    List<String> filterTokenList = new ArrayList<String>();
    String fcmReceiveFlagStr;
    tokenLoop: for (String tokenInfo : memberTokenList) {

      fcmReceiveFlagStr = redisUtil.getRedisData(
              new StringBuilder("FLAG_")
                      .append(userId)
                      .append("_")
                      .append(tokenInfo)
                      .append("_")
                      .append(serialNumber)
                      .toString()).toString();

      if (!"NA".equals(fcmReceiveFlagStr)) {
        JSONObject fcmReceiveControlData = new JSONObject(fcmReceiveFlagStr);

        if (0 == (Integer) fcmReceiveControlData.get("filter_alarm"))
          continue tokenLoop;

        if (!NotifiTimeCheckUtil.isNotifiTimeRangeCheck(fcmReceiveControlData.get("timeFlag").toString()))
          continue tokenLoop;
      }
      filterTokenList.add(tokenInfo);
    }

    JSONObject jb = new JSONObject();
    jb.put("getFilterTokenList", jb);

    return jb;
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
  public HashMap<String, Object> handleReuestMatchException() {
    HashMap<String, Object> res = new HashMap<String, Object>();
    res.put("message", CommonConstant.NULL_PARAM_EX_MSG);
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

