package com.airguard.service.platform;

import com.airguard.exception.SQLException;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.airguard.model.platform.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.airguard.mapper.main.platform.PlatformMapper;
import com.airguard.model.control.ControlDto;
import com.airguard.model.control.PlatformControlDto;
import com.airguard.model.control.ResultControlVo;
import com.airguard.model.redis.RedisChannelDto;
import com.airguard.model.redis.RedisDto;
import com.airguard.model.redis.RedisVentDto;
import com.airguard.model.system.DeviceElements;
import com.airguard.service.datacenter.DatacenterService;
import com.airguard.util.CommonConstant;
import com.airguard.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class PlatformService {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  PlatformMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  @Autowired
  private DatacenterService datacenterService;

  private static final Logger logger = LoggerFactory.getLogger(PlatformService.class);

  public void updateventAiMode(String aiMode, String ventSerial) {
    mapper.updateventAiMode(aiMode, ventSerial);
  }

  public String ventSerialToIaqSerial(String ventSerial) {
    return mapper.ventSerialToIaqSerial(ventSerial);
  }

  public String memberIdxToGroupId(String memberIdx) {
    return mapper.memberIdxToGroupId(memberIdx);
  }

  public String idxToUserId(String idx) {
    return mapper.idxToUserId(idx);
  }

  public String userIdToGroupId(String userId) {
    return mapper.userIdToGroupId(userId);
  }

  public String idxToGroupId(String idx) {
    return mapper.idxToGroupId(idx);
  }

  public void offsetUpdate(HashMap<String, Object> req) {
    mapper.offsetUpdate(req);
  }

  public RedisVentDto selectVentOne(String serial) {
    return mapper.selectVentOne(serial);
  }

  public JSONObject groupingFormat(String id, String type, boolean clear, String domain)
      throws Exception {
    JSONObject resObj = new JSONObject();

    String userFormat;
    String groupFormat;

    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39")) {
      userFormat = CommonConstant.U_ID_FORMAT;
      groupFormat = CommonConstant.G_ID_FORMAT;

    } else {
      userFormat = CommonConstant.T_U_ID_FORMAT;
      groupFormat = CommonConstant.T_G_ID_FORMAT;
    }

    String publisherId = ((type.equals("USER")) ? userFormat : groupFormat) + id;
    List<CollectionDto> deviceInfoList = new ArrayList<>();

    if (type.equals("USER")) {
      deviceInfoList = mapper.selectUserDeviceList(id);
    } else if (type.equals("GROUP")) {
      deviceInfoList = mapper.selectGroupDeviceList(id);
    }

    if (clear) {
      resObj.put(publisherId, new JSONObject());
      return resObj;
    }

    List<String> iaqList = new ArrayList<>();
    List<String> oaqList = new ArrayList<>();
    List<String> dotList = new ArrayList<>();
    List<String> ventList =
        (type.equals("USER")) ? mapper.selectUserVentList(id) : mapper.selectGroupVentList(id);

    for (CollectionDto device : deviceInfoList) {

      try {

        if (device.getDeviceType().equals("IAQ")) {
          iaqList.add(device.getSerialNum());
        } else if (device.getDeviceType().equals("OAQ")) {
          oaqList.add(device.getSerialNum());
        } else {
          dotList.add(device.getSerialNum());
        }
      } catch (Exception ignored) {
      }
    }

    JSONObject keysObj = new JSONObject();

    JSONObject iskp1Obj = new JSONObject();
    JSONObject iske1Obj = new JSONObject();
    JSONObject oskp1Obj = new JSONObject();
    JSONObject oske1Obj = new JSONObject();
    JSONObject osde1Obj = new JSONObject();
    JSONObject vskp1Obj = new JSONObject();

    if (!iaqList.isEmpty()) {
      iskp1Obj.put("list", iaqList);
      iske1Obj.put("list", iaqList);
      keysObj.put("kw-iskp1", iskp1Obj);
      keysObj.put("kw-iske1", iske1Obj);
    }

    if (!oaqList.isEmpty()) {
      oskp1Obj.put("list", oaqList);
      oske1Obj.put("list", oaqList);
      keysObj.put("kw-oskp1", oske1Obj);
      keysObj.put("kw-oske1", oske1Obj);
    }

    if (!dotList.isEmpty()) {
      osde1Obj.put("list", dotList);
      keysObj.put("kw-osde1", osde1Obj);
    }

    if (!ventList.isEmpty()) {
      vskp1Obj.put("list", ventList);
      keysObj.put("kw-vskp1", vskp1Obj);
    }

    resObj.put(publisherId, keysObj);

    return resObj;
  }

  public int publisherPlatform(String id, String type, boolean clearFlag, String domain)
      throws Exception {
    int result = 0;

    if (id == null || id.equals("null") || id.equals("")) {
      return 1;
    }

    RestTemplate restTemplate = new RestTemplate();
    URI url = URI.create(CommonConstant.API_SERVER_HOST_REDIS + CommonConstant.SEARCH_PATH_REDIS);
    JSONObject jsonReq = new JSONObject();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "*/*");
    headers.add("Content-Type", "application/json;charset=UTF-8");

    JSONArray datasetArr = new JSONArray();
    JSONObject dataset = new JSONObject();
    JSONObject serviceObj = new JSONObject();
    JSONObject dataArrayData = new JSONObject();
    JSONArray dataArray = new JSONArray();
    JSONArray channelArray = new JSONArray();

    serviceObj.put("id", CommonConstant.REDIS_PUB_ID);

    channelArray.put(0, "ir-sensor-data-monitor/v1/groupmanager.put:ir-mon/groups/v1/update");
    dataArrayData.put("data", groupingFormat(id, type, clearFlag, domain).toString());
    dataArrayData.put("channel", channelArray);

    dataArray.put(0, dataArrayData);

    dataset.put("data", dataArray);
    dataset.put("service", serviceObj);

    datasetArr.put(0, dataset);

    jsonReq.put("dataset", datasetArr);

    HttpEntity<String> entity = new HttpEntity<>(jsonReq.toString(), headers);
    logger.error("=================================================");
    logger.error("*** Platform GROUP ==> Id :: {} SendData :: {} ***", id, jsonReq.toString());

    ResponseEntity<String> responseMsg = restTemplate
        .exchange(url, HttpMethod.POST, entity, String.class);
    if (responseMsg.getStatusCode() == HttpStatus.ACCEPTED) {
      result = 1;
    }

    logger.error("*** Platform GROUP RESULT :: {}", result);
    logger.error("=================================================");
    return result;
  }

  public String timeStampToString(String timestamp){
    Date st = new Date(Integer.parseInt(timestamp)*1000L);
    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String result = dtFormat.format(st);

    return result;
  }

  public List<ResultCollectionVo> selectTotalSensorApi(String paramType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();

    String deviceTypeIdx = "";
    String deviceType = "";
    switch (paramType) {
      case CommonConstant.PARAM_SENSOR_IAQ:
        deviceType = "IAQ";
        deviceTypeIdx = "1";
        break;
      case CommonConstant.PARAM_SENSOR_OAQ:
        deviceType = "OAQ";
        deviceTypeIdx = "2";
        break;
      case CommonConstant.PARAM_SENSOR_DOT:
        deviceType = "DOT";
        deviceTypeIdx = "3";
        break;
    }
    List<ResultCollectionVo> deviceList = mapper.selectCollectionDeviceWithDeviceType("00", "N",deviceTypeIdx);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR)
            .concat(paramType));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    ObjectMapper objectMapper = new ObjectMapper();

    for (ResultCollectionVo resultCollectionVo : deviceList) {
      try {
        String serial = resultCollectionVo.getSerial();
        PlatformSensorDto resData = objectMapper.readValue(jObj.getString(serial), PlatformSensorDto.class);
        resultCollectionVo.setSensor(resData.getData());
        String timestamp = resData.getService().getTimestamp();
        resultCollectionVo.setReceiveFlag(((Long.parseLong(timestamp)) + (5 * 60)) > (
                System.currentTimeMillis()
                        / 1000));
        resultCollectionVo.setTimestamp(timestamp);
        resultCollectionVo.setDataTime(timeStampToString(timestamp));
      }catch(Exception e){
        continue;
      }finally {
        resCol.add(resultCollectionVo);
      }
    }
    return resCol;
  }


  public Map<String, Object> selectAirMapStationInfo(String serial, String deviceType)
      throws Exception {
    Map<String, Object> result = new LinkedHashMap<>();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type",
        new StringBuilder(MediaType.APPLICATION_FORM_URLENCODED_VALUE).append(";charset=UTF-8")
            .toString());

    String paramType = "";
    Map<String, String> stationInfo = new HashMap<>();
    switch (deviceType) {
      case "oaq":
        stationInfo = readOnlyMapper.selectAirMapDeviceInfoKweather(serial);
        paramType = new StringBuilder("/v1/groups/kw-osk/").append(serial).toString();
        break;
      case "dot":
        stationInfo = readOnlyMapper.selectAirMapDeviceInfoKweather(serial);
        paramType = new StringBuilder("/v1/groups/kw-osd/").append(serial).toString();
        break;
      case "air":
        stationInfo = readOnlyMapper.selectAirMapDeviceInfoAirKorea(serial);
        paramType = new StringBuilder("/v1/sensors/airkorea-aq/").append(serial).toString();
        break;
      default:
        throw new Exception();
    }

    URI url = URI.create(
        new StringBuilder(CommonConstant.API_SERVER_HOST_TOTAL).append(paramType).toString());

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject resObj = new JSONObject(res.getBody().toString());
    result.put("station", stationInfo);
    result.put("sensor",
        resObj.has("data") ? JsonUtil.JsonToMap(resObj.getJSONObject("data").toString())
            : new LinkedHashMap<>());

    return result;
  }

  public List<AirMapOaqVo> selectTotalSensorAirMap(String siDo) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<AirMapOaqVo> resCol = new ArrayList<>();

    if (siDo == null) {
      siDo = "00";
    }

    List<CollectionDto> deviceList = mapper.selectCollectionDevice(siDo, "Y");

    String deviceType = "OAQ";
    String deviceType2 = "DOT";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR)
            .concat(CommonConstant.PARAM_SENSOR_OAQ));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    HashMap<String, String> siDoMap = new HashMap<>();
    siDoMap.put("11", "서울");
    siDoMap.put("26", "부산");
    siDoMap.put("27", "대구");
    siDoMap.put("28", "인천");
    siDoMap.put("29", "광주");
    siDoMap.put("30", "대전");
    siDoMap.put("31", "울산");
    siDoMap.put("36", "세종");
    siDoMap.put("41", "경기");
    siDoMap.put("42", "강원");
    siDoMap.put("43", "충북");
    siDoMap.put("44", "충남");
    siDoMap.put("45", "전북");
    siDoMap.put("46", "전남");
    siDoMap.put("47", "경북");
    siDoMap.put("48", "경남");
    siDoMap.put("50", "제주");

    airLoop:
    for (CollectionDto dto : deviceList) {
      AirMapOaqVo vo = new AirMapOaqVo();
      String serial = dto.getSerialNum();
      Gson gson = new Gson();

      if (!deviceType.equals(dto.getDeviceType())) {
        continue airLoop;
      }

      vo.setSerial(dto.getSerialNum());
      vo.setLat(dto.getLat());
      vo.setLon(dto.getLon());
      vo.setDCode(dto.getDCode());
      vo.setAddr(dto.getEquipAddr());
      vo.setAddr2(dto.getEquipAddr2());
      vo.setStationName(siDoMap.get(dto.getDCode().substring(0, 2)) + "_" + dto.getDeviceIdx());
      vo.setDeviceType("OAQ");

      vo.setMainImage(dto.getMainImage());
      vo.setEastImage(dto.getEastImage());
      vo.setWestImage(dto.getWestImage());
      vo.setSouthImage(dto.getSouthImage());
      vo.setNorthImage(dto.getNorthImage());

      if (!jObj.isNull(serial)) {
        Map<String, Object> resData = gson.fromJson(jObj.getString(serial), (Type) Map.class);
        Map<String, Object> dataList = (LinkedTreeMap) resData.get("data");
        dataList.keySet().removeIf(
            key -> key.contains("_raw") || key.contains("_ratio") || key.contains("_offset")
                || key.contains("coci_") || key.contains("_max"));

        vo.setSensor(dataList);
        LinkedTreeMap<String, Object> serviceList = (LinkedTreeMap) resData.get("service");
        BigDecimal bigDecimal = new BigDecimal((Double) serviceList.get("timestamp"));
        vo.setTimeStamp(bigDecimal);

        if (((Long.parseLong(vo.getTimeStamp().toString())) + (60 * 60 * 24)) < (
            System.currentTimeMillis() / 1000)) {
          continue airLoop;
        }

        vo.setReceiveFlag(((Long.parseLong(vo.getTimeStamp().toString())) + (60 * 60)) > (
            System.currentTimeMillis() / 1000));

      } else {
        continue airLoop;
      }

      resCol.add(vo);
    }

    if (siDo.equals("11") || siDo.equals("00")) {

      URI url2 = URI.create(
          CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR
              + CommonConstant.PARAM_SENSOR_DOT);
      RequestEntity<String> req2 = new RequestEntity<>(headers, HttpMethod.GET, url2);
      ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);

      JSONObject jObj2 = new JSONObject(res2.getBody());

      for (CollectionDto collectionDto : deviceList) {
        AirMapOaqVo vo = new AirMapOaqVo();
        String serial = collectionDto.getSerialNum();
        Gson gson = new Gson();

        if (!deviceType2.equals(collectionDto.getDeviceType())) {
          continue;
        }

        vo.setSerial(collectionDto.getSerialNum());
        vo.setLat(collectionDto.getLat());
        vo.setLon(collectionDto.getLon());
        vo.setDCode(collectionDto.getDCode());
        vo.setAddr(collectionDto.getEquipAddr());
        vo.setAddr2(collectionDto.getEquipAddr2());
        vo.setStationName(collectionDto.getStationName());
        vo.setDeviceType("DOT");

        vo.setMainImage(collectionDto.getMainImage());
        vo.setEastImage(collectionDto.getEastImage());
        vo.setWestImage(collectionDto.getWestImage());
        vo.setSouthImage(collectionDto.getSouthImage());
        vo.setNorthImage(collectionDto.getNorthImage());

        if (!jObj2.isNull(serial)) {
          Map<String, Object> resData = gson.fromJson(jObj2.getString(serial), (Type) Map.class);
          Map<String, Object> dataList = (LinkedTreeMap) resData.get("data");
          dataList.keySet().removeIf(
              key -> key.contains("_raw") || key.contains("_ratio") || key.contains("_offset")
                  || key.contains("coci_") || key.contains("_max"));

          vo.setSensor(dataList);
          LinkedTreeMap<String, Object> serviceList = (LinkedTreeMap) resData.get("service");
          BigDecimal bigDecimal = new BigDecimal((Double) serviceList.get("timestamp"));
          vo.setTimeStamp(bigDecimal);

          if (((Long.parseLong(vo.getTimeStamp().toString())) + (60 * 60 * 24)) < (
              System.currentTimeMillis() / 1000)) {
            continue;
          }

          vo.setReceiveFlag(((Long.parseLong(vo.getTimeStamp().toString())) + (60 * 60)) > (
              System.currentTimeMillis() / 1000));
        }

        resCol.add(vo);
      }

    }

    return resCol;
  }

  static boolean isNumberic(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public List<Map<String, Object>> selectTotalSensorAirMapLightly(String siDo, String type) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<Map<String, Object>> resCol = new ArrayList<>();

    if (siDo == "") {
      siDo = "00";
    }

    HashMap<String, String> siDoMap = new HashMap<>();
    siDoMap.put("11", "서울");
    siDoMap.put("26", "부산");
    siDoMap.put("27", "대구");
    siDoMap.put("28", "인천");
    siDoMap.put("29", "광주");
    siDoMap.put("30", "대전");
    siDoMap.put("31", "울산");
    siDoMap.put("36", "세종");
    siDoMap.put("41", "경기");
    siDoMap.put("42", "강원");
    siDoMap.put("43", "충북");
    siDoMap.put("44", "충남");
    siDoMap.put("45", "전북");
    siDoMap.put("46", "전남");
    siDoMap.put("47", "경북");
    siDoMap.put("48", "경남");
    siDoMap.put("50", "제주");

    List<CollectionDto> deviceList = mapper.selectCollectionDevice(siDo, "Y");

    String[] deviceTypes = {CommonConstant.PARAM_SENSOR_OAQ, CommonConstant.PARAM_SENSOR_DOT};

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url;
    JSONObject jObj = new JSONObject();
    JSONObject[] objs = new JSONObject[2];

    int i = 0;

    RequestEntity<String> req;
    ResponseEntity<String> res;
    switch (type.toUpperCase()) {
      case "KWEATHER":
        for (String deviceType : deviceTypes) {
          url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR).concat(deviceType));

          req = new RequestEntity<>(headers, HttpMethod.GET, url);
          res = restTemplate.exchange(req, String.class);
          objs[i++] = new JSONObject(res.getBody().toString());
        }
        for (JSONObject obj : objs) {
          Iterator it = obj.keys();
          while (it.hasNext()) {
            String key = (String) it.next();
            jObj.put(key, obj.get(key));
          }
        }
      break;
      case "OAQ":
        url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR).concat(deviceTypes[0]));

        req = new RequestEntity<>(headers, HttpMethod.GET, url);
        res = restTemplate.exchange(req, String.class);
        JSONObject oaqObj = new JSONObject(res.getBody().toString());
        Iterator oaqIt = oaqObj.keys();
        while (oaqIt.hasNext()) {
          String key = (String) oaqIt.next();
          jObj.put(key, oaqObj.get(key));
        }
        break;
      case "DOT":
        url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR).concat(deviceTypes[1]));

        req = new RequestEntity<>(headers, HttpMethod.GET, url);
        res = restTemplate.exchange(req, String.class);
        objs[0] = new JSONObject(res.getBody().toString());
        JSONObject dotObj = new JSONObject(res.getBody().toString());
        Iterator dotIt = dotObj.keys();
        while (dotIt.hasNext()) {
          String key = (String) dotIt.next();
          jObj.put(key, dotObj.get(key));
        }
        break;
      default:
        break;
    }


    airLoop: for (CollectionDto dto : deviceList) {
      Map<String, Object> dataMp = new LinkedHashMap<>();
      Map<String, Double> latlonMp = new LinkedHashMap<>();
      String serial = dto.getSerialNum();
      //logger.error("serial :: {}", serial);

      dataMp.put("serial", serial);

      latlonMp.put("lat", !isNumberic(dto.getLat()) ? 0 : Double.parseDouble(dto.getLat()));
      latlonMp.put("lon", !isNumberic(dto.getLon()) ? 0 : Double.parseDouble(dto.getLon()));
      dataMp.put("latlng", latlonMp);

      dataMp.put("stationName", siDoMap.get(dto.getDCode().substring(0, 2)).concat("_").concat(dto.getDeviceIdx()));
      dataMp.put("deviceType", dto.getDeviceType());

      if (!jObj.isNull(serial)) {
        JSONObject dataObj = jObj.getJSONObject(serial).getJSONObject("data");
        dataMp.put("pm10", !dataObj.has("pm10") ? "-" : dataObj.getInt("pm10"));
        dataMp.put("pm25", !dataObj.has("pm25") ? "-" : dataObj.getInt("pm25"));

      } else {
        continue airLoop;
      }

      resCol.add(dataMp);
    }

    return resCol;
  }

  public List<ResultCollectionVo> selectTestTotalSensorApi(String paramType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    List<CollectionDto> deviceList = mapper.selectCollectionDevice("00", "N");
    String[] serialArray = new String[deviceList.size()];

    int idx = 0;
    for (CollectionDto dto : deviceList) {
      serialArray[idx++] = dto.getSerialNum();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + paramType);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Iterator i = jObj.keys();
    while (i.hasNext()) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String key = i.next().toString();

      if (Arrays.asList(serialArray).contains(key)) {
        continue;
      }

      JSONObject jObj2 = (JSONObject) jObj.get(key);
      Object dataObj = jObj2.get("data");
      Object serviceObj = jObj2.get("service");

      SensorDataDto sensorData = objectMapper.readValue(dataObj.toString(), SensorDataDto.class);
      TimeStampDto serviceData = objectMapper.readValue(serviceObj.toString(), TimeStampDto.class);

      vo.setSerial(key);
      vo.setService(serviceData);
      vo.setSensor(sensorData);

      resCol.add(vo);
    }

    return resCol;
  }

  public List<ResultCollectionVo> selectTotalSensorVentApi(String paramType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<CollectionDto> deviceList = mapper.selectCollectionMasterDeviceVent();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + paramType);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    for (CollectionDto collectionDto : deviceList) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = collectionDto.getSerialNum();
      Gson gson = new Gson();

      vo.setSerial(serial);
      vo.setGroupCompanyName(collectionDto.getGroupCompanyName());
      vo.setGroupDepartName(collectionDto.getGroupDepartName());
      vo.setAiMode(collectionDto.getAiMode());
      vo.setTestYn(collectionDto.getTestYn());
      vo.setUserId(collectionDto.getUserId());
      vo.setGroupId(collectionDto.getGroupId());
      vo.setGroupName(collectionDto.getGroupName());
      vo.setMasterIdx(collectionDto.getMasterIdx());
      vo.setMasterName(collectionDto.getMasterName());
      vo.setStationName(collectionDto.getStationName());
      vo.setProductDt(collectionDto.getProductDt());
      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);

        vo.setReceiveFlag(((Long.parseLong(resData.getService().getTimestamp()))
            + (5 * 60)) > (System.currentTimeMillis() / 1000));
        vo.setSensor(resData.getData());
        vo.setTimestamp(resData.getService().getTimestamp());
      }

      resCol.add(vo);
    }

    return resCol;
  }

  public List<ResultCollectionVo> selectTestTotalSensorVentApi(String paramType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    List<CollectionDto> deviceList = mapper.selectCollectionDeviceVent();
    String[] serialArray = new String[deviceList.size()];

    int idx = 0;
    for (CollectionDto dto : deviceList) {
      serialArray[idx++] = dto.getSerialNum();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + paramType);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Iterator i = jObj.keys();
    while (i.hasNext()) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String key = i.next().toString();
      if (Arrays.asList(serialArray).contains(key)) {
        continue;
      }

      JSONObject jObj2 = (JSONObject) jObj.get(key);
      Object dataObj = jObj2.get("data");
      SensorDataDto sensorData = objectMapper.readValue(dataObj.toString(), SensorDataDto.class);
      Object serviceObj = jObj2.get("service");
      TimeStampDto serviceData = objectMapper.readValue(serviceObj.toString(), TimeStampDto.class);
      vo.setSerial(key);
      vo.setTimestamp(serviceData.getTimestamp());
      vo.setSensor(sensorData);

      resCol.add(vo);
    }

    return resCol;
  }

  public List<ResultCollectionVo> selectTotalSensorConnectApi(String paramType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<CollectionDto> deviceList = mapper.selectCollectionDevice("00", "N");

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + paramType);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    for (CollectionDto collectionDto : deviceList) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = collectionDto.getSerialNum();

      Gson gson = new Gson();
      if (!jObj.isNull(serial)) {
        List<PlatformVentDto> vents = new ArrayList<>();
        PlatformDataDto resData = gson.fromJson(jObj.getString(serial), PlatformDataDto.class);

        JSONObject jObjSerial = new JSONObject(jObj.getString(serial));
        JSONObject jObjData = new JSONObject(jObjSerial.getString("data"));
        JSONObject jObjVent = new JSONObject(jObjData.getString("vent"));

        StringBuilder ventsStr = new StringBuilder();
        Iterator i = jObjVent.keys();
        while (i.hasNext()) {
          PlatformVentDto resVent = new PlatformVentDto();
          String key = i.next().toString();
          resVent.setVent(key);
          resVent.setMode(jObjVent.getString(key));
          vents.add(resVent);
          ventsStr.append(key).append(" (")
              .append((jObjVent.getString(key).equals("1")) ? "ON" : "OFF").append(")<br/>");
        }

        vo.setVentsStr(ventsStr.toString());
        vo.setVents(vents);
        vo.setSerial(serial);
        vo.setSensor(resData.getData().getSensor());
        vo.setTimestamp(resData.getService().getTimestamp());
        vo.setEtc(collectionDto.getEtc());
        vo.setTestYn(collectionDto.getTestYn());
        vo.setGroupId(collectionDto.getGroupId());
        vo.setGroupName(collectionDto.getGroupName());
        vo.setParentSpaceName(collectionDto.getParentSpaceName());
        vo.setSpaceName(collectionDto.getSpaceName());
        vo.setUserId(collectionDto.getUserId());
        vo.setStationName(collectionDto.getStationName());
        vo.setProductDt(collectionDto.getProductDt());
        resCol.add(vo);
      }
    }

    return resCol;
  }

  public List<ResultControlVo> selectTotalControlApi(String paramType, String deviceType)
      throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultControlVo> resCol = new ArrayList<>();
    List<ControlDto> deviceList = mapper.selectControlDevice(deviceType);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers
        .add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR + paramType));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    for (ControlDto controlDto : deviceList) {
      ResultControlVo vo = new ResultControlVo();
      String serial = controlDto.getSerialNum();
      Gson gson = new Gson();

      vo.setSerial(serial);
      vo.setTestYn(controlDto.getTestYn());
      vo.setUserId(controlDto.getUserId());
      vo.setStationName(controlDto.getStationName());
      if (!jObj.isNull(serial)) {
        PlatformControlDto sensor = gson.fromJson(jObj.getString(serial), PlatformControlDto.class);
        vo.setControl(sensor.getData());
        vo.setTimestamp(sensor.getService().getTimestamp());
      }

      resCol.add(vo);
    }

    return resCol;
  }

  public List<Map<String, Object>> collectionSensorApiAirMap(String deviceType, String serial,
      String element, String date)
      throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<Map<String, Object>> resultData = new ArrayList<>();
    SimpleDateFormat reqSdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    Map<String, Object> data;
    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> elementMap = new LinkedHashMap<>();

    Calendar cal = Calendar.getInstance();
    cal.setTime(reqSdf.parse(date));
    String fromDt = sdf.format(cal.getTime()).concat("-00:00:00");
    String toDt = sdf.format(cal.getTime()).concat("-23:59:59");

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    String queryString;

    if ("air".equals(deviceType)) {
      queryString = "?start=" + URLEncoder.encode(fromDt, "UTF-8") + "&end="
          + URLEncoder.encode(toDt, "UTF-8") + "&m="
          + URLEncoder.encode("avg:sum", "UTF-8") + ":airkorea-aq." + serial + URLEncoder
          .encode("{sensor=*}", "UTF-8");

    } else {
      queryString = "?start=" + URLEncoder.encode(fromDt, "UTF-8") + "&end="
          + URLEncoder.encode(toDt, "UTF-8") + "&m="
          + URLEncoder.encode(
          "avg:sum:kw-" + ((deviceType.equals("dot")) ? "oaq" : deviceType) + "-sensor-"
              + ((deviceType.equals("dot")) ? "dot" : "kiot") + "." + serial + "{sensor=*}",
          "UTF-8");
    }

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + queryString);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());
    ObjectMapper objectMapper = new ObjectMapper();

    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object senOb;
      senOb = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map senMap = objectMapper.convertValue(senOb, Map.class);

      String sensor = (String) senMap.get("sensor");

      for (String key : (Iterable<String>) reMap.keySet()) {
        if (sensor.equals("tm")) {
          tmMap.put(key, (Double) reMap.get(key));
        } else if (sensor.equals(element)) {
          elementMap.put(key, (Double) reMap.get(key));
        }
      }
    }

    for (String s : elementMap.keySet()) {
      data = new LinkedHashMap<>();
      data.put("tm", Long.parseLong(s));
      data.put("element", elementMap.get(s));

      resultData.add(data);
    }

    return resultData;
  }

  public List<ResultCollectionHisVo> selectSensorApi(String deviceType, String serial,
      String startTime, String endTime,
      String standard, String connect) throws Exception {
    RestTemplate restTemplate = new RestTemplate();

    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> tempMap = new LinkedHashMap<>();
    Map<String, Double> humiMap = new LinkedHashMap<>();
    Map<String, Double> co2Map = new LinkedHashMap<>();
    Map<String, Double> vocMap = new LinkedHashMap<>();
    Map<String, Double> noiseMap = new LinkedHashMap<>();
    Map<String, Double> ciciMap = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();
    Map<String, Double> pm01Map = new LinkedHashMap<>();
    Map<String, Double> pm10RawMap = new LinkedHashMap<>();
    Map<String, Double> pm25RawMap = new LinkedHashMap<>();
    Map<String, Double> pm10RatioMap = new LinkedHashMap<>();
    Map<String, Double> pm25RatioMap = new LinkedHashMap<>();
    Map<String, Double> pm10OffsetMap = new LinkedHashMap<>();
    Map<String, Double> pm25OffsetMap = new LinkedHashMap<>();

    Map<String, Double> ciaiMap = new LinkedHashMap<>();
    Map<String, Double> coaiMap = new LinkedHashMap<>();

    Map<String, Double> ciciPm10Map = new LinkedHashMap<>();
    Map<String, Double> ciciPm25Map = new LinkedHashMap<>();
    Map<String, Double> ciciCo2Map = new LinkedHashMap<>();
    Map<String, Double> ciciVocMap = new LinkedHashMap<>();
    Map<String, Double> ciciTempMap = new LinkedHashMap<>();
    Map<String, Double> ciciHumiMap = new LinkedHashMap<>();
    Map<String, Double> ciciNoiseMap = new LinkedHashMap<>();
    Map<String, Double> luxMap = new LinkedHashMap<>();
    Map<String, Double> uvMap = new LinkedHashMap<>();
    Map<String, Double> wbgtMap = new LinkedHashMap<>();
    Map<String, Double> coMap = new LinkedHashMap<>();
    Map<String, Double> hchoMap = new LinkedHashMap<>();
    Map<String, Double> o3Map = new LinkedHashMap<>();
    Map<String, Double> rnMap = new LinkedHashMap<>();
    Map<String, Double> no2Map = new LinkedHashMap<>();
    Map<String, Double> so2Map = new LinkedHashMap<>();
    Map<String, Double> accxMap = new LinkedHashMap<>();
    Map<String, Double> accxMaxMap = new LinkedHashMap<>();
    Map<String, Double> accyMap = new LinkedHashMap<>();
    Map<String, Double> accyMaxMap = new LinkedHashMap<>();
    Map<String, Double> acczMap = new LinkedHashMap<>();
    Map<String, Double> acczMaxMap = new LinkedHashMap<>();
    Map<String, Double> winddMap = new LinkedHashMap<>();
    Map<String, Double> winddMaxMap = new LinkedHashMap<>();
    Map<String, Double> windsMap = new LinkedHashMap<>();
    Map<String, Double> windsMaxMap = new LinkedHashMap<>();
    Map<String, Double> cociMap = new LinkedHashMap<>();
    Map<String, Double> cociPm10Map = new LinkedHashMap<>();
    Map<String, Double> cociPm25Map = new LinkedHashMap<>();
    Map<String, Double> cociTempMap = new LinkedHashMap<>();
    Map<String, Double> cociHumiMap = new LinkedHashMap<>();
    Map<String, Double> cociNoiseMap = new LinkedHashMap<>();
    Map<String, Double> visitorMap = new LinkedHashMap<>();
    Map<String, Double> rainfallMap = new LinkedHashMap<>();
    Map<String, Double> dayRainfallMap = new LinkedHashMap<>();

    Map<String, Double> atmMap = new LinkedHashMap<>();
    Map<String, Double> rainMap = new LinkedHashMap<>();

    Map<String, Double> nh3Map = new LinkedHashMap<>();
    Map<String, Double> h2sMap = new LinkedHashMap<>();
    Map<String, Double> gpsLatMap = new LinkedHashMap<>();
    Map<String, Double> gpsLonMap = new LinkedHashMap<>();

    Map<String, Double> noMap = new LinkedHashMap<>();
    Map<String, Double> noxMap = new LinkedHashMap<>();
    Map<String, Double> tspMap = new LinkedHashMap<>();

    Map<String, String> regDateMap = new LinkedHashMap<>();
    Map<String, String> powerMap = new LinkedHashMap<>();
    Map<String, String> airVolumeMap = new LinkedHashMap<>();
    Map<String, String> filterAlarmMap = new LinkedHashMap<>();
    Map<String, String> airModeMap = new LinkedHashMap<>();
    Map<String, String> autoModeMap = new LinkedHashMap<>();
    Map<String, String> exhModeMap = new LinkedHashMap<>();

    Map<String, String> cmdWMap = new LinkedHashMap<>();
    Map<String, String> cmdPMap = new LinkedHashMap<>();
    Map<String, String> cmdMMap = new LinkedHashMap<>();
    Map<String, String> aiModeDevicesMap = new LinkedHashMap<>();
    Map<String, String> fireAlarmMap = new LinkedHashMap<>();
    Map<String, String> waterAlarmMap = new LinkedHashMap<>();
    Map<String, String> devStatMap = new LinkedHashMap<>();



    Map<String, String> humRtempMap = new LinkedHashMap<>();
    Map<String, String> humRhumiMap = new LinkedHashMap<>();
    Map<String, String> humOtempMap = new LinkedHashMap<>();
    Map<String, String> humOhumiMap = new LinkedHashMap<>();
    Map<String, String> humCo2Map = new LinkedHashMap<>();
    Map<String, String> humPm10Map = new LinkedHashMap<>();
    Map<String, String> humPm25Map = new LinkedHashMap<>();
    Map<String, String> wattMap = new LinkedHashMap<>();
    Map<String, String> humErrorAlarmHexMap = new LinkedHashMap<>();
    Map<String, String> humErrorAlarmKorMap = new LinkedHashMap<>();

    List<ResultCollectionHisVo> resCol = new ArrayList<>();
    HashMap<String, Object> result = new LinkedHashMap<>();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    String queryString = "?start=" + URLEncoder.encode(startTime, "UTF-8") + "&end="
        + URLEncoder.encode(endTime, "UTF-8") + "&m="
        + URLEncoder.encode(
        "avg:" + standard + ":kw-" + ((deviceType.equals("dot")) ? "oaq" : deviceType.toLowerCase())
            + "-sensor-"
            + ((deviceType.equals("dot")) ? "dot" : "kiot") + "." + serial + "{sensor=*}", "UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + queryString);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    result.put("statusCode", res.getStatusCodeValue());
    result.put("header", res.getHeaders());
    result.put("body", res.getBody());

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());
    ObjectMapper objectMapper = new ObjectMapper();

    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object senOb = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map senMap = objectMapper.convertValue(senOb, Map.class);

      String sensor = (String) senMap.get("sensor");

      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "tm":
            tmMap.put(key, (Double) reMap.get(key));
            break;
          case "pm25_raw":
            pm25RawMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm10_raw":
            pm10RawMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "temp":
            tempMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "humi":
            humiMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "co2":
            co2Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "voc":
            vocMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "noise":
            noiseMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici":
            ciciMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm10":
            pm10Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm25":
            pm25Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm01":
            pm01Map.put(key,
                    (standard.equals("sum")) ? (Double) reMap.get(key)
                            : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm10_ratio":
            pm10RatioMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm25_ratio":
            pm25RatioMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm10_offset":
            pm10OffsetMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm25_offset":
            pm25OffsetMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "ciai":
            ciaiMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "coai":
            coaiMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "cici_pm10":
            ciciPm10Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_pm25":
            ciciPm25Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_co2":
            ciciCo2Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_voc":
            ciciVocMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_temp":
            ciciTempMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_humi":
            ciciHumiMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_noise":
            ciciNoiseMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "lux":
            luxMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "uv":
            uvMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "wbgt":
            wbgtMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "co":
            coMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "hcho":
            hchoMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "o3":
            o3Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "rn":
            rnMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "no2":
            no2Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "so2":
            so2Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "accx":
            accxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accx_max":
            accxMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accy":
            accyMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accy_max":
            accyMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accz":
            acczMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accz_max":
            acczMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "windd":
            winddMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "windd_max":
            winddMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "winds":
            windsMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "winds_max":
            windsMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "coci":
            cociMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key) : (double) Math
                    .round((Double) reMap.get(key)));
            break;
          case "coci_pm10":
            cociPm10Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_pm25":
            cociPm25Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_noise":
            cociNoiseMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_humi":
            cociHumiMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "visitor":
            visitorMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "rainfall":
            rainfallMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "day_rainfall" :
            dayRainfallMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
          case "atm":
            atmMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "rain":
            rainMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "nh3":
            nh3Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "h2s":
            h2sMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "gps_lat":
            gpsLatMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "gps_lon":
            gpsLonMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "no":
            noMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "nox":
            noxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "tsp":
            tspMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;


          case "coci_temp":
            cociTempMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "reg_date":
            regDateMap.put(key, reMap.get(key).toString());
            break;
          case "power":
            powerMap.put(key, reMap.get(key).toString());
            break;
          case "air_volume":
            airVolumeMap.put(key, reMap.get(key).toString());
            break;
          case "filter_alarm":
            filterAlarmMap.put(key, reMap.get(key).toString());
            try {
              String filterAlarm = String
                  .valueOf(Math.round(Double.parseDouble(reMap.get(key).toString())));
              if (filterAlarm.length() == 5) {
                humErrorAlarmHexMap.put("humErrorHex",
                    getHumErrorHexCodeParsingToString(filterAlarm, "hexCodeStr"));
                humErrorAlarmKorMap
                    .put("humErrorKor", getHumErrorHexCodeParsingToString(filterAlarm, "code"));
              }

            } catch (Exception e) {
              logger.error("ERROR :: {}", e.getMessage());
            }

            break;
          case "air_mode":
            airModeMap.put(key, reMap.get(key).toString());
            break;
          case "auto_mode":
            autoModeMap.put(key, reMap.get(key).toString());
            break;
          case "exh_mode":
            exhModeMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_w":
            cmdWMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_p":
            cmdPMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_m":
            cmdMMap.put(key, reMap.get(key).toString());
            break;
          case "ai_mode_devices":
            aiModeDevicesMap.put(key, reMap.get(key).toString());
            break;
          case "fire_alarm":
            fireAlarmMap.put(key, reMap.get(key).toString());
            break;
          case "water_alarm":
            waterAlarmMap.put(key, reMap.get(key).toString());
            break;
          case "dev_stat":
            devStatMap.put(key, reMap.get(key).toString());
            break;
          case "hum_rtemp":
            humRtempMap.put(key, reMap.get(key).toString());
            break;
          case "hum_rhumi":
            humRhumiMap.put(key, reMap.get(key).toString());
            break;
          case "hum_otemp":
            humOtempMap.put(key, reMap.get(key).toString());
            break;
          case "hum_ohumi":
            humOhumiMap.put(key, reMap.get(key).toString());
            break;
          case "hum_co2":
            humCo2Map.put(key, reMap.get(key).toString());
            break;
          case "hum_pm10":
            humPm10Map.put(key, reMap.get(key).toString());
            break;
          case "hum_pm25":
            humPm25Map.put(key, reMap.get(key).toString());
            break;
          case "watt":
            wattMap.put(key, reMap.get(key).toString());
            break;
        }
      }
    }

    Iterator<String> keys2 = tmMap.keySet().iterator();

    if (deviceType.equals("vent")) {
      keys2 = regDateMap.keySet().iterator();
    }

    if (connect.equals("1")) {
      keys2 = cmdWMap.keySet().iterator();
    }

    while (keys2.hasNext()) {
      String key = keys2.next();
      ResultCollectionHisVo vo = new ResultCollectionHisVo();

      vo.setTimestamp(key);

      long timestamp = Long.parseLong(key);
      Date date = new java.util.Date(timestamp * 1000L);
      SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);

      vo.setFormatTimestamp(formattedDate);

      vo.setTm(tmMap.get(key));
      vo.setPm25_raw(pm25RawMap.get(key));
      vo.setPm10_raw(pm10RawMap.get(key));
      vo.setTemp(tempMap.get(key));
      vo.setHumi(humiMap.get(key));
      vo.setCo2(co2Map.get(key));
      vo.setVoc(vocMap.get(key));
      vo.setNoise(noiseMap.get(key));
      vo.setCici(ciciMap.get(key));
      vo.setPm10(pm10Map.get(key));
      vo.setPm25(pm25Map.get(key));
      vo.setPm01(pm01Map.get(key));
      vo.setPm10_ratio(pm10RatioMap.get(key));
      vo.setPm25_ratio(pm25RatioMap.get(key));
      vo.setPm10_offset(pm10OffsetMap.get(key));
      vo.setPm25_offset(pm25OffsetMap.get(key));
      vo.setCiai(ciaiMap.get(key));
      vo.setCoai(coaiMap.get(key));
      vo.setCici_pm10(ciciPm10Map.get(key));
      vo.setCici_pm25(ciciPm25Map.get(key));
      vo.setCici_co2(ciciCo2Map.get(key));
      vo.setCici_voc(ciciVocMap.get(key));
      vo.setCici_temp(ciciTempMap.get(key));
      vo.setCici_humi(ciciHumiMap.get(key));
      vo.setCici_noise(ciciNoiseMap.get(key));
      vo.setLux(luxMap.get(key));
      vo.setUv(uvMap.get(key));
      vo.setWbgt(wbgtMap.get(key));
      vo.setCo(coMap.get(key));
      vo.setHcho(hchoMap.get(key));
      vo.setO3(o3Map.get(key));
      vo.setRn(rnMap.get(key));
      vo.setNo2(no2Map.get(key));
      vo.setSo2(so2Map.get(key));
      vo.setAccx(accxMap.get(key));
      vo.setAccx_max(accxMaxMap.get(key));
      vo.setAccy(accyMap.get(key));
      vo.setAccy_max(accyMaxMap.get(key));
      vo.setAccz(acczMap.get(key));
      vo.setAccz_max(acczMaxMap.get(key));
      vo.setWindd(winddMap.get(key));
      vo.setWindd_max(winddMaxMap.get(key));
      vo.setWinds(windsMap.get(key));
      vo.setWinds_max(windsMaxMap.get(key));
      vo.setCoci(cociMap.get(key));
      vo.setCoci_pm10(cociPm10Map.get(key));
      vo.setCoci_pm25(cociPm25Map.get(key));
      vo.setCoci_humi(cociHumiMap.get(key));
      vo.setCoci_temp(cociTempMap.get(key));
      vo.setCoci_noise(cociNoiseMap.get(key));
      vo.setVisitor(visitorMap.get(key));
      vo.setRainfall(rainfallMap.get(key));
      vo.setDay_rainfall(dayRainfallMap.get(key));
      vo.setAtm(atmMap.get(key));
      vo.setRain(rainMap.get(key));
      vo.setNh3(nh3Map.get(key));
      vo.setH2s(h2sMap.get(key));
      vo.setGps_lat(gpsLatMap.get(key));
      vo.setGps_lon(gpsLonMap.get(key));
      vo.setNo(noMap.get(key));
      vo.setNox(noxMap.get(key));
      vo.setTsp(tspMap.get(key));
      vo.setReg_date(regDateMap.get(key));
      vo.setPower(powerMap.get(key));
      vo.setAir_volume(airVolumeMap.get(key));
      vo.setFilter_alarm(filterAlarmMap.get(key));
      vo.setAir_mode(airModeMap.get(key));
      vo.setAuto_mode(autoModeMap.get(key));
      vo.setExh_mode(exhModeMap.get(key));
      vo.setCmd_w(cmdWMap.get(key));
      vo.setCmd_p(cmdPMap.get(key));
      vo.setCmd_m(cmdMMap.get(key));
      vo.setAi_mode_devices(aiModeDevicesMap.get(key));
      vo.setFire_alarm(fireAlarmMap.get(key));
      vo.setWater_alarm(waterAlarmMap.get(key));
      vo.setDev_stat(devStatMap.get(key));

      vo.setHum_rtemp(humRtempMap.get(key));
      vo.setHum_rhumi(humRhumiMap.get(key));
      vo.setHum_otemp(humOtempMap.get(key));
      vo.setHum_ohumi(humOhumiMap.get(key));
      vo.setHum_co2(humCo2Map.get(key));
      vo.setHum_pm10(humPm10Map.get(key));
      vo.setHum_pm25(humPm25Map.get(key));
      vo.setWatt(wattMap.get(key));
      vo.setHum_err_hex(humErrorAlarmHexMap.get("humErrorHex"));
      vo.setHum_err_kor(humErrorAlarmKorMap.get("humErrorKor"));

      resCol.add(vo);
    }

    return resCol;
  }

  public List<ResultCollectionHisVo> selectSensorHistoryDayMonth(String deviceType, String serial,
      String startTime,
      String endTime, String standard, String connect) throws Exception {
    RestTemplate restTemplate = new RestTemplate();

    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> tempMap = new LinkedHashMap<>();
    Map<String, Double> humiMap = new LinkedHashMap<>();
    Map<String, Double> co2Map = new LinkedHashMap<>();
    Map<String, Double> vocMap = new LinkedHashMap<>();
    Map<String, Double> noiseMap = new LinkedHashMap<>();
    Map<String, Double> ciciMap = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();
    Map<String, Double> pm01Map = new LinkedHashMap<>();
    Map<String, Double> pm10RawMap = new LinkedHashMap<>();
    Map<String, Double> pm25RawMap = new LinkedHashMap<>();
    Map<String, Double> pm10RatioMap = new LinkedHashMap<>();
    Map<String, Double> pm25RatioMap = new LinkedHashMap<>();
    Map<String, Double> pm10OffsetMap = new LinkedHashMap<>();
    Map<String, Double> pm25OffsetMap = new LinkedHashMap<>();

    Map<String, Double> ciaiMap = new LinkedHashMap<>();
    Map<String, Double> coaiMap = new LinkedHashMap<>();

    Map<String, Double> ciciPm10Map = new LinkedHashMap<>();
    Map<String, Double> ciciPm25Map = new LinkedHashMap<>();
    Map<String, Double> ciciCo2Map = new LinkedHashMap<>();
    Map<String, Double> ciciVocMap = new LinkedHashMap<>();
    Map<String, Double> ciciTempMap = new LinkedHashMap<>();
    Map<String, Double> ciciHumiMap = new LinkedHashMap<>();
    Map<String, Double> ciciNoiseMap = new LinkedHashMap<>();
    Map<String, Double> luxMap = new LinkedHashMap<>();
    Map<String, Double> uvMap = new LinkedHashMap<>();
    Map<String, Double> wbgtMap = new LinkedHashMap<>();
    Map<String, Double> coMap = new LinkedHashMap<>();
    Map<String, Double> hchoMap = new LinkedHashMap<>();
    Map<String, Double> o3Map = new LinkedHashMap<>();
    Map<String, Double> rnMap = new LinkedHashMap<>();
    Map<String, Double> no2Map = new LinkedHashMap<>();
    Map<String, Double> so2Map = new LinkedHashMap<>();
    Map<String, Double> accxMap = new LinkedHashMap<>();
    Map<String, Double> accxMaxMap = new LinkedHashMap<>();
    Map<String, Double> accyMap = new LinkedHashMap<>();
    Map<String, Double> accyMaxMap = new LinkedHashMap<>();
    Map<String, Double> acczMap = new LinkedHashMap<>();
    Map<String, Double> acczMaxMap = new LinkedHashMap<>();
    Map<String, Double> winddMap = new LinkedHashMap<>();
    Map<String, Double> winddMaxMap = new LinkedHashMap<>();
    Map<String, Double> windsMap = new LinkedHashMap<>();
    Map<String, Double> windsMaxMap = new LinkedHashMap<>();
    Map<String, Double> cociMap = new LinkedHashMap<>();
    Map<String, Double> cociPm10Map = new LinkedHashMap<>();
    Map<String, Double> cociPm25Map = new LinkedHashMap<>();
    Map<String, Double> cociHumiMap = new LinkedHashMap<>();
    Map<String, Double> cociTempMap = new LinkedHashMap<>();
    Map<String, Double> cociNoiseMap = new LinkedHashMap<>();
    Map<String, Double> visitorMap = new LinkedHashMap<>();
    Map<String, Double> rainfallMap = new LinkedHashMap<>();

    Map<String, Double> atmMap = new LinkedHashMap<>();
    Map<String, Double> rainMap = new LinkedHashMap<>();
    Map<String, Double> nh3Map = new LinkedHashMap<>();
    Map<String, Double> h2sMap = new LinkedHashMap<>();
    Map<String, Double> gpsLatMap = new LinkedHashMap<>();
    Map<String, Double> gpsLonMap = new LinkedHashMap<>();

    Map<String, Double> noMap = new LinkedHashMap<>();
    Map<String, Double> noxMap = new LinkedHashMap<>();
    Map<String, Double> tspMap = new LinkedHashMap<>();

    Map<String, String> regDateMap = new LinkedHashMap<>();
    Map<String, String> powerMap = new LinkedHashMap<>();
    Map<String, String> airVolumeMap = new LinkedHashMap<>();
    Map<String, String> filterAlarmMap = new LinkedHashMap<>();
    Map<String, String> airModeMap = new LinkedHashMap<>();
    Map<String, String> autoModeMap = new LinkedHashMap<>();

    Map<String, String> cmdWMap = new LinkedHashMap<>();
    Map<String, String> cmdPMap = new LinkedHashMap<>();
    Map<String, String> cmdMMap = new LinkedHashMap<>();
    Map<String, String> aiModeDevicesMap = new LinkedHashMap<>();
    Map<String, String> fireAlarmMap = new LinkedHashMap<>();
    Map<String, String> waterAlarmMap = new LinkedHashMap<>();
    Map<String, String> devStatMap = new LinkedHashMap<>();

    List<ResultCollectionHisVo> resCol = new ArrayList<>();

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    ObjectMapper objectMapper = new ObjectMapper();

    serial = serial == null ? "" : serial;

    URI url = URI.create(CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY);
    JSONObject jsonReq = new JSONObject();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "*/*");
    headers.add("Content-Type", "application/json;charset=UTF-8");

    JSONArray datasetArr = new JSONArray();
    JSONObject dataset1 = new JSONObject();
    JSONObject tagData = new JSONObject();

    tagData.put("sensor", "*");

    String metricType = "";
    switch (deviceType.toLowerCase()) {
      case "iaq":
        metricType = CommonConstant.METRIC_TYPE_IAQ;
        break;
      case "oaq":
        metricType = CommonConstant.METRIC_TYPE_OAQ;
        break;
      case "dot":
        metricType = CommonConstant.METRIC_TYPE_DOT;
        break;
      case "vent":
        metricType = CommonConstant.METRIC_TYPE_VENT;
        break;
    }

    String metric = metricType + serial;
    dataset1.put("aggregator", "sum");
    dataset1.put("metric", metric);
    dataset1.put("tags", tagData);
    dataset1.put("downsample", standard);

    datasetArr.put(0, dataset1);

    jsonReq.put("start", startTime);
    jsonReq.put("end", endTime);
    jsonReq.put("timezone", "Asia/Seoul");
    jsonReq.put("useCalendar", true);
    jsonReq.put("queries", datasetArr);

    HttpEntity<String> entity = new HttpEntity<>(jsonReq.toString(), headers);
    ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());

    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object senOb = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map senMap = objectMapper.convertValue(senOb, Map.class);

      String sensor = (String) senMap.get("sensor");

      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "tm":
            tmMap.put(key, (Double) reMap.get(key));
            break;
          case "pm25_raw":
            pm25RawMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm10_raw":
            pm10RawMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "temp":
            tempMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "humi":
            humiMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "co2":
            co2Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "voc":
            vocMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "noise":
            noiseMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici":
            ciciMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm10":
            pm10Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm25":
            pm25Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm01":
            pm01Map.put(key,
                    (standard.equals("sum")) ? (Double) reMap.get(key)
                            : (double) Math.round((Double) reMap.get(key)));
            break;
          case "pm10_ratio":
            pm10RatioMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm25_ratio":
            pm25RatioMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm10_offset":
            pm10OffsetMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "pm25_offset":
            pm25OffsetMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "ciai":
            ciaiMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "coai":
            coaiMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "cici_pm10":
            ciciPm10Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_pm25":
            ciciPm25Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_co2":
            ciciCo2Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_voc":
            ciciVocMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_temp":
            ciciTempMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_humi":
            ciciHumiMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "cici_noise":
            ciciNoiseMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "lux":
            luxMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "uv":
            uvMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "wbgt":
            wbgtMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "co":
            coMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "hcho":
            hchoMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "o3":
            o3Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "rn":
            rnMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "no2":
            no2Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "so2":
            so2Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "accx":
            accxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accx_max":
            accxMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accy":
            accyMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accy_max":
            accyMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accz":
            acczMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "accz_max":
            acczMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100)) / 100.0);
            break;
          case "windd":
            winddMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "windd_max":
            winddMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "winds":
            windsMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "winds_max":
            windsMaxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "coci":
            cociMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key) : (double) Math
                    .round((Double) reMap.get(key)));
            break;
          case "coci_pm10":
            cociPm10Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_pm25":
            cociPm25Map.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_humi":
            cociHumiMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_temp":
            cociTempMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "coci_noise":
            cociNoiseMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "visitor":
            visitorMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "rainfall":
            rainfallMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "atm":
            atmMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 10)) / 10.0);
            break;
          case "rain":
            rainMap.put(key,
                (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round((Double) reMap.get(key)));
            break;
          case "nh3":
            nh3Map.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "h2s":
            h2sMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 1000)) / 1000.0);
            break;
          case "gps_lat":
            gpsLatMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "gps_lon":
            gpsLonMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "no":
            noMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "nox":
            noxMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "tsp":
            tspMap.put(key, (standard.equals("sum")) ? (Double) reMap.get(key)
                    : (double) Math.round(((Double) reMap.get(key) * 100000)) / 100000.0);
            break;
          case "reg_date":
            regDateMap.put(key, reMap.get(key).toString());
            break;
          case "power":
            powerMap.put(key, reMap.get(key).toString());
            break;
          case "air_volume":
            airVolumeMap.put(key, reMap.get(key).toString());
            break;
          case "filter_alarm":
            filterAlarmMap.put(key, reMap.get(key).toString());
            break;
          case "air_mode":
            airModeMap.put(key, reMap.get(key).toString());
            break;
          case "auto_mode":
            autoModeMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_w":
            cmdWMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_p":
            cmdPMap.put(key, reMap.get(key).toString());
            break;
          case "cmd_m":
            cmdMMap.put(key, reMap.get(key).toString());
            break;
          case "ai_mode_devices":
            aiModeDevicesMap.put(key, reMap.get(key).toString());
            break;
          case "fire_alarm":
            fireAlarmMap.put(key, reMap.get(key).toString());
            break;
          case "water_alarm":
            waterAlarmMap.put(key, reMap.get(key).toString());
            break;
          case "dev_stat":
            devStatMap.put(key, reMap.get(key).toString());
            break;
        }
      }
    }

    Iterator<String> keys2 = tmMap.keySet().iterator();

    if (deviceType.equals("vent")) {
      keys2 = regDateMap.keySet().iterator();
    }

    if (connect.equals("1")) {
      keys2 = cmdWMap.keySet().iterator();
    }

    while (keys2.hasNext()) {
      String key = keys2.next();
      ResultCollectionHisVo vo = new ResultCollectionHisVo();

      vo.setTimestamp(key);

      long timestamp = Long.parseLong(key);
      Date date = new java.util.Date(timestamp * 1000L);
      SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);

      vo.setFormatTimestamp(formattedDate);

      vo.setTm(tmMap.get(key));
      vo.setPm25_raw(pm25RawMap.get(key));
      vo.setPm10_raw(pm10RawMap.get(key));
      vo.setTemp(tempMap.get(key));
      vo.setHumi(humiMap.get(key));
      vo.setCo2(co2Map.get(key));
      vo.setVoc(vocMap.get(key));
      vo.setNoise(noiseMap.get(key));
      vo.setCici(ciciMap.get(key));
      vo.setPm10(pm10Map.get(key));
      vo.setPm25(pm25Map.get(key));
      vo.setPm01(pm01Map.get(key));
      vo.setPm10_ratio(pm10RatioMap.get(key));
      vo.setPm25_ratio(pm25RatioMap.get(key));
      vo.setPm10_offset(pm10OffsetMap.get(key));
      vo.setPm25_offset(pm25OffsetMap.get(key));
      vo.setCiai(ciaiMap.get(key));
      vo.setCoai(coaiMap.get(key));
      vo.setCici_pm10(ciciPm10Map.get(key));
      vo.setCici_pm25(ciciPm25Map.get(key));
      vo.setCici_co2(ciciCo2Map.get(key));
      vo.setCici_voc(ciciVocMap.get(key));
      vo.setCici_temp(ciciTempMap.get(key));
      vo.setCici_humi(ciciHumiMap.get(key));
      vo.setCici_noise(ciciNoiseMap.get(key));
      vo.setLux(luxMap.get(key));
      vo.setUv(uvMap.get(key));
      vo.setWbgt(wbgtMap.get(key));
      vo.setCo(coMap.get(key));
      vo.setHcho(hchoMap.get(key));
      vo.setO3(o3Map.get(key));
      vo.setRn(rnMap.get(key));
      vo.setNo2(no2Map.get(key));
      vo.setSo2(so2Map.get(key));
      vo.setAccx(accxMap.get(key));
      vo.setAccx_max(accxMaxMap.get(key));
      vo.setAccy(accyMap.get(key));
      vo.setAccy_max(accyMaxMap.get(key));
      vo.setAccz(acczMap.get(key));
      vo.setAccz_max(acczMaxMap.get(key));
      vo.setWindd(winddMap.get(key));
      vo.setWindd_max(winddMaxMap.get(key));
      vo.setWinds(windsMap.get(key));
      vo.setWinds_max(windsMaxMap.get(key));
      vo.setCoci(cociMap.get(key));
      vo.setCoci_pm10(cociPm10Map.get(key));
      vo.setCoci_pm25(cociPm25Map.get(key));
      vo.setCoci_humi(cociHumiMap.get(key));
      vo.setCoci_temp(cociTempMap.get(key));
      vo.setCoci_noise(cociNoiseMap.get(key));
      vo.setVisitor(visitorMap.get(key));
      vo.setRainfall(rainfallMap.get(key));
      vo.setAtm(atmMap.get(key));
      vo.setRain(rainMap.get(key));
      vo.setNh3(nh3Map.get(key));
      vo.setH2s(h2sMap.get(key));
      vo.setGps_lat(gpsLatMap.get(key));
      vo.setGps_lon(gpsLonMap.get(key));
      vo.setNo(noMap.get(key));
      vo.setNox(noxMap.get(key));
      vo.setTsp(tspMap.get(key));
      vo.setReg_date(regDateMap.get(key));
      vo.setPower(powerMap.get(key));
      vo.setAir_volume(airVolumeMap.get(key));
      vo.setFilter_alarm(filterAlarmMap.get(key));
      vo.setAir_mode(airModeMap.get(key));
      vo.setAuto_mode(autoModeMap.get(key));
      vo.setCmd_w(cmdWMap.get(key));
      vo.setCmd_p(cmdPMap.get(key));
      vo.setCmd_m(cmdMMap.get(key));
      vo.setAi_mode_devices(aiModeDevicesMap.get(key));
      vo.setFire_alarm(fireAlarmMap.get(key));
      vo.setWater_alarm(waterAlarmMap.get(key));
      vo.setDev_stat(devStatMap.get(key));

      resCol.add(vo);
    }

    return resCol;
  }

  public int postPlatformRequestConnect(String serialNum, String domain)
      throws Exception {
    int result = 0;

    if (!domain.equals("220.95.238.45") && !domain.equals("220.95.238.39") && !domain.equals("220.95.238.50")) {
      return 1;
    }

    RestTemplate restTemplate = new RestTemplate();
    URI url = URI.create(CommonConstant.API_SERVER_HOST_REDIS + CommonConstant.SEARCH_PATH_REDIS);
    JSONObject jsonReq = new JSONObject();
    String iaqIdx = mapper.selectIaqIdx(serialNum);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "*/*");
    headers.add("Content-Type", "application/json;charset=UTF-8");
    List<PlatformReqDataset> platReq;

    platReq = getRedisRequestModel(CommonConstant.PLATFORM_API_TYPE_CONNECT, iaqIdx, serialNum, "");

    JSONArray datasetArr = new JSONArray();

    JSONObject dataset1 = new JSONObject();
    JSONObject serviceObj1 = new JSONObject();
    JSONArray dataArray1 = new JSONArray();
    JSONObject dataArrayData1 = new JSONObject();

    JSONObject dataset2 = new JSONObject();
    JSONObject serviceObj2 = new JSONObject();
    JSONArray dataArray2 = new JSONArray();
    JSONObject dataArrayData2 = new JSONObject();
    JSONArray channelArray = new JSONArray();

    serviceObj1.put("id", platReq.get(0).getService().getId()); //"server_kwkiotcluster-redis"
    dataArrayData1.put("key", platReq.get(0).getData().get(0).getKey()); //"ingress-router/v1/memstore:kw/iaq/cmd/v1/KWV-ST1_1900420"
    dataArrayData1.put("value", platReq.get(0).getData().get(0).getValue());
    dataArray1.put(0, dataArrayData1);

    dataset1.put("service", serviceObj1);
    dataset1.put("data", dataArray1);

    serviceObj2.put("id", platReq.get(1).getService().getId());
    channelArray.put(0, platReq.get(1).getData().get(0).getChannel().get(0));
    dataArrayData2.put("channel", channelArray);
    dataArrayData2.put("data", platReq.get(1).getData().get(0).getData());
    dataArray2.put(0, dataArrayData2);

    dataset2.put("service", serviceObj2);
    dataset2.put("data", dataArray2);

    datasetArr.put(0, dataset1);
    datasetArr.put(1, dataset2);

    jsonReq.put("dataset", datasetArr);
    HttpEntity<String> entity = new HttpEntity<>(jsonReq.toString(), headers);
    ResponseEntity<String> responseMsg = restTemplate
        .exchange(url, HttpMethod.POST, entity, String.class);

    if (responseMsg.getStatusCode() == HttpStatus.ACCEPTED) {
      result = 1;
    }

    return result;
  }

  public int postPlatformRequestFota(String apiType, String serial, String firmVersion,
      String domain)
      throws Exception {
    int result = 0;

    if (!domain.equals("220.95.238.45") && !domain.equals("220.95.238.39")) {
      return 1;
    }

    serial = serial == null ? "" : serial;
    firmVersion = firmVersion == null ? "" : firmVersion;

    RestTemplate restTemplate = new RestTemplate();
    URI url = URI.create(CommonConstant.API_SERVER_HOST_REDIS + CommonConstant.SEARCH_PATH_REDIS);
    JSONObject jsonReq = new JSONObject();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "*/*");
    headers.add("Content-Type", "application/json;charset=UTF-8");
    List<PlatformReqDataset> platReq;

    if (apiType.equals(CommonConstant.PLATFORM_API_TYPE_FOTA_RE)) {
      platReq = getRedisRequestModel(CommonConstant.PLATFORM_API_TYPE_FOTA_RE, serial, serial,
          firmVersion);
    } else if (apiType.equals(CommonConstant.PLATFORM_API_TYPE_FOTA_UP)) {
      platReq = getRedisRequestModel(CommonConstant.PLATFORM_API_TYPE_FOTA_UP, serial, serial,
          firmVersion);
    } else {
      return 3;
    }

    JSONArray datasetArr = new JSONArray();

    JSONObject dataset1 = new JSONObject();
    JSONObject serviceObj1 = new JSONObject();
    JSONArray dataArray1 = new JSONArray();
    JSONObject dataArrayData1 = new JSONObject();

    JSONObject dataset2 = new JSONObject();
    JSONObject serviceObj2 = new JSONObject();
    JSONArray dataArray2 = new JSONArray();
    JSONObject dataArrayData2 = new JSONObject();
    JSONArray channelArray = new JSONArray();

    serviceObj1.put("id", platReq.get(0).getService().getId());
    dataArrayData1.put("key", platReq.get(0).getData().get(0).getKey());
    dataArrayData1.put("value", platReq.get(0).getData().get(0).getValue());
    dataArray1.put(0, dataArrayData1);

    dataset1.put("service", serviceObj1);
    dataset1.put("data", dataArray1);

    serviceObj2.put("id", platReq.get(1).getService().getId());
    channelArray.put(0, platReq.get(1).getData().get(0).getChannel().get(0));
    dataArrayData2.put("channel", channelArray);
    dataArrayData2.put("data", platReq.get(1).getData().get(0).getData());
    dataArray2.put(0, dataArrayData2);

    dataset2.put("service", serviceObj2);
    dataset2.put("data", dataArray2);

    datasetArr.put(0, dataset1);
    datasetArr.put(1, dataset2);

    jsonReq.put("dataset", datasetArr);

    HttpEntity<String> entity = new HttpEntity<>(jsonReq.toString(), headers);

    ResponseEntity<String> responseMsg = restTemplate
        .exchange(url, HttpMethod.POST, entity, String.class);

    if (responseMsg.getStatusCode() == HttpStatus.ACCEPTED) {
      result = 1;
    }

    return result;
  }

  public int postPlatformRequestVent(String serial, String mode, String domain)
      throws Exception {
    int result = 0;

    RedisVentDto vent = mapper.selectVentOne(serial);

    if(vent == null){
      throw new NullPointerException();
    }

    if (!mode.equals("A0") && !mode.equals("A1") && !ventCodeCheck(vent.getModel(), mode)) {
      return 2;
    }

    if ((mode.equals("A0") || mode.equals("A1"))
        && !vent.getModel().equals("TAES") // 태성 모델
        && !vent.getModel().equals("AHU") // 공조기
        && !vent.getModel().equals("KESR") // NET 모델
        && !vent.getModel().equals("KWG-ST1")
        && !vent.getModel().equals("KWV-AIC1")) {
      return 2;
    }

    if (!"220.95.238.45".equals(domain) && !"220.95.238.39".equals(domain) && !"220.95.238.50".equals(domain)) {
      return 0;
    }

    RestTemplate restTemplate = new RestTemplate();
    URI url = URI.create(CommonConstant.API_SERVER_HOST_REDIS.concat(CommonConstant.SEARCH_PATH_REDIS));
    JSONObject jsonReq = new JSONObject();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "*/*");
    headers.add("Content-Type", "application/json;charset=UTF-8");
    List<PlatformReqDataset> platReq;

    platReq = getMqttRequestModel(serial, mode, vent);

    JSONArray datasetArr = new JSONArray();

    JSONObject dataset1 = new JSONObject();
    JSONObject serviceObj1 = new JSONObject();
    JSONArray dataArray1 = new JSONArray();
    JSONObject dataArrayData1 = new JSONObject();
    JSONArray channelArray = new JSONArray();

    serviceObj1.put("id", platReq.get(0).getService().getId()); //"server_kwkiotcluster-mqtt-seq"
    dataArrayData1.put("data", platReq.get(0).getData().get(0).getData()); //"CCMDA0P1"
    channelArray.put(0, platReq.get(0).getData().get(0).getChannel().get(0)); //"kerv/req/KWV-ST1_1900420"
    dataArrayData1.put("channel", channelArray);

    int arrCnt = 0;
    if ((vent.getModel().equals("JNT")) && !mode.equals("P0") && !mode.equals("P1")) {
      JSONObject dataArrayDataPower = new JSONObject();
      JSONArray channelArrayPower = new JSONArray();

      serviceObj1.put("id", platReq.get(0).getService().getId());

      dataArrayDataPower.put("data", getControlCommand("P1", vent.getModel()));

      channelArrayPower.put(0, platReq.get(0).getData().get(0).getChannel().get(0));
      dataArrayDataPower.put("channel", channelArrayPower);
      dataArray1.put(arrCnt, dataArrayDataPower);
      arrCnt++;
    }

    dataArray1.put(arrCnt, dataArrayData1);

    dataset1.put("service", serviceObj1);
    dataset1.put("data", dataArray1);

    datasetArr.put(0, dataset1);

    jsonReq.put("dataset", datasetArr);

    HttpEntity<String> entity = new HttpEntity<>(jsonReq.toString(), headers);
    ResponseEntity<String> responseMsg = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    if (responseMsg.getStatusCode() == HttpStatus.ACCEPTED) {
      result = 1;
    }

    return result;
  }

  public List<PlatformReqDataset> getRedisRequestModel(String apiType, String iaqIdx,
      String serialNum, String firmVersion) throws SQLException {
    List<PlatformReqDataset> dataset = new ArrayList<>();

    String value = "";

    switch (apiType) {
      case CommonConstant.PLATFORM_API_TYPE_CONNECT:
        value = getConnectValue(iaqIdx, serialNum);
        break;
      case CommonConstant.PLATFORM_API_TYPE_FOTA_RE:
        value = getFotaValue(apiType, serialNum, firmVersion);
        break;
      case CommonConstant.PLATFORM_API_TYPE_FOTA_UP:
        value = getFotaValue(apiType, serialNum, firmVersion);
        break;
    }

    PlatformReqDataset dataset1 = new PlatformReqDataset();
    PlatformReqService serDataset1 = new PlatformReqService();
    serDataset1.setId(CommonConstant.REDIS_SET_ID);
    List<PlatformReqData> platformDataList1 = new ArrayList<>();
    PlatformReqData platformData1 = new PlatformReqData();

    switch (apiType) {
      case CommonConstant.PLATFORM_API_TYPE_CONNECT:
        platformData1.setKey(CommonConstant.API_KEY_CONNECT + serialNum);
        break;
      case CommonConstant.PLATFORM_API_TYPE_FOTA_RE:
        platformData1.setKey(CommonConstant.API_KEY_FOTA + serialNum);
        break;
      case CommonConstant.PLATFORM_API_TYPE_FOTA_UP:
        platformData1.setKey(CommonConstant.API_KEY_FOTA + serialNum);
        break;
    }

    platformData1.setValue(value);
    platformDataList1.add(platformData1);

    dataset1.setService(serDataset1);
    dataset1.setData(platformDataList1);

    PlatformReqDataset dataset2 = new PlatformReqDataset();
    PlatformReqService serDataset2 = new PlatformReqService();
    serDataset2.setId(CommonConstant.REDIS_PUB_ID);
    List<PlatformReqData> platformDataList2 = new ArrayList<>();
    PlatformReqData platformData2 = new PlatformReqData();
    List<String> channel = new ArrayList<>();
    switch (apiType) {
      case CommonConstant.PLATFORM_API_TYPE_CONNECT:
        channel.add(CommonConstant.API_CHANNEL_CONNECT + serialNum);
        break;
      case CommonConstant.PLATFORM_API_TYPE_FOTA_RE:
        channel.add(CommonConstant.API_CHANNEL_FOTA + serialNum);
        break;
      case CommonConstant.PLATFORM_API_TYPE_FOTA_UP:
        channel.add(CommonConstant.API_CHANNEL_FOTA + serialNum);
        break;
    }

    platformData2.setChannel(channel);
    platformData2.setData(value);
    platformDataList2.add(platformData2);

    dataset2.setService(serDataset2);
    dataset2.setData(platformDataList2);

    dataset.add(dataset1);
    dataset.add(dataset2);

    return dataset;
  }

  public List<PlatformReqDataset> getMqttRequestModel(String serialNum, String mode,
      RedisVentDto vent) {
    List<PlatformReqDataset> datasetList = new ArrayList<>();

    String req = vent.getReq();
    String modelType = vent.getModel();

    String dataValue = getControlCommand(mode, modelType);

    PlatformReqDataset dataset = new PlatformReqDataset();
    PlatformReqService setDataset = new PlatformReqService();
    setDataset.setId(CommonConstant.MQTT_ID);
    List<PlatformReqData> platformDataList = new ArrayList<>();

    PlatformReqData platformData = new PlatformReqData();
    List<String> channel = new ArrayList<>();
    dataset.setService(setDataset);
    dataset.setData(platformDataList);

    channel.add(req + "/" + serialNum);
    platformData.setChannel(channel);
    platformData.setData(dataValue);
    platformDataList.add(platformData);

    datasetList.add(dataset);

    return datasetList;
  }

  public String getConnectValue(String iaqIdx, String serialNum) throws SQLException {
    String result = "";

    ObjectMapper oma = new ObjectMapper();
    RedisDto res = new RedisDto();
    res.setSerial(serialNum);
    try {
      if (readOnlyMapper.getMemberDeviceInfoBySerial(serialNum).getSetTemp() != null) {
        res.setSet_temp(
            Integer.parseInt(readOnlyMapper.getMemberDeviceInfoBySerial(serialNum).getSetTemp()));
      }
      if (readOnlyMapper.getMemberDeviceInfoBySerial(serialNum).getDcode() != null) {
        res.setRef_dcode(readOnlyMapper.getMemberDeviceInfoBySerial(serialNum).getDcode());
      }
      if (readOnlyMapper.getMemberDeviceInfoBySerial(serialNum).getRelatedDevice() != null) {
        res.setRef_oaq(readOnlyMapper.getMemberDeviceInfoBySerial(serialNum).getRelatedDevice());
      }

    } catch (Exception e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }
    res.setUpdate(Calendar.getInstance().getTimeInMillis() / 1000);

    List<RedisVent> resVentList = new ArrayList<>();
    List<RedisVentDto> ventDevices = mapper.selectVentList(iaqIdx);

    int aiDeviceCnt = 0;

    for (RedisVentDto dto : ventDevices) {
      RedisVent resVent = new RedisVent();
      RedisChannelDto resCh = new RedisChannelDto();

      if (dto.getAiMode() == 1) {
        aiDeviceCnt++;
      }

      resCh.setReq(dto.getReq() + "/" + dto.getSerial());
      resVent.setChannel(resCh);
      resVent.setAi_mode(dto.getAiMode());
      resVent.setSerial(dto.getSerial());
      resVent.setModel(dto.getModel());

      resVentList.add(resVent);
    }

    res.setVent(resVentList);
    res.setAi_mode_devices(aiDeviceCnt);

    try {

      result = oma.writeValueAsString(res);

    } catch (JsonProcessingException e) {
      logger.error("JSON E, ", e.getMessage());
    }

    return result;
  }

  public String getFotaValue(String apiType, String serialNum, String firmVersion) {
    String result = "";
    int resetCode = apiType.equals(CommonConstant.PLATFORM_API_TYPE_FOTA_RE) ? 1 : 0;
    int fotaCode = apiType.equals(CommonConstant.PLATFORM_API_TYPE_FOTA_UP) ? 1 : 0;
    ObjectMapper oma = new ObjectMapper();
    FotaDto res = new FotaDto();
    res.setSerial(serialNum);
    res.setUpdate(Calendar.getInstance().getTimeInMillis() / 1000);
    res.setReset(resetCode);
    res.setFota(fotaCode);
    res.setFirmversion(firmVersion);

    try {

      result = oma.writeValueAsString(res);

    } catch (JsonProcessingException e) {
      logger.error("JSON E, ", e.getMessage());
    }

    return result;
  }

  public String getControlCommand(String mode, String modelType) {
    String controlCommand;

    String aiModeOn = "A1";
    String aiModeOff = "A0";

    switch (modelType) {
      case "TAES": // 태성 환기청정기
        if (mode.equals("A0")) {
          controlCommand = "CCMD" + aiModeOff + "P1";
        } else if (mode.equals("A1")) {
          controlCommand = "CCMD" + aiModeOn + "W1";
        } else {
          controlCommand = "CCMD" + aiModeOff + mode;
        }
        break;
      case "AHU":  // 성남 공조기
        if (mode.equals("A0")) {
          controlCommand = "CCMD" + aiModeOff + "00";
        } else if (mode.equals("A1")) {
          controlCommand = "CCMD" + aiModeOn + "00";
        } else {
          controlCommand = "CCMD" + aiModeOff + mode;
        }
        break;
      case "KWG-ST1":  // 수직정원 (삼성)
        if (mode.equals("A0")) {
          controlCommand = "CCMD" + aiModeOff + "P0";
        } else if (mode.equals("A1")) {
          controlCommand = "CCMD" + aiModeOn + "00";
        } else {
          controlCommand = "CCMD" + aiModeOff + mode;
        }
        break;
      case "KESR":  // NET 인증
        if (mode.equals("A0")) {
          controlCommand = "CCMD" + aiModeOff + "P1";
        } else if (mode.equals("A1")) {
          controlCommand = "CCMD" + aiModeOn + "W1";
        } else {
          controlCommand = "CCMD" + aiModeOff + mode;
        }
        break;
      case "KWV-AIC1":  // AI Controller 1번 모델
        if (mode.equals("A0")) {
          controlCommand = "CCMD" + aiModeOff + "P1";
        } else if (mode.equals("A1")) {
          controlCommand = "CCMD" + aiModeOn + "W1";
        } else {
          controlCommand = "CCMD" + aiModeOff + mode;
        }
        break;
      case "HUM":  // 휴미컨 (서울산업진흥원)
        controlCommand = "CCMD" + mode;
        break;
      default:
        controlCommand = "CMD" + mode;
        break;
    }

    logger.error("command log :: {}", controlCommand);
    String result;
    int asciCode = 0;
    String checkSumStr;

    char[] ch = controlCommand.toCharArray();
    for (char c : ch) {
      asciCode += (byte) c;
    }

    int checkSum = asciCode % 256;

    checkSumStr = String.format("%02X", checkSum);

    checkSumStr = checkSumStr.substring(checkSumStr.length() - 1);
    checkSumStr = checkSumStr.toUpperCase();

    result = controlCommand.concat(checkSumStr).concat("=");

    return result;
  }

  public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
    JSONObject jsonObject = new JSONObject();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      try {

        jsonObject.put(key, value);

      } catch (JSONException e) {
        logger.debug("JSON E, " + e.getMessage());
      }
    }

    return jsonObject;
  }

  public boolean ventCodeCheck(String model, String code) {
    boolean result = false;

    switch (model) {
      case "JNT":
        if (Arrays.asList(CommonConstant.VENT_JNT_STATUS_CODE).contains(code)) {
          result = true;
        }
        break;
      case "TAES":
        if (Arrays.asList(CommonConstant.VENT_TAES_STATUS_CODE).contains(code)) {
          result = true;
        }
        break;
      case "AHU":
        if (Arrays.asList(CommonConstant.VENT_AHU_STATUS_CODE).contains(code)) {
          result = true;
        }
        break;
      case "KWG-ST1":
        if (Arrays.asList(CommonConstant.VENT_KWG_STATUS_CODE).contains(code)) {
          result = true;
        }
        break;
      case "KWV-AIC1":
        if (Arrays.asList(CommonConstant.VENT_KWV_AIC1_STATUS_CODE).contains(code)) {
          result = true;
        }
        break;
      case "KESR":
        if (Arrays.asList(CommonConstant.VENT_KESR_STATUS_CODE).contains(code)) {
          result = true;
        }
        break;
      case "HUM":
        if (code.length() == 9) {
          result = true;
        }
        break;
      default:
        break;
    }

    return result;
  }

  public List<CollectionDto> collectionDeviceList() {
    List<CollectionDto> deviceList = mapper.selectCollectionDevice("00", "N");
    deviceList.addAll(mapper.selectCollectionDeviceVent());
    return deviceList;
  }

  public synchronized void downloadPlatformDataMultiDownload(String deviceType, String serial,
      String startTime,
      String endTime,
      String standard, HttpServletResponse res) throws Exception {
    logger.error("Thread LOG :: {}", Thread.currentThread().getId());

    SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
    SimpleDateFormat formatSdf = new SimpleDateFormat("yyyyMMdd");

    SXSSFWorkbook workbook = new SXSSFWorkbook(10000);
    workbook.setCompressTempFiles(true);
    List<ResultCollectionHisVo> datas;
    List<DeviceElements> columns = new ArrayList<>();

    String ciType = deviceType.equals("iaq") ? "cici" : "coci";

    try {

      if (!deviceType.equals("vent")) {
        DeviceElements tmColum = new DeviceElements();
        tmColum.setKorName("데이터 시간");
        tmColum.setEngName("formatTimestamp");
        columns.add(tmColum);
        columns.addAll(datacenterService.selectDeviceModelElements(serial));
        DeviceElements ciColum = new DeviceElements();
        ciColum.setKorName("통합지수");
        ciColum.setEngName(ciType);
        columns.add(ciColum);

        DeviceElements pm10RawColum = new DeviceElements();
        pm10RawColum.setKorName("미세먼지 (원본)");
        pm10RawColum.setEngName("pm10_raw");
        columns.add(pm10RawColum);

        DeviceElements pm25RawColum = new DeviceElements();
        pm25RawColum.setKorName("초미세먼지 (원본)");
        pm25RawColum.setEngName("pm25_raw");
        columns.add(pm25RawColum);

      } else {
        columns = new ArrayList<>();

        DeviceElements tmColum = new DeviceElements();
        tmColum.setKorName("데이터 시간");
        tmColum.setEngName("formatTimestamp");
        columns.add(tmColum);
        DeviceElements c1 = new DeviceElements();
        c1.setKorName("자동모드");
        c1.setEngName("auto_mode");
        columns.add(c1);
        DeviceElements c2 = new DeviceElements();
        c2.setKorName("전원");
        c2.setEngName("power");
        columns.add(c2);
        DeviceElements c3 = new DeviceElements();
        c3.setKorName("풍량");
        c3.setEngName("air_volume");
        columns.add(c3);
        DeviceElements c4 = new DeviceElements();
        c4.setKorName("배기모드");
        c4.setEngName("exh_mode");
        columns.add(c4);
        DeviceElements c5 = new DeviceElements();
        c5.setKorName("필터알림");
        c5.setEngName("filter_alarm");
        columns.add(c5);
        DeviceElements c6 = new DeviceElements();
        c6.setKorName("공기청정기 모드");
        c6.setEngName("air_mode");
        columns.add(c6);
        DeviceElements c7 = new DeviceElements();
        c7.setKorName("화재 경보");
        c7.setEngName("fire_alarm");
        columns.add(c7);
        DeviceElements c8 = new DeviceElements();
        c8.setKorName("수위 경보");
        c8.setEngName("water_alarm");
        columns.add(c8);
        DeviceElements c9 = new DeviceElements();
        c9.setKorName("장비 상태");
        c9.setEngName("dev_stat");
        columns.add(c9);
      }

      if (standard.equals("1d-avg-none") || standard.equals("1n-avg-none")) {
        datas = selectSensorHistoryDayMonth(deviceType, serial, startTime, endTime, standard, "0");
      } else {
        datas = selectSensorApi(deviceType, serial, startTime, endTime, standard, "0");
      }

      platFormDataSheetMaker(workbook, serial, columns, datas);

      String startDtFormat = formatSdf.format(parseSdf.parse(startTime));
      Calendar cal = Calendar.getInstance();
      cal.setTime(parseSdf.parse(endTime));
      cal.add(Calendar.DATE, 1);
      String endDtFormat = formatSdf.format(cal.getTime());

      res.setHeader("Set-Cookie", "fileDownload=true; path=/");
      res.setHeader("Content-Disposition",
          "attachment;fileName=" + serial + "_" + startDtFormat + "_" + endDtFormat + ".xlsx");
      res.setContentType("application/vnd.ms-excel");

      OutputStream out = new BufferedOutputStream(res.getOutputStream());
      workbook.write(out);
      out.flush();
      out.close();
      logger.error("SUCCESS");

    } catch (Exception e) {
      logger.error("ERROR?");
      res.setHeader("Set-Cookie", "fileDownload=false; path=/");
      res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      res.setHeader("Content-Type", "text/html; charset=utf-8");
      OutputStream out = null;

      try {

        out = res.getOutputStream();
        byte[] data = "fail..".getBytes();
        out.write(data, 0, data.length);

      } catch (Exception e2) {
        e2.printStackTrace();

      } finally {

        if (out != null) {
          try {

            out.close();

          } catch (Exception ignore) {
            logger.error("ERROR EXCEL DOWNLOAD");
          }
        }
      }

    } finally {
      workbook.dispose();
      workbook.close();

    }
  }

  public void platFormDataSheetMaker(SXSSFWorkbook workbook, String deviceSerial,
      List<DeviceElements> columns,
      List<ResultCollectionHisVo> datas) throws Exception {
    int colCount = columns.size();

    int rowIndex = 0;
    int excelHead = 0;

    ObjectMapper objectMapper = new ObjectMapper();

    Sheet sheet = workbook.createSheet(deviceSerial);
    SimpleDateFormat preSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    for (int dataIndex = 0; dataIndex <= datas.size(); dataIndex++) {

      try {

        Row row;
        row = sheet.createRow(rowIndex);

        if (excelHead == 0) {
          excelHead++;
          for (int i = 0; i < colCount; i++) {
            Cell cell = row.createCell(i);
            String convertUnit = "";
            if (columns.get(i).getElementUnit() != null) {
              convertUnit = "(".concat(columns.get(i).getElementUnit()).concat(")");
            }

            cell.setCellValue(columns.get(i).getKorName().concat(convertUnit));
          }

        } else {
          ResultCollectionHisVo obj = datas.get(dataIndex - 1);
          HashMap<String, Object> mpData = objectMapper.convertValue(obj, HashMap.class);
          String cellValue;
          for (int i = 0; i < colCount; i++) {
            if ("formatTimestamp".equals(columns.get(i).getEngName())) {
              cellValue = mpData.get((columns.get(i).getEngName())) == null ? ""
                  : mpData.get((columns.get(i).getEngName())).toString();

              if (!"".equals(cellValue)) {
                cellValue = cellValue.substring(0, cellValue.length() - 3);
              }

            } else if ("temp".equals(columns.get(i).getEngName())) {
              cellValue = mpData.get((columns.get(i).getEngName())) == null ? ""
                  : mpData.get((columns.get(i).getEngName())).toString();

            } else {
              cellValue = mpData.get((columns.get(i).getEngName())) == null ? ""
                  : String.valueOf(Math.round((Double) mpData.get((columns.get(i).getEngName()))));
            }

            Cell cell = row.createCell(i);
            cell.setCellValue(cellValue);
          }
        }

        rowIndex++;

      } catch (Exception e) {
        logger.error("null");
      }
    }
  }

  public String getHumErrorHexCodeParsingToString(String codeStr, String parsingType) {
    String result = "";
    List<HashMap<String, Object>> dataList = new ArrayList<>();

    HashMap<String, Object> e7 = new HashMap<String, Object>();
    e7.put("message", "압축기 고압에러");
    e7.put("name", "고압SW");
    e7.put("code", "E7");
    e7.put("hexCode", 0x1000);
    e7.put("hexCodeStr", "0x1000");
    e7.put("byteList", hexToBin("1000"));

    HashMap<String, Object> e8 = new HashMap<String, Object>();
    e8.put("message", "물막힘");
    e8.put("name", "배수에러");
    e8.put("code", "E8");
    e8.put("hexCode", 0x0002);
    e8.put("hexCodeStr", "0x0002");
    e8.put("byteList", hexToBin("0002"));

    HashMap<String, Object> e10 = new HashMap<String, Object>();
    e10.put("message", "압축기 저온에러");
    e10.put("name", "실내저온");
    e10.put("code", "E10");
    e10.put("hexCode", 0x2000);
    e10.put("hexCodeStr", "0x2000");
    e10.put("byteList", hexToBin("2000"));

    HashMap<String, Object> e11 = new HashMap<String, Object>();
    e11.put("message", "필터교체 에러");
    e11.put("name", "필터교체");
    e11.put("code", "E11");
    e11.put("hexCode", 0x4000);
    e11.put("hexCodeStr", "0x4000");
    e11.put("byteList", hexToBin("4000"));

    dataList.add(e7);
    dataList.add(e8);
    dataList.add(e10);
    dataList.add(e11);

    int code = Integer.parseInt(codeStr);
    int hexCode = Integer.valueOf(Integer.toHexString(code));
    String[] hexCodeToByte = hexToBin(String.valueOf(hexCode));

    for (int i = 0; i < 4; i++) {
      String binaryStr = hexCodeToByte[i];
      for (HashMap<String, Object> eh : dataList) {
        String diffBinaryStr = ((String[]) eh.get("byteList"))[i];
        if ((Integer.valueOf(binaryStr) & Integer.valueOf(diffBinaryStr)) != 0) {
          eh.remove("hexCode");
          if ("".equals(result)) {
            result += eh.get(parsingType).toString();

          } else {
            result += ",".concat(eh.get(parsingType).toString());
          }
        }
      }
    }

    return result;
  }

  private String[] hexToBin(String hex) {
    String[] bin = new String[hex.length()];
    String binFragment = "";
    int iHex;
    hex = hex.trim();
    hex = hex.replaceFirst("0x", "");

    for (int i = 0; i < hex.length(); i++) {
      iHex = Integer.parseInt("" + hex.charAt(i), 16);
      binFragment = Integer.toBinaryString(iHex);

      while (binFragment.length() < 4) {
        binFragment = "0" + binFragment;
      }

      bin[i] = binFragment;
    }

    return bin;
  }
}
