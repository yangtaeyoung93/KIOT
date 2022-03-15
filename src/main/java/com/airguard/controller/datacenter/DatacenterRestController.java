package com.airguard.controller.datacenter;

import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.model.system.DeviceElements;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.util.AES256Util;
import com.airguard.util.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(CommonConstant.URL_API_DATACENTER)
public class DatacenterRestController {

  private static final Logger logger = LoggerFactory.getLogger(DatacenterRestController.class);

  @Autowired
  private DatacenterService service;

  private static final String SUCCESS = "SUCCESS";
  private static final String FAIL = "FAIL";

  @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
  public Object dataCenterListApi(HttpServletRequest request) throws Exception {
    if (request.getCookies() == null) {
      logger.error("Call /api/datacenter/list API FAIL : [ ErrorMsg : {}, ClientIp : {} ]",
          CommonConstant.COOKIE_EX_MSG, request.getHeader("X-Forwarded-For"));
      throw new Exception();
    }

    List<ResultCollectionVo> selectList = new ArrayList<>();
    Map<String, Object> mp = new HashMap<>();
    String status = SUCCESS;

    String cookieKey = "";
    String cookieId = "";
    for (Cookie c : request.getCookies()) {
      if (c.getName().equals("GROUP_AUTH") || c.getName().equals("MEMBER_AUTH")) {
        cookieKey = c.getName();
        cookieId = AES256Util.decrypt(c.getValue());
      }
    }

    if (cookieKey.equals("GROUP_AUTH")) {
      for (String deviceType : service.selectGroupDeviceType(cookieId)) {
        if (deviceType != null) {
          logger.info("Provider GROUP : {}", deviceType);
          selectList
              .addAll(service.selectGroupSensorApi(cookieId, deviceType, request.getLocalName()));
        }
      }
      mp.put("groupId", cookieId);

    } else if (cookieKey.equals("MEMBER_AUTH")) {
      for (String deviceType : service.selectDeviceType(cookieId)) {
        if (deviceType != null) {
          logger.info("Provider MEMBER : {}", deviceType);
          selectList
              .addAll(service.selectPrivateSensorApi(cookieId, deviceType, request.getLocalName()));
        }
      }

      mp.put("memberId", cookieId);

    } else {
      mp.put("msg", "No AUTH");
      status = FAIL;
    }

    mp.put("status", status);
    mp.put("data", selectList);

    return mp;
  }

  @CrossOrigin
  @RequestMapping(value = "/vent", method = RequestMethod.GET, produces = "application/json")
  public Object dataCenterVentListApi(HttpServletRequest request) throws Exception {
    if (request.getCookies() == null) {
      logger.error("Call /api/datacenter/vent API FAIL : [ ErrorMsg : {}, ClientIp : {} ]",
          CommonConstant.COOKIE_EX_MSG, request.getHeader("X-Forwarded-For"));
      throw new AuthCookieException();
    }

    List<ResultCollectionVo> selectList = new ArrayList<>();
    Map<String, Object> mp = new HashMap<>();
    String status = SUCCESS;

    String cookieKey = "";
    String cookieId = "";
    for (Cookie c : request.getCookies()) {
      if (c.getName().equals("GROUP_AUTH") || c.getName().equals("MEMBER_AUTH")) {
        if(cookieId.equals("")) { //AIR365수정사항 - 맨 처음 AUTH 쿠키만 참조
          cookieId = AES256Util.decrypt(c.getValue());
          cookieKey = c.getName();
        }
      }
    }

    if (cookieKey.equals("GROUP_AUTH")) {
      selectList.addAll(service.selectGroupVentSensorApi(cookieId, request.getLocalName()));
      mp.put("groupId", cookieId);

    } else if (cookieKey.equals("MEMBER_AUTH")) {
      selectList.addAll(service.selectPrivateVentSensorApi(cookieId, request.getLocalName()));
      mp.put("memberId", cookieId);

    } else {
      mp.put("msg", "No AUTH");
      status = FAIL;
    }

    logger.error("=========================");
    logger.error("Call Domain :: {}", request.getLocalName());
    logger.error("Cookie KEY :: {}", cookieKey);
    logger.error("Cookie VALUE :: {}", cookieId);
    logger.error("DEBUG :: {}", selectList.toString());
    logger.error("=========================");

    mp.put("status", status);
    mp.put("data", selectList);

    return mp;
  }

  @RequestMapping(value = "/elements", method = RequestMethod.GET, produces = "application/json")
  public Object getModelElements(HttpServletRequest request) throws Exception {
    List<DeviceElements> selectList;
    Map<String, Object> mp = new HashMap<>();
    String serial = request.getParameter("serial");
    selectList = service.selectDeviceModelElements(serial);
    mp.put("serial", serial);
    mp.put("data", selectList);
    return mp;
  }

  private static class AuthCookieException extends Exception {
    public AuthCookieException() {
      super();
    }
  }

  @ExceptionHandler(value = AuthCookieException.class)
  public Map<String, Object> handleException() {
    logger.error(CommonConstant.COOKIE_EX_MSG);
    Map<String, Object> res = new HashMap<>();
    res.put("errorMessage", CommonConstant.COOKIE_EX_MSG);
    res.put("resultCode", 0);
    return res;
  }
}
