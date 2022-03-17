package com.airguard.controller.air365.v2;

import com.airguard.util.AES256Util;
import com.airguard.exception.CookieAuthException;
import com.airguard.exception.ParameterException;
import com.airguard.service.app.v2.Air365StationV2Service;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.AES256Util;
import com.airguard.util.CommonConstant;
import com.airguard.util.RestApiCookieManageUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365_V2,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365StationV2RestController {

  @Autowired
  private Air365StationV2Service service;

  @Autowired
  private PlatformService platformService;

  @ApiOperation(value = "장비 목록 조회", tags = "AIR365, 프로젝트")
  @RequestMapping(value = "/station/list", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> selectStationList(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String userType =
            request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.ILLEGAL_ID_PW_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else {
      res = service.selectStationList(userId, userType);
    }

    return res;
  }

  @ApiOperation(value = "장비 연동 추가", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "stationSerial", value = "스테이션 번호", required = true),
          @ApiImplicitParam(name = "stationName", value = "스테이션 명", required = true),
          @ApiImplicitParam(name = "lat", value = "설치 지역 위도", required = true),
          @ApiImplicitParam(name = "lon", value = "설치 지역 경도", required = true)
  })
  @RequestMapping(value = "/station/insert", method = RequestMethod.POST)
  public HashMap<String, Object> insertStation(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String serial = request.getParameter("stationSerial") == null ? ""
            : request.getParameter("stationSerial");
    String stationName = request.getParameter("stationName") == null ? ""
            : request.getParameter("stationName");
    String lon = request.getParameter("lon") == null ? "" : request.getParameter("lon");
    String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat");

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      stationName = AES256Util.decrypt(stationName.replace(" ","+"));
      lon = AES256Util.decrypt(lon.replace(" ","+"));
      lat = AES256Util.decrypt(lat.replace(" ","+"));
    }


    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else if ("".equals(lon) || "".equals(lat)) {
      throw new ParameterException(ParameterException.NULL_LAT_LON_PARAMETER_EXCEPTION);
    } else if ("".equals(stationName)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else if (serial.length() >= 15) {
      throw new ParameterException(ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION);
    } else if (!lat.concat(",").concat(lon).matches(CommonConstant.REG_LAT_LON)) {
      throw new ParameterException(ParameterException.ILLEGAL_LAT_LON_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, Object> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("serial", serial);
      reqInfo.put("stationName", stationName);
      reqInfo.put("lon", lon);
      reqInfo.put("lat", lat);

      if(encoding){
        res = service.insertStationEncodeVersion(reqInfo);
      }else {
        res = service.insertStation(reqInfo);
      }
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());
    }

    return res;
  }

  @ApiOperation(value = "장비 연동 해제", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "stationSerial", value = "스테이션 번호", required = true)
  })
  @RequestMapping(value = "/station/delete", method = {RequestMethod.DELETE, RequestMethod.POST})
  public HashMap<String, Object> deleteStation(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String serial = request.getParameter("stationSerial") == null ? ""
            : request.getParameter("stationSerial");

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      serial = AES256Util.decrypt(serial.replace(" ","+"));
    }

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else {
      res = service.deleteStation(serial);

      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());
    }

    return res;
  }

  @ApiOperation(value = "장비 연동 정보 수정", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "stationSerial", value = "스테이션 번호", required = true),
          @ApiImplicitParam(name = "stationName", value = "스테이션 명"),
          @ApiImplicitParam(name = "lat", value = "설치 지역 위도"),
          @ApiImplicitParam(name = "lon", value = "설치 지역 경도")
  })
  @RequestMapping(value = "/station/update", method = {RequestMethod.PUT, RequestMethod.POST})
  public HashMap<String, Object> updateStationPosition(HttpServletRequest request)
          throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String serial = request.getParameter("stationSerial") == null ? ""
            : request.getParameter("stationSerial");
    String stationName = request.getParameter("stationName") == null ? null
            : request.getParameter("stationName");
    String lon = request.getParameter("lon") == null ? null : request.getParameter("lon");
    String lat = request.getParameter("lat") == null ? null : request.getParameter("lat");
    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId =  AES256Util.decrypt(userId.replace(" ","+"));
      serial =  AES256Util.decrypt(serial.replace(" ","+"));
      stationName =  AES256Util.decrypt(stationName.replace(" ","+"));
      lat = AES256Util.decrypt(lat.replace(" ","+"));
      lon = AES256Util.decrypt(lon.replace(" ","+"));
    }

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else if ((lat == null && lon == null && stationName == null)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else if (lat != null && lon != null && !lat.concat(",").concat(lon)
            .matches(CommonConstant.REG_LAT_LON)) {
      throw new ParameterException(ParameterException.ILLEGAL_LAT_LON_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, Object> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("serial", serial);
      reqInfo.put("stationName", stationName);
      reqInfo.put("lon", lon);
      reqInfo.put("lat", lat);

      res = service.updateStation(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "VENT 장비 연동 추가", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "stationSerial", value = "스테이션 번호", required = true),
          @ApiImplicitParam(name = "ventSerial", value = "환기청정기 번호", required = true)
  })
  @RequestMapping(value = "/station/insert/vent", method = RequestMethod.POST)
  public HashMap<String, Object> insertVentConnect(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String serial = request.getParameter("stationSerial") == null ? ""
            : request.getParameter("stationSerial");
    String ventSerial =
            request.getParameter("ventSerial") == null ? "" : request.getParameter("ventSerial");

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      ventSerial = AES256Util.decrypt(ventSerial.replace(" ","+"));
    }

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(serial) || "".equals(ventSerial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, Object> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("serial", serial);
      reqInfo.put("ventSerial", ventSerial);

      res = service.insertVentConnect(reqInfo);

      platformService.postPlatformRequestConnect(serial,
              request.getLocalName());
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());
    }

    return res;
  }

  @ApiOperation(value = "VENT 장비 연동 삭제", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
          @ApiImplicitParam(name = "stationSerial", value = "스테이션 번호", required = true),
          @ApiImplicitParam(name = "ventSerial", value = "환기청정기 번호", required = true)
  })
  @RequestMapping(value = "/station/delete/vent",
          method = {RequestMethod.DELETE, RequestMethod.POST})
  public HashMap<String, Object> deleteVentConnect(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
            request.getParameter("userId") == null ? "" : request.getParameter("userId");
    String serial = request.getParameter("stationSerial") == null ? ""
            : request.getParameter("stationSerial");
    String ventSerial =
            request.getParameter("ventSerial") == null ? "" : request.getParameter("ventSerial");

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if(encoding){
      userId = AES256Util.decrypt(userId.replace(" ","+"));
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      ventSerial = AES256Util.decrypt(ventSerial.replace(" ","+"));
    }

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(serial) || "".equals(ventSerial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, Object> reqInfo = new HashMap<>();
      reqInfo.put("ventSerial", ventSerial);

      res = service.deleteVentConnect(reqInfo);

      platformService.postPlatformRequestConnect(serial,
              request.getLocalName());
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, request.getLocalName());
      platformService
              .publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
      platformService.publisherPlatform(platformService.userIdToGroupId(userId),
              CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());
    }

    return res;
  }
}
