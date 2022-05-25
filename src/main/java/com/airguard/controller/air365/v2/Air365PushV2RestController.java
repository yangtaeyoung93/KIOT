package com.airguard.controller.air365.v2;

import com.airguard.exception.CookieAuthException;
import com.airguard.exception.ParameterException;
import com.airguard.exception.TokenAuthException;
import com.airguard.service.app.v2.Air365PushV2Service;
import com.airguard.util.AES256Util;
import com.airguard.util.CommonConstant;
import com.airguard.util.RestApiCookieManageUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Slf4j
@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365_V2, produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365PushV2RestController {

  @Autowired
  private Air365PushV2Service service;

  @ApiOperation(value = "푸시 알림 제어 설정", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "token", value = "구글 메시지 토큰", required = true),
          @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "member, group", required = true),
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "target", value = "스테이션 번호", required = true, defaultValue = "all"),
          @ApiImplicitParam(name = "stationType", value = "스테이션 유형", allowableValues = "IAQ, OAQ, all", required = true),
          @ApiImplicitParam(name = "pm10", value = "미세먼지", allowableValues = "1(수신), 0(미수신)"),
          @ApiImplicitParam(name = "pm25", value = "초미세먼지", allowableValues = "1(수신), 0(미수신)"),
          @ApiImplicitParam(name = "co2", value = "이산화탄소", allowableValues = "1(수신), 0(미수신)"),
          @ApiImplicitParam(name = "humi", value = "습도", allowableValues = "1(수신), 0(미수신)"),
          @ApiImplicitParam(name = "vocs", value = "휘발성유기화합물", allowableValues = "1(수신), 0(미수신)"),
          @ApiImplicitParam(name = "filterAlarm", value = "필터알람", allowableValues = "1(수신), 0(미수신)"),
          @ApiImplicitParam(name = "startTime", value = "매너모드 시작 시간", paramType = "hhmm"),
          @ApiImplicitParam(name = "endTime", value = "매너모드 종료 시간", paramType = "hhmm")
  })

  @RequestMapping(value = "/push/control", method = RequestMethod.POST)
  public HashMap<String, Object> pushControlElement(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res;

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    String token =
            request.getParameter("token") == null ? "" : request.getParameter("token");
    String userType =
            request.getParameter("userType") == null ? "" : request.getParameter("userType");
    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String deviceType = request.getParameter("stationType") == null ? ""
            : request.getParameter("stationType");
    String target =
            request.getParameter("target") == null ? "" : request.getParameter("target");

    String pm10Flag =
            request.getParameter("pm10") == null ? "0" : request.getParameter("pm10");
    String pm25Flag =
            request.getParameter("pm25") == null ? "0" : request.getParameter("pm25");
    String co2Flag = request.getParameter("co2") == null ? "0" : request.getParameter("co2");
    String vocs5Flag =
            request.getParameter("vocs") == null ? "0" : request.getParameter("vocs");
    String tempFlag =
            request.getParameter("temp") == null ? "0" : request.getParameter("temp");
    String humiFlag =
            request.getParameter("humi") == null ? "0" : request.getParameter("humi");
    String filterFlag = "0";

    if(request.getParameter("filterAlarm") != null && !request.getParameter("filterAlarm").equals("")){
      filterFlag = request.getParameter("filterAlarm");
    }
    String startTime = request.getParameter("startTime") == null ? "08:00"
            : request.getParameter("startTime");
    String endTime =
            request.getParameter("endTime") == null ? "20:00" : request.getParameter("endTime");

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      token = AES256Util.decrypt(token.replace(" ","+"));
      userType = AES256Util.decrypt(userType.replace(" ","+"));
      deviceType = AES256Util.decrypt(deviceType.replace(" ","+"));
      target = AES256Util.decrypt(target.replace(" ","+"));
      pm10Flag = AES256Util.decrypt(pm10Flag.replace(" ","+"));
      pm25Flag = AES256Util.decrypt(pm25Flag.replace(" ","+"));
      co2Flag = AES256Util.decrypt(co2Flag.replace(" ","+"));
      vocs5Flag = AES256Util.decrypt(vocs5Flag.replace(" ","+"));
      tempFlag = AES256Util.decrypt(tempFlag.replace(" ","+"));
      humiFlag = AES256Util.decrypt(humiFlag.replace(" ","+"));
      filterFlag = AES256Util.decrypt(filterFlag.replace(" ","+"));
      startTime = AES256Util.decrypt(startTime.replace(" ","+"));
      endTime = AES256Util.decrypt(endTime.replace(" ","+"));
    }

    if ("".equals(token)) {
      throw new TokenAuthException(TokenAuthException.TOKEN_AUTH_NONE_EXCEPTION);
    } else if ("".equals(target) || "".equals(deviceType)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else if ("".equals(userType) && "".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();

      reqInfo.put("token", token);
      reqInfo.put("userId", userId);
      reqInfo.put("userType", userType);
      reqInfo.put("target", target);
      reqInfo.put("deviceType", deviceType);
      reqInfo.put("pm10", pm10Flag);
      reqInfo.put("pm25", pm25Flag);
      reqInfo.put("co2", co2Flag);
      reqInfo.put("vocs", vocs5Flag);
      reqInfo.put("temp", tempFlag);
      reqInfo.put("humi", humiFlag);
      if(filterFlag.equals("") || filterFlag.equals(null)){
        filterFlag = "0";
      }
      reqInfo.put("filterAlarm", filterFlag);
      reqInfo.put("startTime", startTime);
      reqInfo.put("endTime", endTime);
      res = service.pushControl(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "푸시 알림 제어 설정 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "token", value = "구글 메시지 토큰", required = true),
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "target", value = "스테이션 번호", defaultValue = "all")
  })
  @RequestMapping(value = "/push/control/view", method = RequestMethod.POST)
  public HashMap<String, Object> pushControlView(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res;

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    String token =
            request.getParameter("token") == null ? "" : request.getParameter("token");
    String userType =
            request.getParameter("userType") == null ? "" : request.getParameter("userType");
    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String target =
            request.getParameter("target") == null ? "" : request.getParameter("target");
    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      token = AES256Util.decrypt(token.replace(" ","+"));
      userType = AES256Util.decrypt(userType.replace(" ","+"));
      target = AES256Util.decrypt(target.replace(" ","+"));
    }

    if ("".equals(token)) {
      throw new TokenAuthException(TokenAuthException.TOKEN_AUTH_NONE_EXCEPTION);
    } else if ("".equals(userType) || "".equals(userId) || "".equals(target)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("token", token);
      reqInfo.put("userType", userType);
      reqInfo.put("userId", userId);
      reqInfo.put("target", target);

      if(encoding){
        res = service.pushControlViewEncodeVersion(reqInfo);
      }else {
        res = service.pushControlView(reqInfo);
      }
    }

    return res;
  }

  @ApiOperation(value = "푸시 알림 이력 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "member, group", required = true),
          @ApiImplicitParam(name = "serial", value = "스테이션 번호", required = true),
          @ApiImplicitParam(name = "hisIdx", value = "조회 시작 번호", defaultValue = "0")
  })
  @RequestMapping(value = "/push/history/list", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> pushHistoryList(HttpServletRequest request) throws Exception {
    LinkedHashMap<String, Object> res;

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String userType =
            request.getParameter("userType") == null ? "" : request.getParameter("userType");
    String serial =
            request.getParameter("serial") == null ? "" : request.getParameter("serial");
    String hisIdx =
            request.getParameter("hisIdx") == null ? "0" : request.getParameter("hisIdx");

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      userType = AES256Util.decrypt(userType.replace(" ","+"));
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      hisIdx = AES256Util.decrypt(hisIdx.replace(" ","+"));

    }

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else if ("".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else if ("".equals(hisIdx)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("userType", userType);
      reqInfo.put("serial", serial);
      reqInfo.put("hisIdx", hisIdx);

      if(encoding){
        res = service.pushHistoryListEncodeVersion(reqInfo);
      }else {
        res = service.pushHistoryList(reqInfo);
      }
    }

    return res;
  }
}