package com.airguard.controller.platform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(CommonConstant.URL_API_PLATFORM)
public class PlatformRestController {

  private static final Logger logger = LoggerFactory.getLogger(PlatformRestController.class);

  @Autowired
  private PlatformService service;

  @ApiOperation(value = "테스트 API", tags = "플랫폼 기반 API")
  @RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
  public int test(HttpServletRequest request) throws Exception {
    service.publisherPlatform("test2@kweather.com", "", true, request.getServerName());
    service.publisherPlatform("test2@kweather.com", "USER", false, request.getServerName());

    return 0;
  }

  @ApiOperation(value = "Redis 업데이트 API", tags = "플랫폼 기반 API")
  @RequestMapping(value = "/redis", method = RequestMethod.POST)
  public int iaqVentConnectApi(HttpServletRequest request) throws Exception {
    
    return service.postPlatformRequestConnect(
        request.getParameter("iaqSerial"), request.getLocalName());
  }

  @ApiOperation(value = "FOTA 정보 조회 API", tags = "플랫폼 기반 API")
  @RequestMapping(value = "/fota/r", method = RequestMethod.POST)
  public int fotaResetApi(HttpServletRequest request) throws Exception {
    return service.postPlatformRequestFota(CommonConstant.PLATFORM_API_TYPE_FOTA_RE,
        request.getParameter("serial"), request.getParameter("firmVersion"),
        request.getLocalName());
  }

  @ApiOperation(value = "FOTA 정보 업데이트 API", tags = "플랫폼 기반 API")
  @RequestMapping(value = "/fota/u", method = RequestMethod.POST)
  public int fotaUpgradeApi(HttpServletRequest request) throws Exception {
    String[] serials = request.getParameterValues("serial");

    int result = 0;
    for (String serial : serials) {
      result += service.postPlatformRequestFota(CommonConstant.PLATFORM_API_TYPE_FOTA_UP, serial,
          request.getParameter("firmVersion"), request.getLocalName());
    }

    if (result > 0) {
      return 1;
    } else {
      return 0;
    }
  }

  @CrossOrigin
  @ApiOperation(value = "MATT 호출 (장비 연동) API", tags = "플랫폼 기반 API")
  @RequestMapping(value = "/mqtt", method = RequestMethod.POST)
  public Map<String, Object> ventControlApi(HttpServletRequest request) throws Exception {
    String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
    String mode = request.getParameter("mode") == null ? "" : request.getParameter("mode");

    Map<String, Object> mp = new HashMap<>();

    if (Arrays.stream(CommonConstant.VENT_STATUS_CODE).noneMatch(mode::equals)) {
      throw new CodeMatchException();
    }

    if (Arrays.asList(CommonConstant.VENT_AI_CODE).contains(mode)) {
      String aiMode = mode.equals("A1") ? "1" : "0";

      service.updateventAiMode(aiMode, serial); // mySQL
      String iaqSerial = service.ventSerialToIaqSerial(serial);

      service.postPlatformRequestConnect(iaqSerial, request.getLocalName());
      service.postPlatformRequestVent(serial, mode, request.getLocalName());

    } else {
      service.postPlatformRequestVent(serial, mode, request.getLocalName());
    }

    mp.put("resultCode", 1);

    return mp;
  }

  private static class CodeMatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public CodeMatchException() {
      super();
    }
  }

  @ExceptionHandler(value = CodeMatchException.class)
  public Map<String, Object> handleCodeMatchException() {
    logger.error("Code match Error");
    Map<String, Object> res = new HashMap<>();
    res.put("errorMessage", CommonConstant.CODE_EX_MSG);
    res.put("resultCode", 0);
    return res;
  }
}
