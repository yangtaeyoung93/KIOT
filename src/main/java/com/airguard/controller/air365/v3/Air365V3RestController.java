package com.airguard.controller.air365.v3;

import com.airguard.exception.CommonErrorMessage;
import com.airguard.exception.ExternalApiException;
import com.airguard.exception.ParameterException;
import com.airguard.model.datacenter.DatacenterConnectDto;
import com.airguard.service.app.v3.Air365StationV3Service;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.MemberDeviceService;
import com.airguard.util.AES256Util;
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
import java.util.*;

@RestController
@RequestMapping(value = CommonConstant.URL_API_APP_AIR365_V3,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class Air365V3RestController {

  private static final Logger logger = LoggerFactory.getLogger(Air365V3RestController.class);

  @Autowired
  private MemberDeviceService memberDeviceService;

  @Autowired
  private Air365StationV3Service stationService;

  @Autowired
  private PlatformService platformService;

  @Autowired
  private DatacenterService service;


  @ApiOperation(value = "장비 데이터 조회 (상세 내용)", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({@ApiImplicitParam(name = "serial", value = "스테이션 번호"),})
  @RequestMapping(value = "/data/detail", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getIotDataDetail(String serial, String hang_cd,boolean encoding) throws Exception {
    HashMap<String, Object> result = new HashMap<>();
    LinkedHashMap<String, Object> dataMp = new LinkedHashMap<>();

    int resultCode = CommonConstant.R_SUCC_CODE;

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    }

    if(encoding){
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      hang_cd = AES256Util.decrypt(hang_cd.replace(" ","+"));
      dataMp = stationService.getIotDataDetailEncodeVersion(serial, hang_cd);

    }else{
      dataMp = stationService.getIotDataDetail(serial, hang_cd);
    }

    result.put("data", dataMp);
    result.put("result", resultCode);

    return result;
  }

  @RequestMapping(value = "/data/vent", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getVentStatusData(String serial, String hang_cd,Boolean encoding) throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    }

    System.out.println(AES256Util.encrypt(serial));
    System.out.println(AES256Util.encrypt(hang_cd));
    if(encoding == null){
      encoding = false;
    }
    if(encoding){
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      hang_cd = AES256Util.decrypt(hang_cd.replace(" ","+"));
      result.put("data", stationService.getVentStatusDataEncodeVersion(serial, hang_cd));
    }else{
      result.put("data", stationService.getVentStatusData(serial, hang_cd));
    }

    result.put("result", 1);

    return result;
  }

}
