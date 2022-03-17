package com.airguard.controller.air365.v2;
import com.airguard.exception.*;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.datacenter.DatacenterConnectDto;
import com.airguard.service.app.VentService;
import com.airguard.util.*;
import com.airguard.model.app.AppVent;
import com.airguard.service.app.v2.Air365StationV2Service;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.service.platform.PlatformService;
import com.airguard.service.system.MemberDeviceService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

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

  @Autowired
  private VentService ventService;

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

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
  public HashMap<String, Object> getIotDataDetail(String serial, String region,boolean encoding) throws Exception {
    HashMap<String, Object> result = new HashMap<>();
    LinkedHashMap<String, Object> dataMp = new LinkedHashMap<>();


    int resultCode = CommonConstant.R_SUCC_CODE;

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    }

    //air365 모바일용 복호화
    if(encoding){
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      region = AES256Util.decrypt(region.replace(" ","+"));
      dataMp = stationService.getIotDataDetailEncodeVersion(serial, region);

    }else{
      dataMp = stationService.getIotDataDetail(serial, region);
    }


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

    Boolean encoding = request.getParameter("encoding") == null ?  false : true;

    if (serial == null || "".equals(serial) || mode == null || "".equals(mode))
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);

    if (Arrays.stream(CommonConstant.VENT_STATUS_CODE).noneMatch(mode::equals))
      throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);

    if(encoding){
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      mode = AES256Util.decrypt(mode.replace(" ","+"));
    }


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

  @ApiOperation(value = "VENT 장비 일괄 제어", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "type", value = "groupId / serial 선택"),
          @ApiImplicitParam(name = "groupId", value = "그룹계정"),
          @ApiImplicitParam(name = "serial", value = "vent serial"),})
  @RequestMapping(value = "/mqtt/all", method = {RequestMethod.POST})
  public HashMap<String, Object> postVentControlAll(HttpServletRequest request) throws Exception {
    HashMap<String, Object> result = new HashMap<>();
    int resultCode = 0;

    String type = request.getParameter("type") == null ? "" : request.getParameter("type");
    String groupId = request.getParameter("groupId") == null ? "" : request.getParameter("groupId");
    String serial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
    String mode = request.getParameter("mode") == null ? "" : request.getParameter("mode");

    if(type == null || "".equals(type)){
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    }

    if(type.equals("groupId") && (groupId == null || "".equals(groupId))){
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    }

    if(type.equals("serial") && (serial == null || "".equals(serial))){
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);
    }


    if (mode == null || "".equals(mode))
      throw new ParameterException(ParameterException.NULL_PARAMETER_EXCEPTION);

    if (Arrays.stream(CommonConstant.VENT_STATUS_CODE).noneMatch(mode::equals))
      throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);

    List<HashMap<String,String>> ventList = new ArrayList<>();
    if(type.equals("groupId")){
      List<String> memberIds = service.selectGroupForUser(groupId);
      for(String userId : memberIds ) {
        List<DatacenterConnectDto> datacenterConnectDtoList = service.selectUserVentDevice(userId);
        for(DatacenterConnectDto list : datacenterConnectDtoList){
          HashMap<String,String> userDevices = new HashMap<>();
          userDevices.put("ventSerial",list.getSerialNum());
          userDevices.put("iaqSerial",list.getIaqSerialNum());
          ventList.add(userDevices);
        }

      }
    }else{
      String[] ventSerials = serial.split(",");
      for(String vent : ventSerials){
        HashMap<String,String> userDevices = new HashMap<>();
        userDevices.put("ventSerial",vent);
        userDevices.put("iaqSerial",platformService.ventSerialToIaqSerial(vent));
        ventList.add(userDevices);
      }

    }
    CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
    List<HashMap<String,Object>> resultList = new ArrayList<>();
    for(HashMap<String,String> list : ventList){
      HashMap<String,Object> resultMap = new HashMap<>();
      resultMap.put("serial",list.get("ventSerial"));
      try {

        if (Arrays.asList(CommonConstant.VENT_AI_CODE).contains(mode)) {

          String aiMode = mode.equals("A1") ? "1" : "0";

          // RDB Update .
          platformService.updateventAiMode(aiMode, list.get("ventSerial"));

          // 연결 상태 알림 (플랫폼)
          platformService.postPlatformRequestConnect(list.get("iaqSerial"), request.getLocalName());

          // 명령어 전송 (플랫폼)
          resultCode = platformService.postPlatformRequestVent(list.get("ventSerial"), mode, request.getLocalName());

        } else {

          if(mode.equals("P1")){//전원ON => AI모드 ON
            String aiMode = "1";
            // RDB Update .
            platformService.updateventAiMode(aiMode, list.get("ventSerial"));
            // 연결 상태 알림 (플랫폼)
            platformService.postPlatformRequestConnect(list.get("iaqSerial"), request.getLocalName());
            // 전원 ON 명령어 전송 (플랫폼)
            resultCode = platformService.postPlatformRequestVent(list.get("ventSerial"), mode, request.getLocalName());
            //전원ON 성공인 경우만 A1 실행
            if(resultCode == 1) {
              resultCode = platformService.postPlatformRequestVent(list.get("ventSerial"), "A1", request.getLocalName());
            }
          }else if(mode.equals("P0")){//전원 OFF => AI모드 OFF
            String aiMode = "0";
            // RDB Update .
            platformService.updateventAiMode(aiMode, list.get("ventSerial"));
            // 연결 상태 알림 (플랫폼)
            platformService.postPlatformRequestConnect(list.get("iaqSerial"), request.getLocalName());
            // AI모드 OFF 명령어 전송 (플랫폼)
            resultCode = platformService.postPlatformRequestVent(list.get("ventSerial"), "A0", request.getLocalName());
            //A0 성공인 경우만 P0 실행
            if(resultCode == 1){
              resultCode = platformService.postPlatformRequestVent(list.get("ventSerial"), mode, request.getLocalName());
            }
          }else {
            // 명령어 전송 (플랫폼)
            resultCode = platformService.postPlatformRequestVent(list.get("ventSerial"), mode, request.getLocalName());
          }
        }

        resultMap.put("result", resultCode);

        if (resultCode == 2) {
//            throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);
          resultMap.put("errorCode",ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);
          resultMap.put("message",commonErrorMessage.getMessage(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION));
        }

        if (resultCode == 0) {
//            throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
          resultMap.put("errorCode",ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
          resultMap.put("message",commonErrorMessage.getMessage(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION));
        }

      } catch (NullPointerException e) {
//          throw new ParameterException(ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION);
        resultMap.put("errorCode",ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION);
        resultMap.put("message",commonErrorMessage.getMessage(ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION));
        resultList.add(resultMap);
        continue;
      } catch (ParameterException e) {
//          throw new ParameterException(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);
        resultMap.put("errorCode",ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION);
        resultMap.put("message",commonErrorMessage.getMessage(ParameterException.ILLEGAL_MODE_PARAMETER_EXCEPTION));
        resultList.add(resultMap);
        continue;
      } catch (Exception e) {
//          throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
        resultMap.put("errorCode",ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
        resultMap.put("message",commonErrorMessage.getMessage(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION));
        resultList.add(resultMap);
        continue;
      }
      resultList.add(resultMap);
    }

    result.put("result", resultList);

    return result;
  }

  @ApiOperation(value = "VENT 장비 데이터 조회 (단일)", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "serial", value = "스테이션 번호"),
  })
  @RequestMapping(value = "/data/vent", method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getVentStatusData(String serial, String region,Boolean encoding) throws Exception {
    HashMap<String, Object> result = new HashMap<>();

    if (serial == null || "".equals(serial)) {
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    }

    if(encoding == null){
      encoding = false;
    }
    if(encoding){
      serial = AES256Util.decrypt(serial.replace(" ","+"));
      region = AES256Util.decrypt(region.replace(" ","+"));
      result.put("data", stationService.getVentStatusDataEncodeVersion(serial, region));
    }else{
      result.put("data", stationService.getVentStatusData(serial, region));
    }

    result.put("result", 1);




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


  @ApiOperation(value = "NET인증 VENT 설정 정보 조회", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "ventSerial", value = "vent 시리얼", required = true),
  })
  @RequestMapping(value = "/kesr/out/get",method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> getKesrOutInfo(HttpServletRequest request) throws Exception {

    HashMap<String, Object> result = new HashMap<>();

    String ventSerial = request.getParameter("ventSerial") == null ? "" : request.getParameter("ventSerial");


    if ("".equals(ventSerial)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    } else {
      result.put("data",ventService.getKesrOutInfo(ventSerial));

    }
    result.put("result", 1);

    return result;
  }


  @ApiOperation(value = "NET인증 VENT 설정 정보 설정", tags = "AIR365, 프로젝트")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "serial", value = "vent 시리얼", required = true),
          @ApiImplicitParam(name = "standardTemp", value = "기준온도", required = true),
          @ApiImplicitParam(name = "outsideType", value = "0:동별미세먼지/1:OAQ", required = true),
          @ApiImplicitParam(name = "oaq", value = "oaq 시리얼(outsideType이 0일 때는 무시", required = true),
  })
  @RequestMapping(value = "/kesr/out/set",method = {RequestMethod.GET, RequestMethod.POST})
  public HashMap<String, Object> setKesrOutInfo(HttpServletRequest request) throws Exception {

    HashMap<String, Object> result = new HashMap<>();

    String ventSerial = request.getParameter("serial") == null ? "" : request.getParameter("serial");
    String standardTemp = request.getParameter("standardTemp") == null ? "" : request.getParameter("standardTemp");
    String outsideType = request.getParameter("outsideType") == null ? "" : request.getParameter("outsideType");
    String oaq = request.getParameter("oaq") == null ? "" : request.getParameter("oaq");

    if ("".equals(ventSerial) || "".equals(standardTemp) || "".equals(outsideType)) {
      throw new ParameterException(ParameterException.NULL_ID_PARAMETER_EXCEPTION);
    }if(outsideType.equals("1") && oaq.equals("")){
      throw new ParameterException(ParameterException.NULL_SERIAL_PARAMETER_EXCEPTION);
    } else {
      ventService.setKesrOutInfo(ventSerial,standardTemp,outsideType,oaq);


      platformService.postPlatformRequestConnect(
              readOnlyMapper.ventForIaq(ventSerial), request.getLocalName());
      platformService.publisherPlatform(platformService.idxToUserId(readOnlyMapper.selectMemberIdxFromVentSerial(ventSerial)),
              CommonConstant.PUBLISHER_USER, true, request.getLocalName());
      platformService.publisherPlatform(platformService.idxToUserId(readOnlyMapper.selectMemberIdxFromVentSerial(ventSerial)),
              CommonConstant.PUBLISHER_USER, false, request.getLocalName());
      platformService.publisherPlatform(platformService.memberIdxToGroupId(readOnlyMapper.selectMemberIdxFromVentSerial(ventSerial)),
              CommonConstant.PUBLISHER_GROUP, true, request.getLocalName());
      platformService.publisherPlatform(platformService.memberIdxToGroupId(readOnlyMapper.selectMemberIdxFromVentSerial(ventSerial)),
              CommonConstant.PUBLISHER_GROUP, false, request.getLocalName());

    }
    result.put("result", 1);

    return result;
  }
}
