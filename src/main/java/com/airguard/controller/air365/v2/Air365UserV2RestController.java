package com.airguard.controller.air365.v2;

import com.airguard.exception.CookieAuthException;
import com.airguard.exception.ParameterException;
import com.airguard.service.app.v2.Air365UserV2Service;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365_V2, produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365UserV2RestController {

  @Autowired
  private Air365UserV2Service service;

  @Autowired
  private PlatformService platformService;

  @ApiOperation(value = "로그인", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "password", value = "사용자 암호", required = true),
      @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "admin, member, group", required = true),
      @ApiImplicitParam(name = "token", value = "구글 메시지 토큰")
  })
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public HashMap<String, Object> appLogin(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String password =
        request.getParameter("password") == null ? "" : request.getParameter("password").trim();
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();
    String token =
        request.getParameter("token") == null ? "" : request.getParameter("token").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(password)) {
      throw new ParameterException(ParameterException.NULL_PW_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("password", password);
      reqInfo.put("userType", userType);
      reqInfo.put("token", token);

      res = service.login(reqInfo, response);
    }

    return res;
  }

  @ApiOperation(value = "로그아웃", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "token", value = "구글 메시지 토큰", required = true)
  })
  @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> appLogout(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String token =
        request.getParameter("token") == null ? "" : request.getParameter("token").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("token", token);

      res = service.appLogOut(reqInfo, response);
    }

    return res;
  }

  @ApiOperation(value = "관심 지역코드 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "lat", value = "위도", required = true),
      @ApiImplicitParam(name = "lon", value = "경도", required = true)
  })
  @RequestMapping(value = "/find/region", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> findRegionInfo(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    String lat = request.getParameter("lat") == null ? "" : request.getParameter("lat").trim();
    String lon = request.getParameter("lon") == null ? "" : request.getParameter("lon").trim();

    HashMap<String, String> reqInfo = new HashMap<>();

    if ("".equals(lat) || "".equals(lon)) {
      throw new ParameterException(ParameterException.NULL_LAT_LON_PARAMETER_EXCEPTION);
    } else if (!lat.concat(",").concat(lon).matches(CommonConstant.REG_LAT_LON)) {
      throw new ParameterException(ParameterException.ILLEGAL_LAT_LON_PARAMETER_EXCEPTION);
    } else {
      reqInfo.put("lat", lat);
      reqInfo.put("lon", lon);

      res = service.findRegionInfo(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "계정 체크", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "admin, member, group", required = true)
  })
  @RequestMapping(value = "/check/userId", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> checkUserId(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();

    HashMap<String, String> reqInfo = new HashMap<>();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else {
      reqInfo.put("userId", userId);
      reqInfo.put("userType", userType);

      res = service.checkUserId(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "SMS 전송", tags = "AIR365, 프로젝트")
  @ApiImplicitParam(name = "phoneNumber", value = "사용자 휴대폰 번호", required = true)
  @RequestMapping(value = "/sms/send", method = RequestMethod.POST)
  public HashMap<String, Object> sendSMSData(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    String phoneNumber =
        request.getParameter("phoneNumber") == null ? ""
            : request.getParameter("phoneNumber").trim();

    HashMap<String, String> reqInfo = new HashMap<>();

    if ("".equals(phoneNumber)) {
      throw new ParameterException(ParameterException.NULL_PHONE_PARAMETER_EXCEPTION);
    } else if (!phoneNumber.matches(CommonConstant.REG_PHONE)) {
      throw new ParameterException(ParameterException.ILLEGAL_PHONE_PARAMETER_EXCEPTION);
    } else {
      reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));

      res = service.sendSMSData(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "사용자 정보 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "admin, member, group", required = true)
  })
  @RequestMapping(value = "/get/user", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> selectUserInfo(HttpServletRequest request) throws Exception {

//    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
//      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
//    }

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();

    if ("admin".equals(userType)) {
      if (!RestApiCookieManageUtil.adminCookieCheck(request)) {
        throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_ADMIN_EXCEPTION);
      }
    }

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);

    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);

    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("userType", userType);

      res = service.selectUserInfo(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "전체 사용자 정보 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "all, member, group", required = true)
  @RequestMapping(value = "/get/userAll", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> selectUserInfoAll(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();

    if ("admin".equals(userType)) {
      if (!RestApiCookieManageUtil.adminCookieCheck(request)) {
        throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_ADMIN_EXCEPTION);
      }
    }

    if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userType", userType);

      res = service.selectUserInfoAll(reqInfo);
    }
    return res;
  }

  @ApiOperation(value = "회원 가입", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "password", value = "사용자 암호", required = true),
      @ApiImplicitParam(name = "userName", value = "사용자 이름", required = true),
      @ApiImplicitParam(name = "phoneNumber", value = "사용자 휴대폰 번호", required = true),
      @ApiImplicitParam(name = "region", value = "관심 지역 코드", required = true),
      @ApiImplicitParam(name = "regionName", value = "관심 지역 명", required = true),
      @ApiImplicitParam(name = "email", value = "사용자 이메일 주소", required = true),
      @ApiImplicitParam(name = "telephone", value = "사용자 유선 전화 번호")
  })
  @RequestMapping(value = "/insert/user", method = RequestMethod.POST)
  public HashMap<String, Object> insertUserInfo(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String password =
        request.getParameter("password") == null ? "" : request.getParameter("password").trim();
    String name =
        request.getParameter("userName") == null ? "" : request.getParameter("userName").trim();
    String phoneNumber = request.getParameter("phoneNumber") == null ? ""
        : request.getParameter("phoneNumber").trim();
    String telephone =
        request.getParameter("telephone") == null ? "" : request.getParameter("telephone").trim();
    String region =
        request.getParameter("region") == null ? "" : request.getParameter("region").trim();
    String regionName =
        request.getParameter("regionName") == null ? "" : request.getParameter("regionName").trim();
    String email =
        request.getParameter("email") == null ? "" : request.getParameter("email").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(password)) {
      throw new ParameterException(ParameterException.NULL_PW_PARAMETER_EXCEPTION);
    } else if ("".equals(phoneNumber)) {
      throw new ParameterException(ParameterException.NULL_PHONE_PARAMETER_EXCEPTION);
    } else if ("".equals(email)) {
      throw new ParameterException(ParameterException.NULL_EMAIL_PARAMETER_EXCEPTION);
    } else if ("".equals(region)) {
      throw new ParameterException(ParameterException.NULL_REGION_NUMBER_PARAMETER_EXCEPTION);
    } else if ("".equals(name) || "".equals(regionName)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else if (!phoneNumber.matches(CommonConstant.REG_PHONE)) {
      throw new ParameterException(ParameterException.ILLEGAL_PHONE_PARAMETER_EXCEPTION);
    } else if (!email.matches(CommonConstant.REG_EMAIL)) {
      throw new ParameterException(ParameterException.ILLEGAL_EMAIL_PARAMETER_EXCEPTION);
    } else if (!region.matches(CommonConstant.REG_NUMBER) || region.length() >= 10) {
      throw new ParameterException(ParameterException.ILLEGAL_REGION_NUMBER_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("userPw", Sha256EncryptUtil.ShaEncoder(password));
      reqInfo.put("userName", name);
      reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));
      reqInfo.put("telephone", telephone.replaceAll("-", ""));
      reqInfo.put("region", region);
      reqInfo.put("regionName", regionName);
      reqInfo.put("userEmail", email);

      res = service.insertUserInfo(reqInfo);
    }
    return res;
  }

  @ApiOperation(value = "회원 정보 수정", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "password", value = "사용자 암호"),
      @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "member, group", required = true),
      @ApiImplicitParam(name = "userName", value = "사용자 이름"),
      @ApiImplicitParam(name = "phoneNumber", value = "사용자 휴대폰 번호"),
      @ApiImplicitParam(name = "region", value = "관심 지역 코드"),
      @ApiImplicitParam(name = "regionName", value = "관심 지역 명"),
      @ApiImplicitParam(name = "email", value = "사용자 이메일 주소"),
      @ApiImplicitParam(name = "telephone", value = "사용자 유선 전화 번호")
  })
  @RequestMapping(value = "/update/user", method = {RequestMethod.PUT, RequestMethod.POST})
  public HashMap<String, Object> updateUserInfo(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String password =
        request.getParameter("password") == null ? null : request.getParameter("password").trim();
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();
    String name =
        request.getParameter("userName") == null ? null : request.getParameter("userName").trim();
    String groupDepartName = request.getParameter("groupDepartName") == null ? null
        : request.getParameter("groupDepartName").trim();
    String phoneNumber = request.getParameter("phoneNumber") == null ? null
        : request.getParameter("phoneNumber").trim().replaceAll("-", "");
    String region =
        request.getParameter("region") == null ? null : request.getParameter("region").trim();
    String regionName = request.getParameter("regionName") == null ? null
        : request.getParameter("regionName").trim();
    String email =
        request.getParameter("email") == null ? null : request.getParameter("email").trim();
    String telephone = request.getParameter("telephone") == null ? null
        : request.getParameter("telephone").trim().replaceAll("-", "");

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else {
      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId.replaceAll(" ", ""));
      reqInfo.put("userPw", password == null ? null : Sha256EncryptUtil.ShaEncoder(password));
      reqInfo.put("userType", userType);
      reqInfo.put("userName", name);
      reqInfo.put("groupDepartName", groupDepartName);
      reqInfo.put("phoneNumber", phoneNumber);
      reqInfo.put("telephone", telephone);
      reqInfo.put("region", region);
      reqInfo.put("regionName", regionName);
      reqInfo.put("userEmail", email);
      reqInfo.put("legacyUserPw", "");

      res = service.updateUserInfo(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "비밀번호 수정", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "userType", value = "사용자 유형", allowableValues = "member, group", required = true),
      @ApiImplicitParam(name = "password", value = "변경할 패스워드", required = true),
      @ApiImplicitParam(name = "legacyPassword", value = "기존 인증용 패스워드")
  })
  @RequestMapping(value = "/update/password", method = {RequestMethod.PUT, RequestMethod.POST})
  public HashMap<String, Object> updateUserPassword(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String legacyPassword = request.getParameter("legacyPassword") == null ? ""
        : request.getParameter("legacyPassword").trim();
    String password =
        request.getParameter("password") == null ? null : request.getParameter("password").trim();
    String userType =
        request.getParameter("userType") == null ? null : request.getParameter("userType").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(password)) {
      throw new ParameterException(ParameterException.NULL_PW_PARAMETER_EXCEPTION);
    } else if ("".equals(userType)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    } else {

      HashMap<String, String> reqInfo = new HashMap<>();
      reqInfo.put("userId", userId);
      reqInfo.put("legacyUserPw", legacyPassword);
      reqInfo.put("userPw", password == null ? null : Sha256EncryptUtil.ShaEncoder(password));
      reqInfo.put("userType", userType);

      res = service.updateUserInfo(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "회원 탈퇴", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userId", value = "사용자 계정", required = true),
      @ApiImplicitParam(name = "password", value = "사용자 암호", required = true),
  })
  @RequestMapping(value = "/delete/user", method = {RequestMethod.DELETE, RequestMethod.POST})
  public HashMap<String, Object> deleteUserInfo(HttpServletRequest request) throws Exception {

    if (!RestApiCookieManageUtil.userCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_MEMBER_EXCEPTION);
    }

    LinkedHashMap<String, Object> res;

    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();
    String password =
        request.getParameter("password") == null ? "" : request.getParameter("password").trim();

    if ("".equals(userId)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(password)) {
      throw new ParameterException(ParameterException.NULL_PW_PARAMETER_EXCEPTION);
    } else {
      res = service.deleteUserInfo(userId, password);

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

  @ApiOperation(value = "계정 찿기", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "userName", value = "사용자 이름", required = true),
      @ApiImplicitParam(name = "phoneNumber", value = "휴대폰 번호", required = true)
  })
  @RequestMapping(value = "/find/userId", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> findUserId(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    String userName =
        request.getParameter("userName") == null ? "" : request.getParameter("userName").trim();
    String phoneNumber = request.getParameter("phoneNumber") == null ? ""
        : request.getParameter("phoneNumber").trim();

    HashMap<String, String> reqInfo = new HashMap<>();

    if ("".equals(phoneNumber)) {
      throw new ParameterException(ParameterException.NULL_PHONE_PARAMETER_EXCEPTION);
    } else if ("".equals(userName)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    } else if (!phoneNumber.matches(CommonConstant.REG_PHONE)) {
      throw new ParameterException(ParameterException.ILLEGAL_PHONE_PARAMETER_EXCEPTION);
    } else {
      reqInfo.put("phoneNumber", phoneNumber.replaceAll("-", ""));
      reqInfo.put("userName", userName);

      res = service.findUserId(reqInfo);
    }

    return res;
  }

  @ApiOperation(value = "대시보드 데이터 조회", tags = "AIR365, 프로젝트")
  @RequestMapping(value = "/statistics/data", method = RequestMethod.GET)
  public HashMap<String, Object> getStatisticsData(HttpServletRequest request) throws Exception {

    LinkedHashMap<String, Object> res;

    if (!RestApiCookieManageUtil.adminCookieCheck(request)) {
      throw new CookieAuthException(CookieAuthException.COOKIE_AUTH_ADMIN_EXCEPTION);
    }

    res = service.getAdminStatisticsData();

    return res;
  }
}
