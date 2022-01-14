package com.airguard.service.app;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.airguard.mapper.main.app.VentMapper;
import com.airguard.mapper.main.platform.PlatformMapper;
import com.airguard.model.app.ResponseModel;
import com.airguard.model.platform.PlatformSensorDto;
import com.airguard.model.platform.SensorDataDto;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.google.gson.Gson;
import com.airguard.model.app.AppVent;
import com.airguard.model.app.ResVentStatusModel;

@Service
public class VentService {

  private static final Logger logger = LoggerFactory.getLogger(VentService.class);

  @Autowired
  VentMapper mapper;

  @Autowired
  PlatformMapper platformMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  private static final Long WIFI_STATIC_MINUTE = 5L;
  private static final String WIFI_CON_F = "0";
  private static final String WIFI_CON_S = "1";

  public String getUserIdToGroupId(String userId) {
    return readOnlyMapper.getUserIdToGroupId(userId);
  }

  public ResponseModel addVent(AppVent vent, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      AppVent ventIdxs = readOnlyMapper.selectObjectIdxs(vent);

      if (ventIdxs.getMemberIdx().isEmpty() || ventIdxs.getIaqDeviceIdx().isEmpty()
          // 계정, 장비 등이 존재 하지 않을 경우 .
          || ventIdxs.getVentDeviceIdx().isEmpty()) {
        logger.error(
            "Call addVent API FAIL : [ ErrorCode : {} ErrorMsg : Member & Device No search, UserId : {}, ClientIp : {}",
            9L, vent.getUserId(), request.getHeader("X-Forwarded-For") + " ]");
        res.setErrorCode(9L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;

      } else {
        int ventConnectCheckFlag = readOnlyMapper
            .selectVentConnectCheck(ventIdxs.getVentDeviceIdx());

        if (ventConnectCheckFlag == 0) {
          logger.error(
              "Call addVent API FAIL : [ ErrorCode : {} ErrorMsg : Vent Device matching ERROR, VentSerial : {}, ClientIp : {}",
              5L, vent.getVentSerial(), request.getHeader("X-Forwarded-For") + " ]");
          res.setErrorCode(5L);
          res.setResult(CommonConstant.R_FAIL_CODE);
          return res;
        }

        vent.setMemberIdx(ventIdxs.getMemberIdx());
        vent.setIaqDeviceIdx(ventIdxs.getIaqDeviceIdx());
        vent.setVentDeviceIdx(ventIdxs.getVentDeviceIdx());
        mapper.addVent(vent);
        res.setErrorCode(0L);
        res.setResult(CommonConstant.R_SUCC_CODE);
      }

    } catch (DataIntegrityViolationException e) {
      logger.error(
          "Call addVent API FAIL : [ ErrorCode : {} ErrorMsg : please Member & Device Match Check, UserId : {}, ClientIp : {}",
          4L, vent.getUserId(), request.getHeader("X-Forwarded-For") + " ]");
      throw new DataIntegrityViolationException("(addVent) KIOT");
    } catch (RuntimeException e) {
      logger.error(
          "Call addVent API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, VentSerial : {}, ClientIp : {}",
          9L, vent.getVentSerial(), request.getHeader("X-Forwarded-For") + " ]");
      throw new RuntimeException("(addVent) KIOT");
    }

    return res;
  }

  public ResponseModel delVent(AppVent vent, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      AppVent ventIdxs = readOnlyMapper.selectObjectIdxs(vent);

      if (ventIdxs.getMemberIdx().isEmpty() || ventIdxs.getIaqDeviceIdx().isEmpty()
          || ventIdxs.getVentDeviceIdx().isEmpty()) {
        logger.error("Call delVent FAIL : {}", 4L);
        logger.error(
            "Call delVent API FAIL : [ ErrorCode : {} ErrorMsg : please Member & Device Match Check, UserId : {}, ClientIp : {}",
            4L, vent.getUserId(), request.getHeader("X-Forwarded-For") + " ]");
        res.setErrorCode(4L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;

      } else {
        vent.setVentDeviceIdx(ventIdxs.getVentDeviceIdx());
        mapper.delVent(vent);
        res.setErrorCode(0L);
        res.setResult(CommonConstant.R_SUCC_CODE);
      }

    } catch (RuntimeException e) {
      logger.error(
          "Call delVent API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {}",
          9L, request.getHeader("X-Forwarded-For") + " ]");
      throw new RuntimeException("(delVent) KIOT");
    }

    return res;
  }

  public ResVentStatusModel controlVent(AppVent vent, HttpServletRequest request) {
    ResVentStatusModel res = new ResVentStatusModel();

    try {

      logger.debug("vent.getVentSerial() : {}", vent.getVentSerial());
      res = getVentStatus(vent.getVentSerial());
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (Exception e) {
      logger.error(
          "Call controlVent API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {}, VentSerial : {}, ControlMode ]",
          9L, request.getHeader("X-Forwarded-For"), vent.getVentSerial(), vent.getControlMode());
      throw new RuntimeException("(controlVent) KIOT");
    }

    return res;
  }

  public ResVentStatusModel statusVent(AppVent vent, HttpServletRequest request) {
    ResVentStatusModel res = new ResVentStatusModel();

    try {

      res = getVentStatus(vent.getVentSerial());
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (Exception e) {
      logger.error(
          "Call statusVent API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {}, VentSerial : {} ]",
          9L, request.getHeader("X-Forwarded-For"), vent.getVentSerial());
      throw new RuntimeException("(statusVent) KIOT");
    }

    return res;
  }

  public ResVentStatusModel getVentStatus(String ventSerial) throws RuntimeException {
    ResVentStatusModel res = new ResVentStatusModel();

    try {

      Calendar calendar = Calendar.getInstance();
      Date date = calendar.getTime();
      String today = (new SimpleDateFormat("yyyyMMddHHmm").format(date));

      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);
      SensorDataDto ventData = selectVentCollectionApi(ventSerial);
      SensorDataDto iaqData = selectIaqCollectionApi(iaqSerial);

      String aiMode = readOnlyMapper.getVentAiMode(ventSerial);

      Long currentDateTime = Long.parseLong(today);
      Long collectionDateTime = Long.parseLong(
          (ventData == null || ventData.getReg_date() == null) ? today : ventData.getReg_date());

      if ((currentDateTime - collectionDateTime) <= WIFI_STATIC_MINUTE) {
        res.setWifi(WIFI_CON_S);
      } else {
        res.setWifi(WIFI_CON_F);
      }

      assert ventData != null;
      res.setTime(
          ventData.getReg_date() == null ? CommonConstant.NULL_DATA : ventData.getReg_date());
      res.setFilter(ventData.getFilter_alarm() == null ? CommonConstant.NULL_DATA
          : ventData.getFilter_alarm());
      res.setPower(ventData.getPower() == null ? CommonConstant.NULL_DATA : ventData.getPower());
      res.setWind(
          ventData.getAir_volume() == null ? CommonConstant.NULL_DATA : ventData.getAir_volume());
      res.setBypass(
          ventData.getExh_mode() == null ? CommonConstant.NULL_DATA : ventData.getExh_mode());
      res.setAuto(aiMode == null ? CommonConstant.NULL_DATA : aiMode);
      res.setCleaner(
          ventData.getAir_mode() == null ? CommonConstant.NULL_DATA : ventData.getAir_mode());

      res.setKhai(iaqData.getCici() == null ? CommonConstant.NULL_DATA : iaqData.getCici());
      res.setInPm10(iaqData.getPm10() == null ? CommonConstant.NULL_DATA : iaqData.getPm10());
      res.setInPm25(iaqData.getPm25() == null ? CommonConstant.NULL_DATA : iaqData.getPm25());
      res.setInCo2(iaqData.getCo2() == null ? CommonConstant.NULL_DATA : iaqData.getCo2());
      res.setInVocs(iaqData.getVoc() == null ? CommonConstant.NULL_DATA : iaqData.getVoc());

      res.setFireAlarm(
          ventData.getFire_alarm() == null ? CommonConstant.NULL_DATA : ventData.getFire_alarm());
      res.setWaterAlarm(
          ventData.getWater_alarm() == null ? CommonConstant.NULL_DATA : ventData.getWater_alarm());
      res.setDevStat(
          ventData.getDev_stat() == null ? CommonConstant.NULL_DATA : ventData.getDev_stat());

      res.setOutName(CommonConstant.NULL_DATA);
      res.setOutPm10(CommonConstant.NULL_DATA);
      res.setOutPm25(CommonConstant.NULL_DATA);
      res.setKorName(CommonConstant.NULL_DATA);
      res.setKorPm10(CommonConstant.NULL_DATA);
      res.setKorPm25(CommonConstant.NULL_DATA);
      res.setAutoCause(4L);

      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (Exception e) {
      throw new RuntimeException("(getVentStatus) KIOT");
    }

    return res;
  }

  public SensorDataDto selectVentCollectionApi(String ventSerial) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    SensorDataDto result = new SensorDataDto();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/kw-vsk/"
            + ventSerial);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Gson gson = new Gson();

    PlatformSensorDto resData = gson.fromJson(jObj.toString(), PlatformSensorDto.class);
    result = resData.getData();

    return result;
  }

  public SensorDataDto selectIaqCollectionApi(String iaqSerial) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    SensorDataDto result = new SensorDataDto();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/kw-isk");

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Gson gson = new Gson();

    if (!jObj.isNull(iaqSerial)) {
      PlatformSensorDto resData = gson.fromJson(jObj.getString(iaqSerial), PlatformSensorDto.class);
      result = resData.getData();
    }

    return result;
  }
}
