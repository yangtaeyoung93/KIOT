package com.airguard.controller.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airguard.model.common.DamperDto;
import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.model.system.Group;
import com.airguard.service.common.DamperService;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.system.GroupService;
import com.airguard.service.system.MemberService;
import com.airguard.util.AES256Util;
import com.airguard.util.Sha256EncryptUtil;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/client")
public class ClientRestController {

  @Autowired
  private GroupService groupService;

  @Autowired
  private DatacenterService dataCenterService;

  @Autowired
  private DamperService damperService;

  @CrossOrigin
  @ApiOperation(value = "개인 사용자 인증 API", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/auth", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = "application/json")
  public Map<String, Object> clientLoginCheckApi(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Map<String, Object> reMap = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    String username = request.getParameter("username");
    String password = request.getParameter("password");

    logger.info("Client username : " + username);
    logger.info("Client password : " + password);
    Group pGroup = new Group();

    pGroup.setGroupId(username);
    pGroup.setGroupPw(Sha256EncryptUtil.ShaEncoder(password));

    int checkCode = groupService.loginCheckGroupId(pGroup);
    if (checkCode == 1) {
      reMap.put("groupId", username);
      reMap.put("resultCode", 0);
      reMap.put("errorCode", 1);
      return reMap;
    } else if (checkCode == 2) {
      reMap.put("groupId", username);
      reMap.put("resultCode", 0);
      reMap.put("errorCode", 2);
      return reMap;
    }

    Group group = groupService.findGroupByLoginId(username);

    if (group == null || !group.getGroupPw().equals(Sha256EncryptUtil.ShaEncoder(password))) {
      return reMap;
    }

    Cookie cookie = new Cookie("GROUP_AUTH", AES256Util.encrypt(username));

    cookie.setPath("/");
    cookie.setDomain("kweather.co.kr");

    reMap.put("groupId", username);
    reMap.put("resultCode", 1);

    response.addCookie(cookie);
    return reMap;
  }

  @CrossOrigin
  @ApiOperation(value = "측정기 장비 조회", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/datacenter/list",
      method = {RequestMethod.GET, RequestMethod.OPTIONS,
          RequestMethod.POST}, produces = "application/json")
  public Map<String, Object> dataCenterList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Map<String, Object> reMap = new HashMap<>();
    List<ResultCollectionVo> selectList = new ArrayList<>();

    Cookie[] cookies = request.getCookies();

    String groupId = request.getParameter("groupId");
    reMap.put("groupId", groupId);

    // cookie
    if (cookies != null) {
      for (Cookie c : cookies) {
        if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")) {

          for (String deviceType : dataCenterService.selectGroupDeviceType(groupId)) {
            selectList.addAll(dataCenterService.selectGroupSensorApi(groupId, deviceType,
                request.getLocalName()));
          }

          reMap.put("data", selectList);
          break;
        }
      }
    }

    response.setHeader("Content-Type", "application/javascript");

    return reMap;
  }

  @CrossOrigin
  @ApiOperation(value = "환기청정기 장비 조회", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/datacenter/vent",
      method = {RequestMethod.GET, RequestMethod.OPTIONS,
          RequestMethod.POST}, produces = "application/json")
  public Map<String, Object> dataCenterVent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Map<String, Object> reMap = new HashMap<>();
    List<ResultCollectionVo> selectList = new ArrayList<>();

    String groupId = request.getParameter("groupId");
    reMap.put("groupId", groupId);
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie c : cookies) {
        if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")) {
          selectList
              .addAll(dataCenterService.selectGroupVentSensorApi(groupId, request.getLocalName()));
          reMap.put("data", selectList);
        }
      }
    } else {
      reMap.put("result", 99);
      // header check
      Enumeration eHeader = request.getHeaderNames();
      while (eHeader.hasMoreElements()) {
        String hName = (String) eHeader.nextElement();
//                String hValue = request.getHeader(hName);

        if ("user-auth".equalsIgnoreCase(hName)) {
          selectList
              .addAll(dataCenterService.selectGroupVentSensorApi(groupId, request.getLocalName()));
          reMap.put("data", selectList);
          break;
        }
      }
    }

    response.setHeader("Content-Type", "application/javascript");

    return reMap;
  }

  @CrossOrigin
  @ApiOperation(value = "환기청정기 damper 데이터 조회 (순천)", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/datacenter/damper/list", method = RequestMethod.GET, produces = "application/json")
  public Map<String, Object> damperDataGet(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Map<String, Object> reMap = new HashMap<>();
    Cookie[] cookies = request.getCookies();

      if (cookies != null) {
          for (Cookie c : cookies) {
              if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")) {
                  reMap.put("data", damperService.selectDamper());
                  reMap.put("result", 1);
              } else {
                  reMap.put("result", 99);
              }
          }
      } else {
          reMap.put("result", 99);
      }

    response.setHeader("Content-Type", "application/javascript");
    return reMap;
  }

  @CrossOrigin
  @ApiOperation(value = "환기청정기 damper 데이터 변경 (순천)", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/datacenter/damper/control", method = RequestMethod.PUT, produces = "application/json")
  public Map<String, Object> damperDataPost(HttpServletRequest request, DamperDto damper,
      HttpServletResponse response) throws Exception {
    Map<String, Object> reMap = new HashMap<>();
    Cookie[] cookies = request.getCookies();
      if (cookies != null) {
          for (Cookie c : cookies) {
              if (c.getName().equals("ADMIN_AUTH") || c.getName().equals("GROUP_AUTH")) {
                  reMap.put("result", damperService.updateDamper(damper));
              } else {
                  reMap.put("result", 99);
              }
          }
      } else {
          reMap.put("result", 99);
      }
    response.setHeader("Content-Type", "application/javascript");
    return reMap;
  }

  @CrossOrigin
  @ApiOperation(value = "사용자 로그아웃", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "application/json")
  public void logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("_USER_AUTH", null);
    Cookie grCookie = new Cookie("GROUP_AUTH", null);

    cookie.setMaxAge(0);
    grCookie.setMaxAge(0);

    cookie.setPath("/");
    grCookie.setPath("/");

    cookie.setDomain("kweather.co.kr");
    grCookie.setDomain("kweather.co.kr");

    response.addCookie(cookie);
    response.addCookie(grCookie);
  }

  @ApiOperation(value = "그룹 사용자 인증 API", tags = "외부 프로젝트용 API")
  @RequestMapping(value = "/master/auth", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = "application/json")
  public Map<String, Object> clientLoginMasterApi(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Map<String, Object> reMap = new HashMap<>();

    String groupId = request.getParameter("groupId");

    Cookie cookie = new Cookie("GROUP_AUTH", AES256Util.encrypt(groupId));

    cookie.setPath("/");
    cookie.setDomain("kweather.co.kr");

    reMap.put("groupId", groupId);
    reMap.put("resultCode", 1);

    response.addCookie(cookie);
    return reMap;
  }
}
