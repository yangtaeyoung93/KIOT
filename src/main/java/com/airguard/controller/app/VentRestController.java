package com.airguard.controller.app;

import java.sql.SQLException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airguard.model.app.AppVent;
import com.airguard.model.app.ExceptionModel;
import com.airguard.model.app.RequestModel;
import com.airguard.model.app.ResVentStatusModel;
import com.airguard.model.app.ResponseModel;
import com.airguard.service.app.VentService;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(CommonConstant.URL_API_APP)
public class VentRestController {

  private static final Logger logger = LoggerFactory.getLogger(VentRestController.class);

  @Autowired
  private VentService service;

  @Autowired
  private PlatformService platformService;

  @ApiOperation(value = "환기청정기, 기기 등록", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/adddevice", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel addDevice(@ModelAttribute RequestModel reqVent, HttpServletRequest request)
      throws Exception {

    if (reqVent.getId().isEmpty() || reqVent.getStation_no().isEmpty()
        || reqVent.getSerial().isEmpty()) {
      throw new Exception();
    }

    AppVent vent = new AppVent();
    vent.setUserId(reqVent.getId());
    vent.setStationNo(reqVent.getStation_no());
    vent.setVentSerial(reqVent.getSerial());

    ResponseModel res = service.addVent(vent, request);
    if (res.getResult() == CommonConstant.R_FAIL_CODE) {
      return res;
    }

    String domain = request.getLocalName();
    String iaqSerial = vent.getStationNo();
    String userId = vent.getUserId();
    String groupId = service.getUserIdToGroupId(userId);

    platformService.postPlatformRequestConnect(iaqSerial,
        domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, true, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, false, domain);

    res.setDataUrl(request.getRequestURL().toString());
    return res;
  }

  @ApiOperation(value = "환기청정기, 기기 해제", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/deldevice", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResponseModel delDevice(@ModelAttribute RequestModel reqVent, HttpServletRequest request)
      throws Exception {

    if (reqVent.getId().isEmpty() || reqVent.getStation_no().isEmpty()
        || reqVent.getSerial().isEmpty()) {
      throw new Exception();
    }

    AppVent vent = new AppVent();
    vent.setUserId(reqVent.getId());
    vent.setStationNo(reqVent.getStation_no());
    vent.setVentSerial(reqVent.getSerial());

    ResponseModel res = service.delVent(vent, request);
    if (res.getResult() == CommonConstant.R_FAIL_CODE)
      return res;

    String domain = request.getLocalName();
    String iaqSerial = vent.getStationNo();
    String userId = vent.getUserId();
    String groupId = service.getUserIdToGroupId(userId);

    platformService.postPlatformRequestConnect(iaqSerial,
        domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, true, domain);
    platformService.publisherPlatform(userId, CommonConstant.PUBLISHER_USER, false, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, true, domain);
    platformService.publisherPlatform(groupId, CommonConstant.PUBLISHER_GROUP, false, domain);

    res.setDataUrl(request.getRequestURL().toString());
    return res;
  }

  @ApiOperation(value = "환기청정기, 제어 설정", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/devicecontrol", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResVentStatusModel controlDevice(@ModelAttribute RequestModel reqVent,
      HttpServletRequest request) throws Exception {
    ResVentStatusModel res;
    String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
    String mode = request.getParameter("mode") == null ? "" : request.getParameter("mode");
    String domain = request.getLocalName();

    if (reqVent.getSerial().isEmpty() || reqVent.getMode().isEmpty()) {
      throw new Exception();
    }

    if (Arrays.stream(CommonConstant.VENT_STATUS_CODE).noneMatch(reqVent.getMode()::equals)) {
      throw new CodeMatchException();
    }

    if (Arrays.asList(CommonConstant.VENT_AI_CODE).contains(reqVent.getMode())) {
      String ventSerial = reqVent.getSerial();
      String aiMode = reqVent.getMode().equals("A1") ? "1" : "0";

      platformService.updateventAiMode(aiMode, ventSerial);
      String iaqSerial = platformService.ventSerialToIaqSerial(ventSerial);
      platformService.postPlatformRequestConnect(
          iaqSerial, domain);

    }

    platformService.postPlatformRequestVent(serial,
        mode, request.getLocalName());

    AppVent vent = new AppVent();
    vent.setVentSerial(reqVent.getSerial());
    vent.setControlMode(reqVent.getMode());

    res = service.controlVent(vent, request);

    return res;
  }

  @ApiOperation(value = "환기청정기, 상태 정보 조회", tags = "AirGuard.K App, 프로젝트")
  @RequestMapping(value = "/devicestatus", method = {RequestMethod.GET, RequestMethod.POST},
      produces = "application/xml")
  public ResVentStatusModel statusDevice(@ModelAttribute RequestModel reqVent,
      HttpServletRequest request) throws Exception {
    ResVentStatusModel res;

    if (reqVent.getSerial().isEmpty()) {
      throw new Exception();
    }

    AppVent vent = new AppVent();
    vent.setVentSerial(reqVent.getSerial());

    res = service.statusVent(vent, request);

    return res;
  }

  private static class CodeMatchException extends Exception {
    public CodeMatchException() {
      super();
    }
  }

  @ExceptionHandler(value = CodeMatchException.class)
  public ExceptionModel handleCodeMatchException() {
    logger.error(CommonConstant.CODE_EX_MSG);
    ExceptionModel res = new ExceptionModel();
    res.setError_message(CommonConstant.CODE_EX_MSG);
    res.setError_code(93L);
    res.setResult(0);
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

  @ExceptionHandler(value = DataIntegrityViolationException.class)
  public ExceptionModel handleDataIntegrityViolationException() {
    logger.error(CommonConstant.DUPLICATE_EX_MSG);
    ExceptionModel res = new ExceptionModel();
    res.setError_message(CommonConstant.DUPLICATE_EX_MSG);
    res.setError_code(4L);
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
