package com.airguard.controller.air365.v2;

import com.airguard.exception.AuthException;
import com.airguard.exception.ExternalApiException;
import com.airguard.exception.ParameterException;
import com.airguard.model.app.AppVent;
import com.airguard.service.app.v2.Air365StationV2Service;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.MemberDeviceService;
import com.airguard.util.CommonConstant;
import com.airguard.util.MailSendUtil;
import com.airguard.util.SmsSendUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365_V2,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365V2RestController {

  private static final Logger logger = LoggerFactory.getLogger(Air365V2RestController.class);

  @Autowired
  private MemberDeviceService memberDeviceService;

  @Autowired
  private Air365StationV2Service stationService;

  @Autowired
  private PlatformService platformService;

  @Autowired
  private DatacenterService service;

  @ApiOperation(value = "사용자 장비 조회", tags = "AIR365, 프로젝트")
  @RequestMapping(value = "/member/device/list", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> memberDeviceAllList() throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    result.put("data", memberDeviceService.selectMemberDeviceListAir365());
    result.put("result", CommonConstant.R_SUCC_CODE);

    return result;
  }

  @ApiOperation(value = "이메일 전송", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({@ApiImplicitParam(name = "email", value = "전달받을 메일 계정", required = true),
      @ApiImplicitParam(name = "subject", value = "메일 제목"),
      @ApiImplicitParam(name = "content", value = "메일 내용"),
      @ApiImplicitParam(name = "sendType", value = "전송 유형")})
  @RequestMapping(value = "/mail/send", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> mailSend(HttpServletRequest request) throws Exception {
    HashMap<String, Object> result = new HashMap<>();
    int resultCode;

    String email =
        request.getParameter("email") == null ? "" : request.getParameter("email").trim();
    String subject =
        request.getParameter("subject") == null ? "" : request.getParameter("subject").trim();
    String content =
        request.getParameter("content") == null ? "" : request.getParameter("content").trim();
    String sendType =
        request.getParameter("sendType") == null ? "" : request.getParameter("sendType").trim();

    if ("".equals(email)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    }

    if ("SMS".equalsIgnoreCase(sendType)) {
      resultCode = SmsSendUtil.mailSend(email,
          URLEncoder.encode(content, StandardCharsets.UTF_8.toString()));
    } else {
      resultCode = MailSendUtil.mailSend(email, subject, content);
    }

    result.put("result", resultCode);

    return result;
  }

  @ApiOperation(value = "측정항목 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "사용자 계정"),
      @ApiImplicitParam(name = "serial", value = "스테이션 번호", defaultValue = "all"),
      @ApiImplicitParam(name = "userType", value = "사용자 유형")})
  @RequestMapping(value = "/elements", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getModelElements(HttpServletRequest request) throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    int resultCode = CommonConstant.R_SUCC_CODE;

    String serial =
        request.getParameter("serial") == null ? "all" : request.getParameter("serial").trim();
    String userType =
        request.getParameter("userType") == null ? "" : request.getParameter("userType").trim();
    String userId =
        request.getParameter("userId") == null ? "" : request.getParameter("userId").trim();

    if ("".equals(userId) && "all".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else if ("".equals(userType) && "all".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_TYPE_PARAMETER_EXCEPTION);
    }
    if ("all".equals(serial)) {
      result.put("data", service.selectDeviceModelElements(userType, userId));
    } else {
      result.put("data", service.selectDeviceModelElements(serial));
    }
    result.put("result", resultCode);

    return result;
  }

  @ApiOperation(value = "장비 데이터 조회 (상세 내용)", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({@ApiImplicitParam(name = "serial", value = "스테이션 번호"),})
  @RequestMapping(value = "/data/detail", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getIotDataDetail(String serial, String region) throws Exception {
    HashMap<String, Object> result = new HashMap<>();
    LinkedHashMap<String, Object> dataMp = new LinkedHashMap<>();

    int resultCode = CommonConstant.R_SUCC_CODE;

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    }

    dataMp = stationService.getIotDataDetail(serial, region);

    result.put("data", dataMp);
    result.put("result", resultCode);

    return result;
  }

  @ApiOperation(value = "VENT 장비 제어", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "request", value = "Prod / Dev Domain 구분 이용"),
      @ApiImplicitParam(name = "serial", value = "스테이션 번호"),
      @ApiImplicitParam(name = "mode", value = "명령어"),})
  @RequestMapping(value = "/mqtt", method = {RequestMethod.POST})
  public HashMap<String, Object> postVentControl(HttpServletRequest request) throws Exception {
    HashMap<String, Object> result = new HashMap<>();
    int resultCode = 0;

    String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
    String mode = request.getParameter("mode") == null ? "" : request.getParameter("mode");

    if (serial == null || "".equals(serial) || mode == null || "".equals(mode)) 
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);

    if (Arrays.stream(CommonConstant.VENT_STATUS_CODE).noneMatch(mode::equals)) 
      throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);

    try {

      if (Arrays.asList(CommonConstant.VENT_AI_CODE).contains(mode)) {
        String aiMode = mode.equals("A1") ? "1" : "0";

        // RDB Update .
        platformService.updateventAiMode(aiMode, serial);
        String iaqSerial = platformService.ventSerialToIaqSerial(serial);

        // 연결 상태 알림 (플랫폼)
        platformService.postPlatformRequestConnect(iaqSerial, request.getLocalName());

        // 명령어 전송 (플랫폼)
        resultCode = platformService.postPlatformRequestVent(serial, mode, request.getLocalName());

      } else {
        // 명령어 전송 (플랫폼)
        resultCode = platformService.postPlatformRequestVent(serial, mode, request.getLocalName());
      }

      if (resultCode == 2) 
        throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);
      if (resultCode == 0)
        throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);

    } catch (NullPointerException e) {
      throw new ParameterException(ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION);

    } catch (ParameterException e) {
      throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);

    } catch (Exception e) {
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    result.put("result", resultCode);

    return result;
  }

  @ApiOperation(value = "VENT 장비 데이터 조회 (단일)", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
        @ApiImplicitParam(name = "serial", value = "스테이션 번호"),
      })
  @RequestMapping(value = "/data/vent", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getVentStatusData(String serial, String region) throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    }

    result.put("result", 1);
    result.put("data", stationService.getVentStatusData(serial, region));

    return result;
  }

  @ApiOperation(value = "장비 데이터 조회 (통계 데이터) standard :: hour & days", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
        @ApiImplicitParam(name = "serial", value = "스테이션 번호"),
        @ApiImplicitParam(name = "searchDate", value = "검색 기준 일"),
        @ApiImplicitParam(name = "standard", value = "검색 데이터 기준"),
      })
  @RequestMapping(value = "/data/stat", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getStatIotData(String serial, String searchDate, String standard, String type) throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    if (Arrays.stream(new String[] {"hour", "day"}).noneMatch(standard::equals)) 
      throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);
    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else if (searchDate == null || "".equals(searchDate) || standard == null || "".equals(standard)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    }

//    if (stationService.air365PremiumCheckProcessF(serial, searchDate.substring(0, 6), "STATS") == 0) {
//      throw new AuthException(AuthException.PREMIUM_AUTH_EXCEPTION);
//    };

    // app 이용, 예외 처리
    result = "app".equals(type) ? stationService.getStatIotDataApp(serial, searchDate, ("day".equals(standard) ? standard : "hour")) : 
      stationService.getStatIotDataWeb(serial, searchDate, ("day".equals(standard) ? standard : "hour"));

    return result;
  }

  @ApiOperation(value = "장비 데이터 조회 (분석 데이터) standard :: 1 minute, 5 minute, 1 hours, 1 days, 1 month", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
        @ApiImplicitParam(name = "serial", value = "스테이션 번호"),
        @ApiImplicitParam(name = "searchDate", value = "검색 기준 일"),
        @ApiImplicitParam(name = "standard", value = "검색 데이터 기준"),
      })
  @RequestMapping(value = "/data/report", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getReportData(String serial, String searchDate, String standard, String type) throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    if (Arrays.stream(new String[] {"1min", "5min", "hour"}).noneMatch(standard::equals)) 
      throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else if (searchDate == null || "".equals(searchDate) || standard == null || "".equals(standard)) {
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    }

    // app 이용, 예외 처리
    result = "app".equals(type) ? stationService.getReportDataApp(serial, searchDate, standard)
        : stationService.getReportDataWeb(serial, searchDate, standard);

    return result;
  }
}
