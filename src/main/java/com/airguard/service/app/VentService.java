package com.airguard.service.app;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.airguard.model.system.Device;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.ArrayUtils;
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
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/kw-isk/"+iaqSerial);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Gson gson = new Gson();

    if (!jObj.isNull("data")) {

      PlatformSensorDto resData = gson.fromJson(jObj.toString(), PlatformSensorDto.class);
      result = resData.getData();

    }

    return result;
  }


  public Map<String, Object> getKesrOutInfo(String ventSerial) {

    Map<String, Object> result = new LinkedHashMap<>();

    try {
      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);
      result.put("standardTemp", readOnlyMapper.selectSetTemp(iaqSerial) == null ? 26 : readOnlyMapper.selectSetTemp(iaqSerial));
      Map<String, Object> iaqInfo = readOnlyMapper.selectIaqRelatedOaq(iaqSerial);
      result.put("locationArea", iaqInfo.get("dfname"));
      result.put("lat",iaqInfo.getOrDefault("lat","NA"));
      result.put("lon",iaqInfo.getOrDefault("lon","NA"));
      result.put("oaq", iaqInfo.getOrDefault("related_device_serial", "NA"));
      result.put("oaq_lat", iaqInfo.getOrDefault("oaq_lat", "NA"));
      result.put("oaq_lon", iaqInfo.getOrDefault("oaq_lon", "NA"));
      if (!result.get("oaq").equals("NA")) {
        HashMap<String, Double> lat = DMSCalculationForDistance(
                Double.parseDouble(getDmsByLatLon(iaqInfo.get("lat")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lat")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lat")).get("seconds").toString()),
                Double.parseDouble(getDmsByLatLon(iaqInfo.get("oaq_lat")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("oaq_lat")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("oaq_lat")).get("seconds").toString())
        );

        HashMap<String, Double> lon = DMSCalculationForDistance(
                Double.parseDouble(getDmsByLatLon(iaqInfo.get("lon")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lon")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lon")).get("seconds").toString()),
                Double.parseDouble(getDmsByLatLon(iaqInfo.get("oaq_lon")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("oaq_lon")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("oaq_lon")).get("seconds").toString())
        );
        result.put("distance", getDistanceByDms(lat.get("resultDeg"), lat.get("resultMin"), lat.get("resultSec"), lon.get("resultDeg"), lon.get("resultMin"), lon.get("resultSec")));
      }

      //result.put("distance",);
      result.put("outsideType", result.get("oaq").equals("NA")? "0" : "1");
      HashMap<String, Object> latLon = getLatLonRangeByDistance(iaqInfo.get("lat"), iaqInfo.get("lon"));
      HashMap<String, Object> nearByOaqs = new HashMap<>();
      List<HashMap<String, Object>> nearByOaqList = readOnlyMapper.selectNearByOaqs(latLon);
      nearByOaqs.put("nearbyOaqSize", nearByOaqList.size());
      ArrayList elements = new ArrayList<>();
      for (HashMap<String, Object> nearByOaq : nearByOaqList) {
        HashMap<String, Object> element = new HashMap<>();
        element.put("serial", nearByOaq.get("serial_num"));
        element.put("dateTime", selectOaqDateTime(nearByOaq.get("serial_num").toString()));
        HashMap<String, Double> lat = DMSCalculationForDistance(
                Double.parseDouble(getDmsByLatLon(iaqInfo.get("lat")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lat")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lat")).get("seconds").toString()),
                Double.parseDouble(getDmsByLatLon(nearByOaq.get("lat")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(nearByOaq.get("lat")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(nearByOaq.get("lat")).get("seconds").toString())
        );

        HashMap<String, Double> lon = DMSCalculationForDistance(
                Double.parseDouble(getDmsByLatLon(iaqInfo.get("lon")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lon")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(iaqInfo.get("lon")).get("seconds").toString()),
                Double.parseDouble(getDmsByLatLon(nearByOaq.get("lon")).get("degree").toString()), Double.parseDouble(getDmsByLatLon(nearByOaq.get("lon")).get("minutes").toString()), Double.parseDouble(getDmsByLatLon(nearByOaq.get("lon")).get("seconds").toString())
        );
        element.put("distance", getDistanceByDms(lat.get("resultDeg"), lat.get("resultMin"), lat.get("resultSec"), lon.get("resultDeg"), lon.get("resultMin"), lon.get("resultSec")));
        element.put("latitute", nearByOaq.get("lat"));
        element.put("longitude", nearByOaq.get("lon"));
        elements.add(element);
      }
      nearByOaqs.put("elements", elements);
      result.put("nearbyOaqs", nearByOaqs);
    }catch(Exception e){
      logger.error("getKesrOutInfo ERROR :: " + ventSerial);
      e.printStackTrace();
    }
    return result;

  }


  //소수점 표현 위도,경도를 DMS 표현으로 변환
  public HashMap<String ,Object> getDmsByLatLon(Object data) throws Exception{
    HashMap<String,Object> result = new HashMap<>();
    try {
      String[] dataArray = data.toString().split("\\.");
      String dataDegree = dataArray[0];

      String dataMinutesFull = String.valueOf(Double.parseDouble("0." + dataArray[1]) * 60);
      String dataMinutes = dataMinutesFull.split("\\.")[0];
      String dataSeconds;
      if(String.valueOf(Double.parseDouble("0." + dataMinutesFull.split("\\.")[1]) * 60).length()<5){
        dataSeconds = String.valueOf(Double.parseDouble("0." + dataMinutesFull.split("\\.")[1]) * 60);
      }else {
        dataSeconds = String.valueOf(Double.parseDouble("0." + dataMinutesFull.split("\\.")[1]) * 60).substring(0, 5);
      }
      result.put("degree", dataDegree);
      result.put("minutes", dataMinutes);
      result.put("seconds", dataSeconds);
    }catch(Exception e){
      e.printStackTrace();
    }
    return result;
  }

  //DMS 표현 위경도에 특정 거리를 더하는 연산
  public HashMap<String,Double> latlonCalDistance (Double targetDeg, Double targetMin, Double targetSec, Double opDeg, Double opMin, Double opSec,String operator) throws Exception{
    HashMap<String,Double> result = new HashMap<>();
    Double resultSec;
    Double resultMin;
    Double resultDeg;

    //더하기 연산
    if(operator =="+"){
      if(targetSec+opSec>=60){
        resultSec = targetSec+opSec-60;
        resultMin = targetMin+opMin+1;
        if(resultMin >=60){
          resultMin = resultMin -60;
          resultDeg = targetDeg + opDeg + 1;
        }else{
          resultDeg = targetDeg + opDeg;
        }
      }else{
        resultSec = targetSec+opSec;
        resultMin = targetMin+opMin;
        if(resultMin >=60){
          resultMin = resultMin -60;
          resultDeg = targetDeg + opDeg + 1;
        }else{
          resultDeg = targetDeg +opDeg;
        }
      }

    //빼기 연산
    }else{
      if(opSec > targetSec){
        resultSec = 60-opSec+targetSec;
        resultMin = targetMin-1;
        if(opMin > targetMin){
          resultMin = 60-opMin+resultMin;
          resultDeg = targetDeg -1 - opDeg;
        }else{
          resultMin = resultMin-opMin;
          resultDeg = targetDeg - opDeg;
        }
      }else{
        resultSec = targetSec - opSec;
        if(opMin > targetMin){
          resultMin = 60-opMin+targetMin;
          resultDeg = targetDeg -1 - opDeg;
        }else{
          resultMin = targetMin-opMin;
          resultDeg = targetDeg - opDeg;
        }
      }
    }
    result.put("resultDeg",resultDeg);
    result.put("resultMin",resultMin);
    result.put("resultSec",resultSec);
    return result;
  }

  //지리좌표간 두 점의 거리 계산을 위한 연산
  public HashMap<String, Double> DMSCalculationForDistance(Double targetDeg, Double targetMin, Double targetSec, Double opDeg, Double opMin, Double opSec)throws Exception{
    HashMap<String, Double> result = new HashMap<>();
    result.put("resultDeg",targetDeg-opDeg);
    result.put("resultMin",targetMin-opMin);
    result.put("resultSec",targetSec-opSec);

    return result;
  }


  public HashMap<String,Object> getLatLonRangeByDistance(Object lat, Object lon) throws Exception{
    HashMap<String,Object> result = new HashMap<>();
    try {
      //기준 장비 위도,경도를 DD(Decimal Degree) 표현-> DMS(Degree Minutes Seconds) 표현으로 변환
      String latDegree = (getDmsByLatLon(lat).get("degree")).toString();
      String latMinutes = (getDmsByLatLon(lat).get("minutes")).toString();
      String latSeconds = (getDmsByLatLon(lat).get("seconds")).toString();

      String lonDegree = (getDmsByLatLon(lon).get("degree")).toString();
      String lonMinutes = (getDmsByLatLon(lon).get("minutes")).toString();
      String lonSeconds = (getDmsByLatLon(lon).get("seconds")).toString();

      //위도 5km = 약 2분 42.2초(1분=1.85km, 1초=30.8m로 계산)
      int latDistanceMin = 2;
      Double latDistanceSec = 42.2D;

      //경도 5km = 약 3분 22.4초(1분=1.48km, 1초=25m로 계산)
      int lonDistanceMin = 3;
      Double lonDistanceSec = 22.4D;

      //위도 최대값 구하기(DMS)
      HashMap<String, Double> calResult = new HashMap<>();
      calResult = latlonCalDistance(Double.parseDouble(latDegree), Double.parseDouble(latMinutes), Double.parseDouble(latSeconds), 0D, (double) latDistanceMin, latDistanceSec, "+");
      Double latMaxSec = calResult.get("resultSec");
      Double latMaxMin = calResult.get("resultMin");
      Double latMaxDeg = calResult.get("resultDeg");


      //경도 최대값 구하기(DMS)
      calResult = latlonCalDistance(Double.parseDouble(lonDegree), Double.parseDouble(lonMinutes), Double.parseDouble(lonSeconds), 0D, (double) lonDistanceMin, lonDistanceSec, "+");
      Double lonMaxSec = calResult.get("resultSec");
      Double lonMaxMin = calResult.get("resultMin");
      Double lonMaxDeg = calResult.get("resultDeg");


      //위도 최소값 구하기(DMS)
      calResult = latlonCalDistance(Double.parseDouble(latDegree), Double.parseDouble(latMinutes), Double.parseDouble(latSeconds), 0D, (double) latDistanceMin, latDistanceSec, "-");
      Double latMinSec = calResult.get("resultSec");
      Double latMinMin = calResult.get("resultMin");
      Double latMinDeg = calResult.get("resultDeg");


      //경도 최소값 구하기(DMS)
      calResult = latlonCalDistance(Double.parseDouble(lonDegree), Double.parseDouble(lonMinutes), Double.parseDouble(lonSeconds), 0D, (double) lonDistanceMin, lonDistanceSec, "-");
      Double lonMinSec = calResult.get("resultSec");
      Double lonMinMin = calResult.get("resultMin");
      Double lonMinDeg = calResult.get("resultDeg");


      //DMS -> 소수점 표현으로 변환
      double latMax = latMaxDeg + (latMaxMin + latMaxSec / 60) / 60;
      //System.out.println("MAX LAT 소수점 표현 : " + latMax);

      double lonMax = lonMaxDeg + (lonMaxMin + lonMaxSec / 60) / 60;
      //System.out.println("MAX LON 소수점 표현 : " + lonMax);

      double latMin = latMinDeg + (latMinMin + latMinSec / 60) / 60;
      //System.out.println("MIN LAT 소수점 표현 : " + latMin);


      double lonMin = lonMinDeg + (lonMinMin + lonMinSec / 60) / 60;
      //System.out.println("MIN LON 소수점 표현 : " + lonMin);

      result.put("latMax", latMax);
      result.put("lonMax", lonMax);
      result.put("latMin", latMin);
      result.put("lonMin", lonMin);
    }catch(Exception e ){
      e.printStackTrace();
    }
    return result;
  }

  public Double getDistanceByDms(Double latDeg,Double latMin, Double latSec,Double lonDeg, Double lonMin, Double lonSec) throws Exception{
    Double result;
    result = Math.sqrt(Math.pow(latDeg*88.9036+latMin*1.4817+latSec*0.0246,2) + Math.pow(lonDeg*111.3194+lonMin*1.8553+lonSec*0.0309,2));
    return result;
  }


  public String selectOaqDateTime(String oaqSerial) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
            CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/kw-osk/"
                    + oaqSerial);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    JSONObject service = new JSONObject(jObj.getString("service"));
    String tsp = service.getString("timestamp");

    Date st = new Date(Integer.parseInt(tsp)*1000L);
    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmm");
    String result = dtFormat.format(st);

    return result;
  }


  public Map<String, Object> setKesrOutInfo(String ventSerial,String standardTemp,String outsideType,String oaq) {
    Map<String, Object> result = new HashMap<>();
    HashMap<String, Object> param = new HashMap<>();
    try{
      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);
      param.put("iaqSerial",iaqSerial);
      param.put("standardTemp",standardTemp);

      if(outsideType.equals("1")){//OAQ설정인경우
        param.put("oaqSerial",oaq);
      }else{
        param.put("oaqSerial",null);
      }
      mapper.updateIaqForVent(param);

    }catch(Exception e){
      logger.error("setKesrOutInfo ERROR :: ");
      e.printStackTrace();
    }
    return result;

  }



}
