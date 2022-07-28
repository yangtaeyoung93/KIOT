package com.airguard.service.app.v2;

import com.airguard.util.AES256Util;
import com.airguard.exception.ExternalApiException;
import com.airguard.exception.ParameterException;
import com.airguard.exception.SQLException;
import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.model.app.AppVent;
import com.airguard.model.platform.SensorDataDto;
import com.airguard.model.system.DeviceElements;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Vent;
import com.airguard.service.app.VentService;
import com.airguard.mapper.readonly.DatacenterMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.airguard.util.JsonUtil;
import com.airguard.util.KweatherElemeniUtil;
import com.airguard.util.KweatherElementMessageManageUtil;
import com.airguard.util.WeatherApiUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class Air365StationV2Service {

  private static final Logger logger = LoggerFactory.getLogger(Air365StationV2Service.class);

  @Autowired
  StationMapper mapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  @Autowired
  MemberDeviceMapper memberDeviceMapper;

  @Autowired
  private DatacenterMapper dataCenterMapper;

  @Autowired
  private VentService appVentService;

  public LinkedHashMap<String, Object> selectStationList(String userId, String userType)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    List<Map<String, Object>> datas = new ArrayList<>();

    Map<String, Object> deviceInfo;
    List<Map<String, Object>> ventDeviceList;
    Map<String, Object> ventDeviceInfo;

    switch (userType) {
      case "member":
        String memberIdx = readOnlyMapper.selectUserIdx(userId);

        for (MemberDevice md : readOnlyMapper.selectMemberDeviceOne(memberIdx)) {
          deviceInfo = new LinkedHashMap<>();
          deviceInfo.put("stationSerial",
                  md.getSerialNum() == null ? CommonConstant.NULL_DATA : md.getSerialNum());
          deviceInfo.put("stationType",
                  md.getDeviceType() == null ? CommonConstant.NULL_DATA : md.getDeviceType());
          deviceInfo.put("lon", md.getLon() == null ? CommonConstant.NULL_DATA : md.getLon());
          deviceInfo.put("lat", md.getLat() == null ? CommonConstant.NULL_DATA : md.getLat());

          ventDeviceList = new ArrayList<>();
          if ("IAQ".equals(md.getDeviceType())) {
            for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(md.getDeviceIdx())) {
              ventDeviceInfo = new LinkedHashMap<>();
              ventDeviceInfo.put("ventSerial",
                      v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());

              ventDeviceList.add(ventDeviceInfo);
            }

            deviceInfo.put("vents", ventDeviceList);
          }

          datas.add(deviceInfo);
        }

        res.put("memberId", userId);
        res.put("data", datas);
        break;

      case "group":
        String groupIdx = readOnlyMapper.getGroupIdToGroupIdx(userId);

        List<HashMap<String, Object>> groupData = new ArrayList<>();
        for (Map<String, Object> groupMember : readOnlyMapper.selectGroupMembers(groupIdx)) {
          datas = new ArrayList<>();

          String groupMemberIdx = groupMember.get("memberIdx").toString();
          String groupMemberId = groupMember.get("userId").toString();

          HashMap<String, Object> groupMemberInfo = new LinkedHashMap<>();
          for (MemberDevice md : readOnlyMapper.selectMemberDeviceOne(groupMemberIdx)) {
            deviceInfo = new LinkedHashMap<>();
            deviceInfo.put("stationSerial",
                    md.getSerialNum() == null ? CommonConstant.NULL_DATA : md.getSerialNum());
            deviceInfo.put("stationType",
                    md.getDeviceType() == null ? CommonConstant.NULL_DATA : md.getDeviceType());
            deviceInfo.put("lon", md.getLon() == null ? CommonConstant.NULL_DATA : md.getLon());
            deviceInfo.put("lat", md.getLat() == null ? CommonConstant.NULL_DATA : md.getLat());

            if ("IAQ".equals(md.getDeviceType())) {
              ventDeviceList = new ArrayList<>();
              for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(md.getDeviceIdx())) {
                ventDeviceInfo = new LinkedHashMap<>();
                ventDeviceInfo.put("ventSerial",
                        v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());

                ventDeviceList.add(ventDeviceInfo);
              }

              deviceInfo.put("vents", ventDeviceList);
            }

            datas.add(deviceInfo);
          }

          groupMemberInfo.put("memberId", groupMemberId);
          groupMemberInfo.put("memberData", datas);

          groupData.add(groupMemberInfo);
        }

        res.put("groupId", userId);
        res.put("data", groupData);
        break;

      default:
        throw new ParameterException(ParameterException.ILLEGAL_TYPE_PARAMETER_EXCEPTION);
    }

    res.put("result", CommonConstant.R_SUCC_CODE);
    return res;
  }

  public LinkedHashMap<String, Object> insertStation(HashMap<String, Object> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    int count;
    int resultCode;
    String deviceIdx;
    String memberIdx;
    HashMap<String, Object> data = new LinkedHashMap<>();
    HashMap<String, String> deviceData;

    try {

      deviceIdx = Integer
              .toString(readOnlyMapper.getDeviceInfoBySerial((String) req.get("serial")).getIdx());
      memberIdx = readOnlyMapper.findByUsername((String) req.get("userId")).getIdx();
      count = readOnlyMapper.memberDeviceCheck(memberIdx, deviceIdx);

    } catch (Exception e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    if (count != 0) {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    }

    try {

      resultCode = memberDeviceMapper.insertMemberDeviceApp(req);

      if (resultCode == 1) {
        deviceData = readOnlyMapper.selectDeviceToSerial(deviceIdx);

        data.put("deviceIdx", Integer.parseInt(deviceIdx));
        data.put("deviceType", deviceData.get("device_type"));
        data.put("serial", req.get("serial"));
        data.put("stationName", req.get("stationName"));
      }

    } catch (DuplicateKeyException e) {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    res.put("data", data);
    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> insertStationEncodeVersion(HashMap<String, Object> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    int count;
    int resultCode;
    String deviceIdx;
    String memberIdx;
    HashMap<String, Object> data = new LinkedHashMap<>();
    HashMap<String, String> deviceData;

    try {

      deviceIdx = Integer
              .toString(readOnlyMapper.getDeviceInfoBySerial((String) req.get("serial")).getIdx());
      memberIdx = readOnlyMapper.findByUsername((String) req.get("userId")).getIdx();
      count = readOnlyMapper.memberDeviceCheck(memberIdx, deviceIdx);

    } catch (Exception e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    if (count != 0) {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    }

    try {

      resultCode = memberDeviceMapper.insertMemberDeviceApp(req);

      if (resultCode == 1) {
        deviceData = readOnlyMapper.selectDeviceToSerial(deviceIdx);

        data.put("deviceIdx", AES256Util.encrypt(deviceIdx));
        data.put("deviceType", AES256Util.encrypt(deviceData.get("device_type")));
        data.put("serial", AES256Util.encrypt(req.get("serial")+""));
        data.put("stationName", AES256Util.encrypt(req.get("stationName")+""));
      }

    } catch (DuplicateKeyException e) {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);
    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    res.put("data", data);
    res.put("result", resultCode);
    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public LinkedHashMap<String, Object> deleteStation(String serial) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    try {

      String deviceIdx = readOnlyMapper.getSerialToDeviceIdx(serial);

      mapper.delMemberDevice(deviceIdx);

      for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(deviceIdx)) {
        memberDeviceMapper.deleteMemberDeviceVent(v.getVentDeviceIdx());
      }

      res.put("result", CommonConstant.R_SUCC_CODE);

    } catch (Exception e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    return res;
  }

  public LinkedHashMap<String, Object> updateStation(HashMap<String, Object> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    try {
      if(req.getOrDefault("lat",null)!=null && req.getOrDefault("lon",null)!=null) {
        String lat = req.get("lat").toString();
        String lon = req.get("lon").toString();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        headers.add("auth", "a3dlYXRoZXItYXBwLWF1dGg=");


        URI kwapiUrl = URI.create("https://kwapi.kweather.co.kr/v1/gis/geo/loctoaddr?lat=" + lat + "&lon=" + lon);

        RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, kwapiUrl);
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        JSONObject jObj = new JSONObject(responseEntity.getBody());
        JSONObject data = jObj.getJSONObject("data");
        String hang_cd = data.getString("hang_cd");
        String sido_nm = data.getString("sido_nm");
        String sg_nm = data.getString("sg_nm");
        String emd_nm = data.getString("emd_nm");

        req.put("dcode", hang_cd);
      }
      memberDeviceMapper.updateMemberDevice(req);

      res.put("result", CommonConstant.R_SUCC_CODE);

    }catch(JSONException e){
      throw new ParameterException(ParameterException.ILLEGAL_LAT_LON_PARAMETER_EXCEPTION);
    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    return res;
  }


  public LinkedHashMap<String, Object> insertVentConnect(HashMap<String, Object> req)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    try {

      String deviceTypeIdx = readOnlyMapper.getDeviceInfoBySerial((String) req.get("serial"))
              .getDeviceTypeIdx();
      String ventTypeIdx = readOnlyMapper.getDeviceInfoBySerial((String) req.get("ventSerial"))
              .getDeviceTypeIdx();
      if (!deviceTypeIdx.equals("1") || !ventTypeIdx.equals("7")) {
        throw new ParameterException(ParameterException.ILLEGAL_SERIAL_PARAMETER_EXCEPTION);
      }

    } catch (Exception e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    String memberIdx = readOnlyMapper.getUserIdToUserIdx(req.get("userId").toString());
    String deviceIdx = readOnlyMapper.getSerialToDeviceIdx(req.get("serial").toString());
    String ventDeviceIdx = readOnlyMapper.getSerialToDeviceIdx(req.get("ventSerial").toString());

    try {

      memberDeviceMapper.insertMemberDeviceVent(memberIdx, deviceIdx, ventDeviceIdx);

    } catch (DuplicateKeyException e) {
      throw new SQLException(SQLException.DUPLICATE_TARGET_EXCEPTION);

    }

    res.put("result", CommonConstant.R_SUCC_CODE);
    return res;
  }

  public LinkedHashMap<String, Object> deleteVentConnect(HashMap<String, Object> req)
          throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    try {

      String ventDeviceIdx = readOnlyMapper.getSerialToDeviceIdx(req.get("ventSerial").toString());

      memberDeviceMapper.deleteMemberDeviceVent(ventDeviceIdx);

    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    res.put("result", CommonConstant.R_SUCC_CODE);
    return res;
  }

  public Map<String, Object> getSerialToPlatFormData(String deviceType, String serialNum) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> resultData = new LinkedHashMap<>();
    HttpHeaders headers = new HttpHeaders();

    String paramType = "";
    switch (deviceType.toUpperCase()) {
      case "IAQ":
        paramType = CommonConstant.PARAM_SENSOR_IAQ;
        break;
      case "OAQ":
        paramType = CommonConstant.PARAM_SENSOR_OAQ;
        break;
      case "DOT":
        paramType = CommonConstant.PARAM_SENSOR_DOT;
        break;
    }

    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR)
            .concat(paramType).concat("/").concat(serialNum));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    if (jObj.has("data")) {
      resultData = JsonUtil.JsonToMap(jObj.get("data").toString());

      long timestamp = Long.parseLong(jObj.getJSONObject("service").get("timestamp").toString());
      String receiveDateTime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date(timestamp * 1000));
      resultData.put("timestamp", timestamp);

      resultData.put("cusTm", receiveDateTime);
      resultData.put("resultCode", 1);

    } else {
      resultData.put("resultCode", 0);
    }

    return resultData;
  }

  public int air365PremiumCheckProcessF(String serial, String dateStr, String checkType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    JSONObject parsingJsonData;

    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(new  StringBuilder("https://www.air365.co.kr/api/premium/check?serialNum=").append(serial)
            .append("&createDt=").append(dateStr).toString());

    try {

      RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);

      logger.error("AIR365 API, Response :: {}", res);
      parsingJsonData = new JSONObject(res.getBody().toString());

    } catch (Exception e) {
      logger.error("AIR365 Application, ERROR :: {}", e.getMessage());
      parsingJsonData = new JSONObject("{\r\n\"STATS\": 1,\r\n\"REPORT\": 1,\r\n\"CONTROL\": 1\r\n}");
    }

    return Integer.parseInt(parsingJsonData.get(checkType).toString());
  }

  public LinkedHashMap<String, Object> getIotDataDetail(String serial, String region) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    Map<String, Object> collectionData;

    List<LinkedHashMap<String, Object>> elementDatas, elementDatas2;
    LinkedHashMap<String, Object> elementData;
    List<LinkedHashMap<String, String>> ventDatas;
    LinkedHashMap<String, String> ventData;

    LinkedHashMap<String, Object> weatherData = new LinkedHashMap<>();

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    HashMap<String, Object> criteriaMp = new LinkedHashMap<>();
    criteriaMp.put("pm10", new Integer[]{0, 30, 80, 150, 600});
    criteriaMp.put("pm25", new Integer[]{0, 15, 35, 75, 500});
    criteriaMp.put("co2", new Integer[]{0, 500, 1000, 1500, 10000});
    criteriaMp.put("voc", new Integer[]{0, 200, 400, 1000, 10000});
    criteriaMp.put("noise", new Integer[]{0, 30, 55, 70, 100});
    criteriaMp.put("temp", new Integer[]{21, 24, 27, 30, 34, 21, 18, 16, 14, 0}); // 더움, 추움
    criteriaMp.put("humi", new Integer[]{50, 60, 75, 90, 100, 50, 40, 35, 20, 0}); // 습함, 건조

    String[] elNormalArray = {"pm10", "pm25", "co2", "voc", "noise"};
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(10 * 1000);
    factory.setReadTimeout(30 * 1000);
    RestTemplate restTemplate = new RestTemplate(factory);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", new StringBuilder(MediaType.APPLICATION_FORM_URLENCODED_VALUE).append(";charset=UTF-8").toString());
    try {

      String deviceType = String.valueOf(deviceInfo.get("device_type"));
      String serialNum = String.valueOf(deviceInfo.get("serial_num"));
      String stationName = String.valueOf(deviceInfo.get("station_name"));
      String deviceIdx = String.valueOf(deviceInfo.get("device_idx"));
      String ci = "IAQ".equals(deviceType.toUpperCase())? "cici" : "coci";

      collectionData = getSerialToPlatFormData(deviceType, serialNum);
      ventDatas = new ArrayList<>();
      if ("IAQ".equals(deviceType.toUpperCase())) {
        for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(deviceIdx)) {
          ventData = new LinkedHashMap<>();
          ventData.put("ventDeviceIdx", v.getVentDeviceIdx() == null ? CommonConstant.NULL_DATA : v.getVentDeviceIdx());
          ventData.put("ventSerial", v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());
          ventData.put("deviceModel", v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel());
          try {
            URI uri = URI.create(new StringBuilder(CommonConstant.API_SERVER_HOST_TOTAL)
                    .append(CommonConstant.SEARCH_PATH_SENSOR)
                    .append(CommonConstant.PARAM_SENSOR_VENT)
                    .append("/").append(v.getSerialNum()).toString());
            RequestEntity<String> req = null;
            ResponseEntity<String> res = null;
            req = new RequestEntity<>(headers, HttpMethod.GET, uri);
            res = restTemplate.exchange(req, String.class);
            JSONObject jo = new JSONObject(res.getBody()).getJSONObject("data");
            ventData.put("filter_alarm", jo.getString("filter_alarm"));
          }catch (Exception e){
            logger.error("JSON NOT FOUND === {}",serialNum);
          }
          ventDatas.add(ventData);
        }
      }

      String badTargetElements = "";
      elementDatas = new ArrayList<>();
      elementDatas2 = new ArrayList<>();
      logger.error("serial = {}",serial);
      logger.error("region = {}",region);

      for (DeviceElements el : dataCenterMapper.selectDeviceModelElements(serial)) {
        elementData = new LinkedHashMap<>();
        int elIndex;
        int elIndexOld;

        String elAction = "";

        Object elData;
        Object tempElData;

        int ciValue = -999;

        elData = (!collectionData.containsKey(el.getEngName()) ? null :
                Integer.valueOf(String.valueOf(Math.round(Double.parseDouble(collectionData.get(el.getEngName()).toString())))));

        tempElData = (!collectionData.containsKey(el.getEngName()) ? null :
                Math.round(Double.parseDouble(collectionData.get(el.getEngName()).toString()) * 10) / 10.0);

        if (Arrays.stream(elNormalArray).anyMatch(el.getEngName().toLowerCase()::equals) && elData != null)
          ciValue = KweatherElemeniUtil.elementCiCalculator(
                  Integer.valueOf((int) Math.round(Double.valueOf(elData.toString()))), el.getEngName());

        if ("temp".equals(el.getEngName()) || "humi".equals(el.getEngName())) {
          elIndex = !collectionData.containsKey(ci.concat("_").concat(el.getEngName())) ? 0
                  : KweatherElementMessageManageUtil.elementLevel(deviceType.toUpperCase(), el.getEngName(), Double.parseDouble(
                  collectionData.get(el.getEngName()).toString()));

        } else {
          if (ciValue != -999) {
            elIndex = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(ciValue));

          } else {
            elIndex = !collectionData.containsKey(ci.concat("_").concat(el.getEngName())) ? 0
                    : KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(
                    collectionData.get(ci.concat("_").concat(el.getEngName())).toString()));
          }
        }

        elementData.put("korName", el.getKorName() == null ? "NA" : el.getKorName().toString());
        elementData.put("engName", el.getEngName() == null ? "NA" : el.getEngName().toString());
        elementData.put("viewName", el.getViewName() == null ? "NA" : el.getViewName().toString());
        elementData.put("unit", el.getElementUnit() == null ? "NA" : el.getElementUnit().toString());

        elementData.put("criteria", criteriaMp.containsKey(el.getEngName()) ? criteriaMp.get(el.getEngName()) : criteriaMp.get("pm10"));

        elementData.put("value", !collectionData.containsKey(el.getEngName()) ? "NA" : (!"temp".equals(el.getEngName()) ? elData : tempElData));
        elementData.put("score", collectionData.containsKey(ci.concat("_").concat(el.getEngName())) ?
                Math.round(Double.parseDouble(collectionData.get(ci.concat("_").concat(el.getEngName())).toString()))
                : (ciValue != -999 ? ciValue : "NA"));

        elementData.put("grade", (elIndex == 0) ? "NA" : elIndex);

        switch (elIndex) {
          case 3:
            elIndexOld = 3;
            break;
          case 4:
            elIndexOld = 3;
            break;
          case 5:
            elIndexOld = 4;
            break;
          case 6:
            elIndexOld = 4;
            break;
          default:
            elIndexOld = elIndex;
            break;
        }

        if ("temp".equals(el.getEngName()) || "humi".equals(el.getEngName())) {
          elementData.put("grade4", (elIndexOld == 0) ? "NA" : elIndexOld);

        } else {
          elementData.put("grade4", (elIndex == 0) ? "NA" : elIndex);
        }

        elementData.put("index", (elIndex == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(el.getEngName(), String.valueOf(elIndex)));

        if (!Arrays.stream(KweatherElementMessageManageUtil.ELEMENT_TYPE_LIST).noneMatch(el.getEngName().toLowerCase()::equals)) {
          String[] diffArray;
          switch (el.getEngName().toLowerCase()) {
            case "pm10":
              diffArray = KweatherElementMessageManageUtil.PM10_ACTION;
              break;
            case "pm25":
              diffArray = KweatherElementMessageManageUtil.PM25_ACTION;
              break;
            case "co2":
              diffArray = KweatherElementMessageManageUtil.CO2_ACTION;
              break;
            case "voc":
              diffArray = KweatherElementMessageManageUtil.VOC_ACTION;
              break;
            case "noise":
              diffArray = KweatherElementMessageManageUtil.NOISE_ACTION;
              break;
            case "temp":
              diffArray = KweatherElementMessageManageUtil.TEMP_ACTION;
              break;
            case "humi":
              diffArray = KweatherElementMessageManageUtil.HUMI_ACTION;
              break;
            default:
              diffArray = KweatherElementMessageManageUtil.ETC_ACTION;
              break;
          }

          // App 이용 예외 처리
          elAction = (elIndex == 0) ? "NA" : diffArray[elIndex - 1];
          if (elIndex != 0 && elIndex > 2) {
            if (("".equals(badTargetElements))) {
              badTargetElements += " 다만, 더욱 쾌적한 환경을 위해서 아래의 행동 요령을 참고해 주세요. * ".concat(elAction);

            } else {
              badTargetElements += "* ".concat(elAction);
            }
          }

        } else {
          elAction = "NA";
        }

        elementData.put("action", elAction);

        if (Arrays.stream(elNormalArray).anyMatch(el.getEngName().toLowerCase()::equals) || "temp".equals(el.getEngName().toLowerCase()) ||
                "humi".equals(el.getEngName().toLowerCase())) {
          elementDatas.add(elementData);

        } else {
          elementDatas2.add(elementData);
        }
      }

      Map<String, Object> weatherParsingData = new LinkedHashMap<>();

      // App 이용 예외 처리 (기상 api 호출 및 파싱 대행)
      try {

        weatherParsingData = WeatherApiUtil.weatherTodayApi(region);

      } catch (Exception e) {
        logger.error("Weather TODAY API ERROR .");
      }

      String urlForDust = "http://kapi.kweather.co.kr/getXML_air_fcast_3times_area.php?mode=n&region="+region;

      URL url = new URL(urlForDust);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-type", "text/plain");

      if(connection.getResponseCode() == 200) {
        StringBuilder result = new StringBuilder ();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        while((line = in.readLine()) != null) {
          result.append(line);
        }
        in.close();
        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject outdoor = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) parser.parse(result.toString())).get("main");
        weatherData.put("pm10", outdoor.get("pm10Value").toString());
        weatherData.put("pm25", outdoor.get("pm25Value").toString());

      }

      weatherData.put("dongKo", weatherParsingData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherParsingData.get("P_1"));

      weatherData.put("icon", (weatherParsingData.get("P_2") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_2"));

      weatherData.put("wdws", (weatherParsingData.get("P_3") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_3"));

      weatherData.put("temp", (weatherParsingData.get("P_4") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_4"));

      weatherData.put("humi", (weatherParsingData.get("P_5") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_5"));

      weatherData.put("snowf", (weatherParsingData.get("P_6") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_6"));

      weatherData.put("rainf", (weatherParsingData.get("P_7") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_7"));

      int ciIndex = !collectionData.containsKey(ci) ? 0 :
              KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(collectionData.get(ci).toString()));

      resultData.put("deviceIdx", Integer.valueOf(deviceIdx));
      resultData.put("deviceSerial", serialNum);
      resultData.put("deviceType", deviceType);
      resultData.put("stationName", stationName);
      resultData.put("total", !collectionData.containsKey(ci) ? "NA" : Math.round(Double.parseDouble(collectionData.get(ci).toString())));
      resultData.put("totalIndex", KweatherElementMessageManageUtil.setElementLevelKorName("ci", String.valueOf(ciIndex)));
      resultData.put("totalGrade", (ciIndex == 0) ? "NA" : ciIndex);
      resultData.put("lastUpdated", !collectionData.containsKey("cusTm") ? "NA" : collectionData.get("cusTm").toString());
      resultData.put("receiveError", !collectionData.containsKey("timestamp") ? "NA" :
              ((Long.parseLong(collectionData.get("timestamp").toString())) + (5 * 60)) > (System.currentTimeMillis()));

      resultData.put("action", (ciIndex == 0) ? "NA" : KweatherElementMessageManageUtil.CI_ACTION[ciIndex - 1]
              .concat(" 다만, 더욱 쾌적한 환경을 위해서 아래의 행동 요령을 참고해 주세요. ").concat(badTargetElements));

      resultData.put("vents", ventDatas);
      resultData.put("elements", elementDatas);
      resultData.put("elementsAddition", elementDatas2);
      resultData.put("weather", weatherData);

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    return resultData;
  }


  public LinkedHashMap<String, Object> getIotDataDetailEncodeVersion(String serial, String region) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    Map<String, Object> collectionData;

    List<LinkedHashMap<String, Object>> elementDatas, elementDatas2;
    LinkedHashMap<String, Object> elementData;
    List<LinkedHashMap<String, String>> ventDatas;
    LinkedHashMap<String, String> ventData;

    LinkedHashMap<String, Object> weatherData = new LinkedHashMap<>();

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    HashMap<String, Object> criteriaMp = new LinkedHashMap<>();
    criteriaMp.put("pm10", new String[]{AES256Util.encrypt("0"), AES256Util.encrypt("30"), AES256Util.encrypt("80"), AES256Util.encrypt("150"), AES256Util.encrypt("600")});
    criteriaMp.put("pm25", new String[]{AES256Util.encrypt("0"), AES256Util.encrypt("15"), AES256Util.encrypt("35"), AES256Util.encrypt("75"), AES256Util.encrypt("500")});
    criteriaMp.put("co2", new String[]{AES256Util.encrypt("0"), AES256Util.encrypt("500"), AES256Util.encrypt("1000"), AES256Util.encrypt("1500"), AES256Util.encrypt("10000")});
    criteriaMp.put("voc", new String[]{AES256Util.encrypt("0"), AES256Util.encrypt("200"), AES256Util.encrypt("400"), AES256Util.encrypt("1000"), AES256Util.encrypt("10000")});
    criteriaMp.put("noise", new String[]{AES256Util.encrypt("0"), AES256Util.encrypt("30"), AES256Util.encrypt("55"), AES256Util.encrypt("70"), AES256Util.encrypt("100")});
    criteriaMp.put("temp", new String[]{AES256Util.encrypt("21"), AES256Util.encrypt("24"), AES256Util.encrypt("27"), AES256Util.encrypt("30"), AES256Util.encrypt("34"), AES256Util.encrypt("21"), AES256Util.encrypt("18"), AES256Util.encrypt("16"), AES256Util.encrypt("14"), AES256Util.encrypt("0")}); // 더움, 추움
    criteriaMp.put("humi", new String[]{AES256Util.encrypt("50"), AES256Util.encrypt("60"), AES256Util.encrypt("75"), AES256Util.encrypt("90"), AES256Util.encrypt("100"), AES256Util.encrypt("50"), AES256Util.encrypt("40"), AES256Util.encrypt("35"), AES256Util.encrypt("20"), AES256Util.encrypt("0")}); // 습함, 건조

    String[] elNormalArray = {"pm10", "pm25", "co2", "voc", "noise"};

    try {

      String deviceType = String.valueOf(deviceInfo.get("device_type"));
      String serialNum = String.valueOf(deviceInfo.get("serial_num"));
      String stationName = String.valueOf(deviceInfo.get("station_name"));
      String deviceIdx = String.valueOf(deviceInfo.get("device_idx"));
      String ci = "IAQ".equals(deviceType.toUpperCase())? "cici" : "coci";

      collectionData = getSerialToPlatFormData(deviceType, serialNum);

      HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
      factory.setConnectTimeout(10 * 1000);
      factory.setReadTimeout(30 * 1000);
      RestTemplate restTemplate = new RestTemplate(factory);
      HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.add("Content-Type", new StringBuilder(MediaType.APPLICATION_FORM_URLENCODED_VALUE).append(";charset=UTF-8").toString());

      ventDatas = new ArrayList<>();
      if ("IAQ".equals(deviceType.toUpperCase())) {
        for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(deviceIdx)) {
          ventData = new LinkedHashMap<>();
          ventData.put("ventDeviceIdx", AES256Util.encrypt(v.getVentDeviceIdx() == null ? CommonConstant.NULL_DATA : v.getVentDeviceIdx()));
          ventData.put("ventSerial", AES256Util.encrypt(v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum()));
          ventData.put("deviceModel", AES256Util.encrypt(v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel()));

            try {
              URI uri = URI.create(new StringBuilder(CommonConstant.API_SERVER_HOST_TOTAL).append(CommonConstant.SEARCH_PATH_SENSOR).append(CommonConstant.PARAM_SENSOR_VENT).append("/").append(v.getSerialNum()).toString());
              RequestEntity<String> req = null;
              ResponseEntity<String> res = null;
              req = new RequestEntity<>(headers, HttpMethod.GET, uri);
              res = restTemplate.exchange(req, String.class);
              JSONObject jo = new JSONObject(res.getBody()).getJSONObject("data");
              ventData.put("filter_alarm", jo.getString("filter_alarm"));
            }catch (Exception e){
            e.printStackTrace();
            }


          ventDatas.add(ventData);
        }
      }

      String badTargetElements = "";
      elementDatas = new ArrayList<>();
      elementDatas2 = new ArrayList<>();

      for (DeviceElements el : dataCenterMapper.selectDeviceModelElements(serial)) {
        elementData = new LinkedHashMap<>();
        int elIndex;
        int elIndexOld;

        String elAction = "";

        Object elData;
        Object tempElData;

        int ciValue = -999;

        elData = (!collectionData.containsKey(el.getEngName()) ? null :
                Integer.valueOf(String.valueOf(Math.round(Double.parseDouble(collectionData.get(el.getEngName()).toString())))));

        tempElData = (!collectionData.containsKey(el.getEngName()) ? null :
                Math.round(Double.parseDouble(collectionData.get(el.getEngName()).toString()) * 10) / 10.0);

        if (Arrays.stream(elNormalArray).anyMatch(el.getEngName().toLowerCase()::equals) && elData != null)
          ciValue = KweatherElemeniUtil.elementCiCalculator(
                  Integer.valueOf((int) Math.round(Double.valueOf(elData.toString()))), el.getEngName());

        if ("temp".equals(el.getEngName()) || "humi".equals(el.getEngName())) {
          elIndex = !collectionData.containsKey(ci.concat("_").concat(el.getEngName())) ? 0
                  : KweatherElementMessageManageUtil.elementLevel(deviceType.toUpperCase(), el.getEngName(), Double.parseDouble(
                  collectionData.get(el.getEngName()).toString()));

        } else {
          if (ciValue != -999) {
            elIndex = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(ciValue));

          } else {
            elIndex = !collectionData.containsKey(ci.concat("_").concat(el.getEngName())) ? 0
                    : KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(
                    collectionData.get(ci.concat("_").concat(el.getEngName())).toString()));
          }
        }

        elementData.put("korName", AES256Util.encrypt(el.getKorName() == null ? "NA" : el.getKorName().toString()));
        elementData.put("engName", AES256Util.encrypt(el.getEngName() == null ? "NA" : el.getEngName().toString()));
        elementData.put("viewName", AES256Util.encrypt(el.getViewName() == null ? "NA" : el.getViewName().toString()));
        elementData.put("unit", AES256Util.encrypt(el.getElementUnit() == null ? "NA" : el.getElementUnit().toString()));

        elementData.put("criteria", criteriaMp.containsKey(el.getEngName()) ? criteriaMp.get(el.getEngName()) : criteriaMp.get("pm10"));

        elementData.put("value", AES256Util.encrypt(!collectionData.containsKey(el.getEngName()) ? "NA" : (!"temp".equals(el.getEngName()) ? elData : tempElData).toString()));
        elementData.put("score", AES256Util.encrypt(collectionData.containsKey(ci.concat("_").concat(el.getEngName())) ?
                Math.round(Double.parseDouble(collectionData.get(ci.concat("_").concat(el.getEngName())).toString()))+""
                : (ciValue != -999 ? ciValue+"" : "NA")));

        elementData.put("grade", AES256Util.encrypt((elIndex == 0) ? "NA" : elIndex+""));

        switch (elIndex) {
          case 3:
            elIndexOld = 3;
            break;
          case 4:
            elIndexOld = 3;
            break;
          case 5:
            elIndexOld = 4;
            break;
          case 6:
            elIndexOld = 4;
            break;
          default:
            elIndexOld = elIndex;
            break;
        }

        if ("temp".equals(el.getEngName()) || "humi".equals(el.getEngName())) {
          elementData.put("grade4", AES256Util.encrypt((elIndexOld == 0) ? "NA" : elIndexOld+""));

        } else {
          elementData.put("grade4", AES256Util.encrypt((elIndex == 0) ? "NA" : elIndex+""));
        }

        elementData.put("index", AES256Util.encrypt((elIndex == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(el.getEngName(), String.valueOf(elIndex))));

        if (!Arrays.stream(KweatherElementMessageManageUtil.ELEMENT_TYPE_LIST).noneMatch(el.getEngName().toLowerCase()::equals)) {
          String[] diffArray;
          switch (el.getEngName().toLowerCase()) {
            case "pm10":
              diffArray = KweatherElementMessageManageUtil.PM10_ACTION;
              break;
            case "pm25":
              diffArray = KweatherElementMessageManageUtil.PM25_ACTION;
              break;
            case "co2":
              diffArray = KweatherElementMessageManageUtil.CO2_ACTION;
              break;
            case "voc":
              diffArray = KweatherElementMessageManageUtil.VOC_ACTION;
              break;
            case "noise":
              diffArray = KweatherElementMessageManageUtil.NOISE_ACTION;
              break;
            case "temp":
              diffArray = KweatherElementMessageManageUtil.TEMP_ACTION;
              break;
            case "humi":
              diffArray = KweatherElementMessageManageUtil.HUMI_ACTION;
              break;
            default:
              diffArray = KweatherElementMessageManageUtil.ETC_ACTION;
              break;
          }

          // App 이용 예외 처리
          elAction = (elIndex == 0) ? "NA" : diffArray[elIndex - 1];
          if (elIndex != 0 && elIndex > 2) {
            if (("".equals(badTargetElements))) {
              badTargetElements += " 다만, 더욱 쾌적한 환경을 위해서 아래의 행동 요령을 참고해 주세요. * ".concat(elAction);

            } else {
              badTargetElements += "* ".concat(elAction);
            }
          }

        } else {
          elAction = "NA";
        }

        elementData.put("action", AES256Util.encrypt(elAction));

        if (Arrays.stream(elNormalArray).anyMatch(el.getEngName().toLowerCase()::equals) || "temp".equals(el.getEngName().toLowerCase()) ||
                "humi".equals(el.getEngName().toLowerCase())) {
          elementDatas.add(elementData);

        } else {
          elementDatas2.add(elementData);
        }
      }

      Map<String, Object> weatherParsingData = new LinkedHashMap<>();

      // App 이용 예외 처리 (기상 api 호출 및 파싱 대행)
      try {

        weatherParsingData = WeatherApiUtil.weatherTodayApi(region);

      } catch (Exception e) {
        logger.error("Weather TODAY API ERROR .");
      }

      String urlForDust = "http://kapi.kweather.co.kr/getXML_air_fcast_3times_area.php?mode=n&region="+region;
      URL url = new URL(urlForDust);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-type", "text/plain");

      if(connection.getResponseCode() == 200) {
        StringBuilder result = new StringBuilder ();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        while((line = in.readLine()) != null) {
          result.append(line);
        }
        in.close();
        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject outdoor = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) parser.parse(result.toString())).get("main");
        weatherData.put("pm10", AES256Util.encrypt(outdoor.get("pm10Value").toString()));
        weatherData.put("pm25", AES256Util.encrypt(outdoor.get("pm25Value").toString()));

      }

      weatherData.put("dongKo", AES256Util.encrypt(weatherParsingData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherParsingData.get("P_1")+""));

      weatherData.put("icon", AES256Util.encrypt((weatherParsingData.get("P_2") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_2")+""));

      weatherData.put("wdws", AES256Util.encrypt((weatherParsingData.get("P_3") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_3")+""));

      weatherData.put("temp", AES256Util.encrypt((weatherParsingData.get("P_4") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_4")+""));

      weatherData.put("humi", AES256Util.encrypt((weatherParsingData.get("P_5") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_5")+""));

      weatherData.put("snowf", AES256Util.encrypt((weatherParsingData.get("P_6") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_6")+""));

      weatherData.put("rainf", AES256Util.encrypt((weatherParsingData.get("P_7") == null || weatherParsingData.get("P_1") == null
              || CommonConstant.NULL_DATA.equals(weatherParsingData.get("P_1"))) ? CommonConstant.NULL_DATA : weatherParsingData.get("P_7")+""));

      int ciIndex = !collectionData.containsKey(ci) ? 0 :
              KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(collectionData.get(ci).toString()));

      resultData.put("deviceIdx", AES256Util.encrypt(deviceIdx));
      resultData.put("deviceSerial", AES256Util.encrypt(serialNum));
      resultData.put("deviceType", AES256Util.encrypt(deviceType));
      resultData.put("stationName", AES256Util.encrypt(stationName));
      resultData.put("total", AES256Util.encrypt(!collectionData.containsKey(ci) ? "NA" : Math.round(Double.parseDouble(collectionData.get(ci).toString()))+""));
      resultData.put("totalIndex", AES256Util.encrypt(KweatherElementMessageManageUtil.setElementLevelKorName("ci", String.valueOf(ciIndex))));
      resultData.put("totalGrade", AES256Util.encrypt((ciIndex == 0) ? "NA" : ciIndex+""));
      resultData.put("lastUpdated", AES256Util.encrypt(!collectionData.containsKey("cusTm") ? "NA" : collectionData.get("cusTm").toString()));
      resultData.put("receiveError", AES256Util.encrypt(!collectionData.containsKey("timestamp") ? "NA" :
              String.valueOf(((Long.parseLong(collectionData.get("timestamp").toString())) + (5 * 60)) > (System.currentTimeMillis()))));

      resultData.put("action", AES256Util.encrypt((ciIndex == 0) ? "NA" : KweatherElementMessageManageUtil.CI_ACTION[ciIndex - 1]
              .concat(" 다만, 더욱 쾌적한 환경을 위해서 아래의 행동 요령을 참고해 주세요. ").concat(badTargetElements)));

      resultData.put("vents", ventDatas);
      resultData.put("elements", elementDatas);
      resultData.put("elementsAddition", elementDatas2);
      resultData.put("weather", weatherData);

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    return resultData;
  }

  public JSONObject getSerialToStatPlatFormData(String paramStr, String standard) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    JSONObject result = new JSONObject();
    JSONObject responseObj;
    JSONArray dataArray;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url = URI.create(new StringBuilder(CommonConstant.API_SERVER_HOST_AIR365_STAT_API).append(paramStr).toString());
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    if (res.getStatusCode() == HttpStatus.OK) {
      responseObj = new JSONObject(res.getBody());
      dataArray = responseObj.getJSONArray("data");
      for (int i = 0; i < dataArray.length(); i++) {
        JSONObject dataObj = dataArray.getJSONObject(i);
        result.put("hour".equals(standard) ? dataObj.getString("tm").concat("00") : dataObj.getString("tm").concat("0000"), dataObj);
      }

    } else {
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return result;
  }

  public Map<String, String> getSearchDate(String standard, String searchDate, String formatType) throws ParseException {
    Map<String, String> dateMp = new HashMap<>();
    SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdfFormat = new SimpleDateFormat("A".equals(formatType) ? "yyyy-MM-dd" : "yyyy/MM/dd");

    String fromStr;
    String toStr;

    if ("day".equals(standard)) {
      Calendar cal = Calendar.getInstance();
      Date searchFromDate = new Date();
      searchFromDate = sdfParse.parse(searchDate.substring(0, searchDate.length() - 2).concat("01"));
      cal.setTime(searchFromDate);
      cal.add(Calendar.MONTH, 1);
      cal.add(Calendar.MINUTE, -1);

      fromStr = sdfFormat.format(searchFromDate);
      toStr = sdfFormat.format(cal.getTime());

    } else {
      fromStr = sdfFormat.format(sdfParse.parse(searchDate));
      toStr = fromStr;
    }

    dateMp.put("from", fromStr);
    dateMp.put("to", toStr);

    return dateMp;
  }

  public List<String> createDateTimeList(String fromStr, String toStr, String standard, String formatType) throws Exception {
    List<String> tmList = new ArrayList<>();
    SimpleDateFormat sdfParse = new SimpleDateFormat("A".equals(formatType) ? "yyyy-MM-ddHHmm" : "yyyyMMddHHmm");
    SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyyMMddHHmm");
    Date currentDate = new Date();
    Date fromDate = sdfParse.parse(fromStr.concat("0000"));
    Date toDate = sdfParse.parse(toStr.concat("2359"));

    Calendar cal = Calendar.getInstance();
    cal.setTime(fromDate);
    while ((cal.getTime().before(toDate) && cal.getTime().before(currentDate)) || cal.getTime().equals(toDate)) {
      tmList.add("A".equals(formatType) ? sdfFormat.format(cal.getTime()) : String.valueOf(cal.getTimeInMillis() / 1000L));
      switch (standard) {
        case "hour":
          cal.add(Calendar.HOUR, 1);
          break;
        case "1min":
          cal.add(Calendar.MINUTE, 1);
          break;
        case "5min":
          cal.add(Calendar.MINUTE, 5);
          break;
        default:
          cal.add(Calendar.DATE, 1);
          break;
      }
    }

    return tmList;
  }

  public LinkedHashMap<String, Object> getVentStatusData(String ventSerial, String regionCode) throws Exception {
    LinkedHashMap<String, Object> resDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> ventDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> stationDataObj = new LinkedHashMap<String, Object>();
    SensorDataDto ventData = new SensorDataDto();
    SensorDataDto iaqData = new SensorDataDto();

    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    String today = (new SimpleDateFormat("yyyyMMddHHmm").format(date));
    try {

      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);

      ventData = appVentService.selectVentCollectionApi(ventSerial);
      iaqData = appVentService.selectIaqCollectionApi(iaqSerial);

      String aiMode = readOnlyMapper.getVentAiMode(ventSerial);

      Long currentDateTime = Long.parseLong(today);
      Long collectionDateTime = Long.parseLong(
              (ventData == null || ventData.getReg_date() == null) ? today : ventData.getReg_date());

      if ((currentDateTime - collectionDateTime) <= 5L)
        ventDataObj.put("wifi", 1);
      else
        ventDataObj.put("wifi", 0);

      assert ventData != null;
      DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
      boolean iaqReceiveError = false, ventReceiveError = false;
      long currentTs = System.currentTimeMillis();
      long ventTs;
      long iaqTs;

      if (ventData.getReg_date() != null) {
        LocalDateTime localVentDateTime = LocalDateTime.from(formatDateTime.parse(ventData.getReg_date()));
        ventTs = Timestamp.valueOf(localVentDateTime).getTime() + (5 * 60000);

        ventReceiveError = (ventTs >= currentTs) ? false : true;

        logger.error("curr TS :: {}, vent TS :: {}", currentTs, ventTs);
        logger.error("systemcurr :: {}, ventTs :: {}, ventReceiveError :: {}", new SimpleDateFormat("yyyyMMddHHmm").format(new Date(currentTs)),
                new SimpleDateFormat("yyyyMMddHHmm").format(new Date(ventTs)), ventReceiveError);
      }

      ventDataObj.put("receiveError", ventReceiveError);
      ventDataObj.put("dateTime", ventData.getReg_date() == null ? CommonConstant.NULL_DATA : ventData.getReg_date());
      ventDataObj.put("filterAlarm", ventData.getFilter_alarm() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getFilter_alarm()));
      ventDataObj.put("powerMode", ventData.getPower() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getPower()));
      ventDataObj.put("windMode", ventData.getAir_volume() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getAir_volume()));
      ventDataObj.put("exhMode", ventData.getExh_mode() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getExh_mode()));
      ventDataObj.put("autoMode", ventData.getAuto_mode() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getAuto_mode()));
      ventDataObj.put("airMode", aiMode == null ? CommonConstant.NULL_DATA : Integer.valueOf(aiMode));

      int ciIndex = iaqData.getCici() == null ? 0 :
              KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(iaqData.getCici().toString()));

      stationDataObj.put("total", iaqData.getCici() == null ? CommonConstant.NULL_DATA : Integer.valueOf(iaqData.getCici()));
      stationDataObj.put("totalIndex", KweatherElementMessageManageUtil.setElementLevelKorName("ci", String.valueOf(ciIndex)));
      stationDataObj.put("totalGrade", (ciIndex == 0) ? CommonConstant.NULL_DATA : ciIndex);

      if (iaqData.getTm() != null) {
        LocalDateTime localIaqDateTime = LocalDateTime.from(formatDateTime.parse(iaqData.getTm()));
        iaqTs = Timestamp.valueOf(localIaqDateTime).getTime() + (5 * 60000);
        iaqReceiveError = (iaqTs >= currentTs) ? false : true;
      }

      stationDataObj.put("receiveError", iaqReceiveError);
      stationDataObj.put("inDateTime", iaqData.getTm() == null ? CommonConstant.NULL_DATA : iaqData.getTm());

      List<Map<String, Object>> elementList = new ArrayList<>();

      List<String[]> elDataList = new ArrayList<>();
      String[] pm10Datas = {"pm10", "ug/m³", iaqData.getPm10() == null ? CommonConstant.NULL_DATA : iaqData.getPm10(),
              iaqData.getCici_pm10() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm10()};
      String[] pm25Datas = {"pm25", "ug/m³", iaqData.getPm25() == null ? CommonConstant.NULL_DATA : iaqData.getPm25(),
              iaqData.getCici_pm25() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm25()};
      String[] co2Datas = {"co2", "ppm", iaqData.getCo2() == null ? CommonConstant.NULL_DATA : iaqData.getCo2(),
              iaqData.getCici_co2() == null ? CommonConstant.NULL_DATA : iaqData.getCici_co2()};
      String[] vocDatas = {"voc", "ppb", iaqData.getVoc() == null ? CommonConstant.NULL_DATA : iaqData.getVoc(),
              iaqData.getCici_voc() == null ? CommonConstant.NULL_DATA : iaqData.getCici_voc()};

      elDataList.add(pm10Datas);
      elDataList.add(pm25Datas);
      elDataList.add(co2Datas);
      elDataList.add(vocDatas);

      for (String[] elData : elDataList) {
        Map<String, Object> elMp = new LinkedHashMap<>();
        elMp.put("engName", elData[0]);
        elMp.put("score", CommonConstant.NULL_DATA.equals(elData[3]) ? CommonConstant.NULL_DATA : Math.round(Double.valueOf(elData[3])));
        elMp.put("value", CommonConstant.NULL_DATA.equals(elData[2]) ? CommonConstant.NULL_DATA : Integer.valueOf(elData[2]));
        elMp.put("unit", elData[1]);
        elMp.put("index", CommonConstant.NULL_DATA.equals(elData[3]) ? 0
                : KweatherElementMessageManageUtil.setElementLevelKorName(elData[0],
                String.valueOf(KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(elData[3])))));
        elMp.put("grade4", CommonConstant.NULL_DATA.equals(elData[3]) ? 0
                : KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(elData[3])));
        elementList.add(elMp);
      }

      Map<String, Object> weatherData = new LinkedHashMap<>();

      try {

        if (!"".equals(regionCode))
          weatherData = WeatherApiUtil.weatherAirCastApi(regionCode, new String[] {
                  "pm10Grade_who", "pm25Grade_who", "pm10Value", "pm25Value"});

      } catch (Exception e) {
        logger.error("Weather F.CAST API ERROR .");
      }

      ventDataObj.put("fireAlarm", ventData.getFire_alarm() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getFire_alarm()));
      ventDataObj.put("waterAlarm", ventData.getWater_alarm() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getWater_alarm()));
      ventDataObj.put("devStat", ventData.getDev_stat() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getDev_stat()));

      stationDataObj.put("outPm10Grade", weatherData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherData.get("P_1"));
      stationDataObj.put("outPm25Grade", weatherData.get("P_2") == null ? CommonConstant.NULL_DATA : weatherData.get("P_2"));
      stationDataObj.put("outPm10Value", weatherData.get("P_3") == null ? CommonConstant.NULL_DATA : weatherData.get("P_3"));
      stationDataObj.put("outPm25Value", weatherData.get("P_4") == null ? CommonConstant.NULL_DATA : weatherData.get("P_4"));

      stationDataObj.put("autoCause", 4L);
      stationDataObj.put("elements", elementList);

      resDataObj.put("ventData", ventDataObj);
      resDataObj.put("stationData", stationDataObj);

    } catch(HttpClientErrorException e){
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.PLATFORM_API_CALL_EXCEPTION);
    }catch(NumberFormatException e){
      e.printStackTrace();
      throw new NumberFormatException();
    }catch(SQLException e){
      e.printStackTrace();
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }catch (Exception e) {
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }
    return resDataObj;
  }

  public LinkedHashMap<String, Object> getVentStatusDataEncodeVersion(String ventSerial, String regionCode) throws Exception {
    LinkedHashMap<String, Object> resDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> ventDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> stationDataObj = new LinkedHashMap<String, Object>();
    SensorDataDto ventData, iaqData;

    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    String today = (new SimpleDateFormat("yyyyMMddHHmm").format(date));

    try {

      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);

      ventData = appVentService.selectVentCollectionApi(ventSerial);
      iaqData = appVentService.selectIaqCollectionApi(iaqSerial);

      String aiMode = readOnlyMapper.getVentAiMode(ventSerial);

      Long currentDateTime = Long.parseLong(today);
      Long collectionDateTime = Long.parseLong(
              (ventData == null || ventData.getReg_date() == null) ? today : ventData.getReg_date());

      if ((currentDateTime - collectionDateTime) <= 5L)
        ventDataObj.put("wifi", AES256Util.encrypt("1"));
      else
        ventDataObj.put("wifi", AES256Util.encrypt("0"));

      assert ventData != null;
      DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
      boolean iaqReceiveError = false, ventReceiveError = false;
      long currentTs = System.currentTimeMillis();
      long ventTs;
      long iaqTs;

      if (ventData.getReg_date() != null) {
        LocalDateTime localVentDateTime = LocalDateTime.from(formatDateTime.parse(ventData.getReg_date()));
        ventTs = Timestamp.valueOf(localVentDateTime).getTime() + (5 * 60000);

        ventReceiveError = (ventTs >= currentTs) ? false : true;

        logger.error("curr TS :: {}, vent TS :: {}", currentTs, ventTs);
        logger.error("systemcurr :: {}, ventTs :: {}, ventReceiveError :: {}", new SimpleDateFormat("yyyyMMddHHmm").format(new Date(currentTs)),
                new SimpleDateFormat("yyyyMMddHHmm").format(new Date(ventTs)), ventReceiveError);
      }

      ventDataObj.put("receiveError", AES256Util.encrypt(String.valueOf(ventReceiveError)));
      ventDataObj.put("dateTime", AES256Util.encrypt(ventData.getReg_date() == null ? CommonConstant.NULL_DATA : ventData.getReg_date()));
      ventDataObj.put("filterAlarm", AES256Util.encrypt(ventData.getFilter_alarm() == null ? CommonConstant.NULL_DATA : ventData.getFilter_alarm()));
      ventDataObj.put("powerMode", AES256Util.encrypt(ventData.getPower() == null ? CommonConstant.NULL_DATA : ventData.getPower()));
      ventDataObj.put("windMode", AES256Util.encrypt(ventData.getAir_volume() == null ? CommonConstant.NULL_DATA : ventData.getAir_volume()));
      ventDataObj.put("exhMode", AES256Util.encrypt(ventData.getExh_mode() == null ? CommonConstant.NULL_DATA : ventData.getExh_mode()));
      ventDataObj.put("autoMode", AES256Util.encrypt(ventData.getAuto_mode() == null ? CommonConstant.NULL_DATA : ventData.getAuto_mode()));
      ventDataObj.put("airMode", AES256Util.encrypt(aiMode == null ? CommonConstant.NULL_DATA : aiMode));
      ventDataObj.put("filter_alarm", AES256Util.encrypt(ventData.getFilter_alarm() == null ? CommonConstant.NULL_DATA : ventData.getFilter_alarm()));

      int ciIndex = iaqData.getCici() == null ? 0 :
              KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(iaqData.getCici().toString()));

      stationDataObj.put("total", AES256Util.encrypt(iaqData.getCici() == null ? CommonConstant.NULL_DATA : iaqData.getCici()));
      stationDataObj.put("totalIndex", AES256Util.encrypt(KweatherElementMessageManageUtil.setElementLevelKorName("ci", String.valueOf(ciIndex))));
      stationDataObj.put("totalGrade", AES256Util.encrypt((ciIndex == 0) ? CommonConstant.NULL_DATA : ciIndex+""));

      if (iaqData.getTm() != null) {
        LocalDateTime localIaqDateTime = LocalDateTime.from(formatDateTime.parse(iaqData.getTm()));
        iaqTs = Timestamp.valueOf(localIaqDateTime).getTime() + (5 * 60000);
        iaqReceiveError = (iaqTs >= currentTs) ? false : true;
      }

      stationDataObj.put("receiveError", AES256Util.encrypt(String.valueOf(iaqReceiveError)));
      stationDataObj.put("inDateTime", AES256Util.encrypt(iaqData.getTm() == null ? CommonConstant.NULL_DATA : iaqData.getTm()));

      List<Map<String, Object>> elementList = new ArrayList<>();

      List<String[]> elDataList = new ArrayList<>();
      String[] pm10Datas = {"pm10", "ug/m³", iaqData.getPm10() == null ? CommonConstant.NULL_DATA : iaqData.getPm10(),
              iaqData.getCici_pm10() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm10()};
      String[] pm25Datas = {"pm25", "ug/m³", iaqData.getPm25() == null ? CommonConstant.NULL_DATA : iaqData.getPm25(),
              iaqData.getCici_pm25() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm25()};
      String[] co2Datas = {"co2", "ppm", iaqData.getCo2() == null ? CommonConstant.NULL_DATA : iaqData.getCo2(),
              iaqData.getCici_co2() == null ? CommonConstant.NULL_DATA : iaqData.getCici_co2()};
      String[] vocDatas = {"voc", "ppb", iaqData.getVoc() == null ? CommonConstant.NULL_DATA : iaqData.getVoc(),
              iaqData.getCici_voc() == null ? CommonConstant.NULL_DATA : iaqData.getCici_voc()};

      elDataList.add(pm10Datas);
      elDataList.add(pm25Datas);
      elDataList.add(co2Datas);
      elDataList.add(vocDatas);

      for (String[] elData : elDataList) {
        Map<String, Object> elMp = new LinkedHashMap<>();
        elMp.put("engName", AES256Util.encrypt(elData[0]));
        elMp.put("score", AES256Util.encrypt(CommonConstant.NULL_DATA.equals(elData[3]) ? CommonConstant.NULL_DATA : Math.round(Double.valueOf(elData[3]))+""));
        elMp.put("value", AES256Util.encrypt(CommonConstant.NULL_DATA.equals(elData[2]) ? CommonConstant.NULL_DATA : elData[2]));
        elMp.put("unit", AES256Util.encrypt(elData[1]));
        elMp.put("index", AES256Util.encrypt(CommonConstant.NULL_DATA.equals(elData[3]) ? "0"
                : KweatherElementMessageManageUtil.setElementLevelKorName(elData[0],
                String.valueOf(KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(elData[3]))))));
        elMp.put("grade4", AES256Util.encrypt(CommonConstant.NULL_DATA.equals(elData[3]) ? "0"
                : KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(elData[3]))+""));
        elementList.add(elMp);
      }

      Map<String, Object> weatherData = new LinkedHashMap<>();

      try {

        if (!"".equals(regionCode))
          weatherData = WeatherApiUtil.weatherAirCastApi(regionCode, new String[] {
                  "pm10Grade_who", "pm25Grade_who", "pm10Value", "pm25Value"});

      } catch (Exception e) {
        logger.error("Weather F.CAST API ERROR .");
      }

      ventDataObj.put("fireAlarm", AES256Util.encrypt(ventData.getFire_alarm() == null ? CommonConstant.NULL_DATA : ventData.getFire_alarm()));
      ventDataObj.put("waterAlarm", AES256Util.encrypt(ventData.getWater_alarm() == null ? CommonConstant.NULL_DATA : ventData.getWater_alarm()));
      ventDataObj.put("devStat", AES256Util.encrypt(ventData.getDev_stat() == null ? CommonConstant.NULL_DATA : ventData.getDev_stat()));

      stationDataObj.put("outPm10Grade", AES256Util.encrypt(weatherData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherData.get("P_1")+""));
      stationDataObj.put("outPm25Grade", AES256Util.encrypt(weatherData.get("P_2") == null ? CommonConstant.NULL_DATA : weatherData.get("P_2")+""));
      stationDataObj.put("outPm10Value", AES256Util.encrypt(weatherData.get("P_3") == null ? CommonConstant.NULL_DATA : weatherData.get("P_3")+""));
      stationDataObj.put("outPm25Value", AES256Util.encrypt(weatherData.get("P_4") == null ? CommonConstant.NULL_DATA : weatherData.get("P_4")+""));

      stationDataObj.put("autoCause", AES256Util.encrypt("4"));
      stationDataObj.put("elements", elementList);

      resDataObj.put("ventData", ventDataObj);
      resDataObj.put("stationData", stationDataObj);


    } catch(HttpClientErrorException e){
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.PLATFORM_API_CALL_EXCEPTION);
    }catch(NumberFormatException e){
      e.printStackTrace();
      throw new NumberFormatException();
    }catch(SQLException e){
      e.printStackTrace();
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }catch (Exception e) {
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }
    return resDataObj;
  }

  public LinkedHashMap<String, Object> getStatIotDataWeb(String serial, String searchDate, String standard) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    int resultCode = 0;
    Map<String, Object> dataMp = new LinkedHashMap<>();
    Map<String, Object> avgMp = new LinkedHashMap<>();
    Map<String, Object> maxMp = new LinkedHashMap<>();
    Map<String, Object> minMp = new LinkedHashMap<>();
    List<String> tmList = new ArrayList<String>();
    List<DeviceElements> elMetaDataList = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();
    Map<String, Object> elDataMp = new LinkedHashMap<>();

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    try {

      String deviceType = String.valueOf(deviceInfo.get("device_type"));
      String serialNum = String.valueOf(deviceInfo.get("serial_num"));

      Map<String, String> searchDateMp = getSearchDate(standard, searchDate, "A");
      String paramStr = "?serialNum=".concat(serialNum)
              .concat("&deviceType=").concat(deviceType)
              .concat("&searchType=").concat(standard)
              .concat("&searchFromDate=").concat(searchDateMp.get("from"))
              .concat("&searchToDate=").concat(searchDateMp.get("to"));

      JSONObject platResponse = getSerialToStatPlatFormData(paramStr, standard);

      tmList = createDateTimeList(searchDateMp.get("from"), searchDateMp.get("to"), standard, "A");

      elMetaDataList = dataCenterMapper.selectDeviceModelElements(serial);
      for (DeviceElements el : elMetaDataList) {
        elDataMp = new LinkedHashMap<>();
        String engName = el.getEngName();

        List<Object> avgElData = new ArrayList<>(), maxElData = new ArrayList<>(), minElData = new ArrayList<>();
        List<Object> avgElDouData = new ArrayList<>(), maxElDouData = new ArrayList<>(), minElDouData = new ArrayList<>();

        for (String tm : tmList) {
          double avgElVal, maxElVal, minElVal;
          if (platResponse.has(tm)) {
            JSONObject tmData = platResponse.getJSONObject(tm);

            JSONObject avgTempObj = tmData.getJSONObject("avg");
            JSONObject maxTempObj = tmData.getJSONObject("max");
            JSONObject minTempObj = tmData.getJSONObject("min");

            avgElVal = avgTempObj.has(engName) ? avgTempObj.getDouble(engName) : -999;
            maxElVal = maxTempObj.has(engName) ? maxTempObj.getDouble(engName) : -999;
            minElVal = minTempObj.has(engName) ? minTempObj.getDouble(engName) : -999;

          } else {
            avgElVal = -999;
            maxElVal = -999;
            minElVal = -999;
          }

          if ("temp".equals(engName)) {
            avgElDouData.add(avgElVal);
            maxElDouData.add(maxElVal);
            minElDouData.add(minElVal);

          } else {
            avgElData.add((int) Math.round(avgElVal));
            maxElData.add((int) Math.round(maxElVal));
            minElData.add((int) Math.round(minElVal));
          }
        }

        avgMp.put(engName, "temp".equals(engName) ? avgElDouData : avgElData);
        maxMp.put(engName, "temp".equals(engName) ? maxElDouData : maxElData);
        minMp.put(engName, "temp".equals(engName) ? minElDouData : minElData);

        elDataMp.put("korName", el.getKorName());
        elDataMp.put("engName", engName);
        elDataMp.put("viewName", el.getViewName());
        elDataMp.put("unit", el.getElementUnit());

        dataList.add(elDataMp);
      }

      dataMp.put("avg", avgMp);
      dataMp.put("max", maxMp);
      dataMp.put("min", minMp);

      resultCode = 1;

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    resultData.put("result", resultCode);
    resultData.put("date", searchDate);
    resultData.put("count", tmList.size());
    resultData.put("data", dataMp);
    resultData.put("elementsCount", elMetaDataList.size());
    resultData.put("elements", dataList);

    return resultData;
  }

  // App 이용
  public LinkedHashMap<String, Object> getStatIotDataApp(String serial, String searchDate, String standard) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    int resultCode = 0;
    Map<String, Object> dataMp = new LinkedHashMap<>();
    List<String> tmList = new ArrayList<String>();
    List<DeviceElements> elMetaDataList = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();
    Map<String, Object> elDataMp = new LinkedHashMap<>();

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    try {

      String deviceType = String.valueOf(deviceInfo.get("device_type"));
      String serialNum = String.valueOf(deviceInfo.get("serial_num"));

      Map<String, String> searchDateMp = getSearchDate(standard, searchDate, "A");
      String paramStr = "?serialNum=".concat(serialNum)
              .concat("&deviceType=").concat(deviceType)
              .concat("&searchType=").concat(standard)
              .concat("&searchFromDate=").concat(searchDateMp.get("from"))
              .concat("&searchToDate=").concat(searchDateMp.get("to"));

      JSONObject platResponse = getSerialToStatPlatFormData(paramStr, standard);

      tmList = createDateTimeList(searchDateMp.get("from"), searchDateMp.get("to"), standard, "A");

      elMetaDataList = dataCenterMapper.selectDeviceModelElements(serial);
      for (DeviceElements el : elMetaDataList) {
        elDataMp = new LinkedHashMap<>();
        dataMp = new LinkedHashMap<>();
        String engName = el.getEngName();

        List<Object> avgElData = new ArrayList<>(), maxElData = new ArrayList<>(), minElData = new ArrayList<>();
        List<Object> avgElDouData = new ArrayList<>(), maxElDouData = new ArrayList<>(), minElDouData = new ArrayList<>();

       // avgElData = new ArrayList<>();
       // maxElData = new ArrayList<>();
       // minElData = new ArrayList<>();

        for (String tm : tmList) {
          double avgElVal, maxElVal, minElVal;
          if (platResponse.has(tm)) {
            JSONObject tmData = platResponse.getJSONObject(tm);

            JSONObject avgTempObj = tmData.getJSONObject("avg");
            JSONObject maxTempObj = tmData.getJSONObject("max");
            JSONObject minTempObj = tmData.getJSONObject("min");

            avgElVal = avgTempObj.has(engName) ? avgTempObj.getDouble(engName) : -999;
            maxElVal = maxTempObj.has(engName) ? maxTempObj.getDouble(engName) : -999;
            minElVal = minTempObj.has(engName) ? minTempObj.getDouble(engName) : -999;

          } else {
            avgElVal = -999;
            maxElVal = -999;
            minElVal = -999;
          }

          if ("temp".equals(engName)) {
            avgElDouData.add(avgElVal);
            maxElDouData.add(maxElVal);
            minElDouData.add(minElVal);

          } else {
            avgElData.add((int) Math.round(avgElVal));
            maxElData.add((int) Math.round(maxElVal));
            minElData.add((int) Math.round(minElVal));
          }
        }

        dataMp.put("avg", "temp".equals(engName) ? avgElDouData : avgElData);
        dataMp.put("max", "temp".equals(engName) ? maxElDouData : maxElData);
        dataMp.put("min", "temp".equals(engName) ? minElDouData : minElData);

        elDataMp.put("korName", el.getKorName());
        elDataMp.put("engName", engName);
        elDataMp.put("viewName", el.getViewName());
        elDataMp.put("unit", el.getElementUnit());

        elDataMp.put("value", dataMp);

        dataList.add(elDataMp);
      }

      resultCode = 1;

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    resultData.put("result", resultCode);
    resultData.put("date", searchDate);
    resultData.put("count", tmList.size());
    resultData.put("elementsCount", elMetaDataList.size());
    resultData.put("elements", dataList);

    return resultData;
  }

  public JSONObject getSerialToReportPlatFormData(String paramStr, String standard) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    JSONObject result = new JSONObject();
    JSONArray responseDatas;

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url = URI.create(
            new StringBuilder(CommonConstant.API_SERVER_HOST_DEVICE)
                    .append(CommonConstant.SEARCH_PATH_QUERY)
                    .append(paramStr).toString());

    logger.error("getSerialToReportPlatFormData URL :: {}", url);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    if (res.getStatusCode() == HttpStatus.OK) {
      responseDatas = new JSONArray(res.getBody());
      for (int i = 0; i < responseDatas.length(); i++) {
        JSONObject dataObj = responseDatas.getJSONObject(i);
        JSONObject dpsObj = dataObj.getJSONObject("dps");
        String tagName = dataObj.getJSONObject("tags").getString("sensor");
        result.put(tagName, dpsObj);
      }

    } else {
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return result;
  }


  public LinkedHashMap<String, Object> getReportDataWeb(String serial, String searchDate, String standard) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    List<String> tmList = new ArrayList<String>();
    int resultCode = 0;
    Map<String, Object> dataMp = new LinkedHashMap<>();
    String standardParam = "";

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    try {

      switch (standard) {
        case "1min":
          standardParam = "sum";
          break;
        case "5min":
          standardParam = "5m-avg-none";
          break;
        case "hour":
          standardParam = "1h-avg-none";
          break;
        default:
          standardParam = "sum";
          break;
      }

      String deviceType = String.valueOf(deviceInfo.get("device_type"));

      Map<String, String> searchDateMp = getSearchDate(standard, searchDate, "B");

      String paramStr = "?start=".concat(URLEncoder.encode(searchDateMp.get("from").concat("-00:00:00"), "UTF-8"))
              .concat("&end=").concat(URLEncoder.encode(searchDateMp.get("to").concat("-23:59:00"), "UTF-8"))
              .concat("&m=").concat(URLEncoder.encode("avg:".concat(standardParam.concat(":kw-")
                      .concat((deviceType.equals("dot")) ? "oaq" : deviceType.toLowerCase()))
                      .concat("-sensor-").concat((deviceType.equals("dot")) ? "dot" : "kiot")
                      .concat(".").concat(serial).concat("{sensor=*}"), "UTF-8"));

      JSONObject platData = getSerialToReportPlatFormData(paramStr, standard);
      tmList = createDateTimeList(searchDate, searchDate, standard, "B");

      elLoop: for (DeviceElements el : dataCenterMapper.selectDeviceModelElements(serial)) {
        String engName = el.getEngName();

        List<Object> elDataTempList = new ArrayList<>(), elDataList = new ArrayList<>();
        for (String tm : tmList) {
          String timestamp = String.valueOf(tm);
          double elData;

          if (platData.has(engName)) {
            JSONObject platElData = platData.getJSONObject(engName);
            elData = platElData.has(timestamp) ? platElData.getDouble(timestamp) : -999;

          } else {
            elData = -999;
          }

          if ("temp".equals(engName)) {
            elDataTempList.add(Math.round(elData * 10) / 10.0);

          } else {
            elDataList.add(Math.round(elData));
          }

          dataMp.put(engName, "temp".equals(engName) ? elDataTempList : elDataList);
        }
      }

      resultCode = 1;

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    resultData.put("result", resultCode);
    resultData.put("date", searchDate);
    resultData.put("count", tmList.size());
    resultData.put("data", dataMp);

    return resultData;
  }

  public LinkedHashMap<String, Object> getReportDataWebWithDate(String serial, String fromDate, String toDate, String standard) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    List<String> tmList = new ArrayList<String>();
    int resultCode = 0;
    Map<String, Object> dataMp = new LinkedHashMap<>();
    String standardParam = "";

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    try {

      switch (standard) {
        case "1min":
          standardParam = "sum";
          break;
        case "5min":
          standardParam = "5m-avg-none";
          break;
        case "hour":
          standardParam = "1h-avg-none";
          break;
        default:
          standardParam = "sum";
          break;
      }

      String deviceType = String.valueOf(deviceInfo.get("device_type"));

//      Map<String, String> searchDateMp = getSearchDate(standard, searchDate, "B");
      String fD = fromDate.substring(0,4)+"/"+fromDate.substring(4,6)+"/"+fromDate.substring(6);
      String tD = toDate.substring(0,4)+"/"+toDate.substring(4,6)+"/"+toDate.substring(6);
      String paramStr = "?start=".concat(URLEncoder.encode(fD.concat("-00:00:00"), "UTF-8"))
              .concat("&end=").concat(URLEncoder.encode(tD.concat("-23:59:00"), "UTF-8"))
              .concat("&m=").concat(URLEncoder.encode("avg:".concat(standardParam.concat(":kw-")
                      .concat((deviceType.equals("dot")) ? "oaq" : deviceType.toLowerCase()))
                      .concat("-sensor-").concat((deviceType.equals("dot")) ? "dot" : "kiot")
                      .concat(".").concat(serial).concat("{sensor=*}"), "UTF-8"));

      JSONObject platData = getSerialToReportPlatFormData(paramStr, standard);
      tmList = createDateTimeList(fromDate, toDate, standard, "B");

      elLoop: for (DeviceElements el : dataCenterMapper.selectDeviceModelElements(serial)) {
        String engName = el.getEngName();

        List<Object> elDataTempList = new ArrayList<>(), elDataList = new ArrayList<>();
        for (String tm : tmList) {
          String timestamp = String.valueOf(tm);
          double elData;

          if (platData.has(engName)) {
            JSONObject platElData = platData.getJSONObject(engName);
            elData = platElData.has(timestamp) ? platElData.getDouble(timestamp) : -999;

          } else {
            elData = -999;
          }

          if ("temp".equals(engName)) {
            elDataTempList.add(Math.round(elData * 10) / 10.0);

          } else {
            elDataList.add(Math.round(elData));
          }

          dataMp.put(engName, "temp".equals(engName) ? elDataTempList : elDataList);
        }
      }

      resultCode = 1;

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    resultData.put("result", resultCode);
    resultData.put("date", fromDate+"-"+toDate);
    resultData.put("count", tmList.size());
    resultData.put("data", dataMp);

    return resultData;
  }

  // App 이용
  public LinkedHashMap<String, Object> getReportDataApp(String serial, String searchDate, String standard) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    List<String> tmList = new ArrayList<String>();
    int resultCode = 0;
    List<Map<String, Object>> dataList = new ArrayList<>();
    List<DeviceElements> elMetaDataList = new ArrayList<>();
    String standardParam = "";

    HashMap<String, String> deviceInfo = readOnlyMapper.selectMemberDeviceInfo(serial);

    try {

      switch (standard) {
        case "1min":
          standardParam = "sum";
          break;
        case "5min":
          standardParam = "5m-avg-none";
          break;
        case "hour":
          standardParam = "1h-avg-none";
          break;
        default:
          standardParam = "sum";
          break;
      }

      String deviceType = String.valueOf(deviceInfo.get("device_type"));

      Map<String, String> searchDateMp = getSearchDate(standard, searchDate, "B");

      String paramStr = "?start=".concat(URLEncoder.encode(searchDateMp.get("from").concat("-00:00:00"), "UTF-8"))
              .concat("&end=").concat(URLEncoder.encode(searchDateMp.get("to").concat("-23:59:00"), "UTF-8"))
              .concat("&m=").concat(URLEncoder.encode("avg:".concat(standardParam.concat(":kw-")
                      .concat((deviceType.equals("dot")) ? "oaq" : deviceType.toLowerCase()))
                      .concat("-sensor-").concat((deviceType.equals("dot")) ? "dot" : "kiot")
                      .concat(".").concat(serial).concat("{sensor=*}"), "UTF-8"));

      JSONObject platData = getSerialToReportPlatFormData(paramStr, standard);
      tmList = createDateTimeList(searchDate, searchDate, standard, "B");

      elMetaDataList = dataCenterMapper.selectDeviceModelElements(serial);

      Map<String, Object> dataMp = new LinkedHashMap<>();
      elLoop: for (DeviceElements el : elMetaDataList) {
        String engName = el.getEngName();
        dataMp = new LinkedHashMap<String, Object>();

        if (platData.has(engName)) {
          List<Object> elDataTempList = new ArrayList<>();
          List<Object> elRawDataList = new ArrayList<>();
          JSONObject platElData = platData.getJSONObject(engName);

          for (String tm : tmList) {
            String timestamp = String.valueOf(tm);
            double elData;

            elData = platElData.has(timestamp) ? platElData.getDouble(timestamp) : -999;

            if ("temp".equals(engName)) {
              elDataTempList.add(Math.round(elData * 10) / 10.0);

            } else {
              elRawDataList.add(Math.round(elData));
            }
          }

          dataMp.put("korName", el.getKorName());
          dataMp.put("engName", el.getEngName());
          dataMp.put("viewName", el.getViewName());
          dataMp.put("unit", el.getElementUnit());

          dataMp.put("value", "temp".equals(engName) ? elDataTempList : elRawDataList);

        } else {
          List<Object> elRawDataList = new ArrayList<>();
          for (String tm : tmList) {
            int elData = -999;

              elRawDataList.add(elData);
          }

          dataMp.put("korName", el.getKorName());
          dataMp.put("engName", el.getEngName());
          dataMp.put("viewName", el.getViewName());
          dataMp.put("unit", el.getElementUnit());

          dataMp.put("value",  elRawDataList);
          //continue elLoop;
        }

        dataList.add(dataMp);
      }

      resultCode = 1;

    } catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    resultData.put("result", resultCode);
    resultData.put("date", searchDate);
    resultData.put("count", tmList.size());
    resultData.put("elementsCount", elMetaDataList.size());
    resultData.put("elements", dataList);

    return resultData;
  }
}
