package com.airguard.service.app.v3;

import com.airguard.exception.ExternalApiException;
import com.airguard.exception.ParameterException;
import com.airguard.exception.SQLException;
import com.airguard.mapper.main.app.StationMapper;
import com.airguard.mapper.main.app.UserMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.mapper.readonly.DatacenterMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.platform.SensorDataDto;
import com.airguard.model.system.Device;
import com.airguard.model.system.DeviceElements;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Vent;
import com.airguard.service.app.VentService;
import com.airguard.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class Air365StationV3Service {

  private static final Logger logger = LoggerFactory.getLogger(Air365StationV3Service.class);

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



  public LinkedHashMap<String, Object> getIotDataDetail(String serial) throws Exception {
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
    criteriaMp.put("temp", new Integer[]{21, 24, 27, 30, 34, 21, 18, 16, 14, 0}); // ??????, ??????
    criteriaMp.put("humi", new Integer[]{50, 60, 75, 90, 100, 50, 40, 35, 20, 0}); // ??????, ??????

    String[] elNormalArray = {"pm10", "pm25", "co2", "voc", "noise"};

    try {

      String deviceType = String.valueOf(deviceInfo.get("device_type"));
      String serialNum = String.valueOf(deviceInfo.get("serial_num"));
      String stationName = String.valueOf(deviceInfo.get("station_name"));
      String deviceIdx = String.valueOf(deviceInfo.get("device_idx"));
      String dcode = String.valueOf(deviceInfo.get("dcode"));
      String dfname = String.valueOf(deviceInfo.get("dfname"));
      String lat = String.valueOf(deviceInfo.get("lat"));
      String lon = String.valueOf(deviceInfo.get("lon"));
      String ci = "IAQ".equals(deviceType.toUpperCase())? "cici" : "coci";

      collectionData = getSerialToPlatFormData(deviceType, serialNum);

      ventDatas = new ArrayList<>();
      if ("IAQ".equals(deviceType.toUpperCase())) {
        for (Vent v : readOnlyMapper.selectMemberDeviceVentOne(deviceIdx)) {
          ventData = new LinkedHashMap<>();
          ventData.put("ventDeviceIdx", v.getVentDeviceIdx() == null ? CommonConstant.NULL_DATA : v.getVentDeviceIdx());
          ventData.put("ventSerial", v.getSerialNum() == null ? CommonConstant.NULL_DATA : v.getSerialNum());
          ventData.put("deviceModel", v.getDeviceModel() == null ? CommonConstant.NULL_DATA : v.getDeviceModel());
          ventData.put("filterAlarm", filterAlram(v).toString());
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

          // App ?????? ?????? ??????
          elAction = (elIndex == 0) ? "NA" : diffArray[elIndex - 1];
          if (elIndex != 0 && elIndex > 2) {
            if (("".equals(badTargetElements))) {
              badTargetElements += " ??????, ?????? ????????? ????????? ????????? ????????? ?????? ????????? ????????? ?????????. * ".concat(elAction);

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



      //??????????????? -> ???????????? ??????
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
      headers.add("auth", "a3dlYXRoZXItYXBwLWF1dGg=");
      HashMap<String, Object> valueMap = new HashMap<>();
      Map<String, Object> weatherParsingData = new LinkedHashMap<>();
      if (dcode != "null" && !dcode.equals("")  ) {
      logger.error("DCODE IS NOT NULL");
      URI kwapiUrl = URI.create("https://kwapi.kweather.co.kr/v1/gis/geo/hangaddr?hangCd="+dcode);
      RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, kwapiUrl);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);

      JSONObject jObj = new JSONObject(res.getBody());
      JSONObject data = jObj.getJSONObject("data");
      String region = data.getString("city_id");

      // App ?????? ?????? ?????? (?????? api ?????? ??? ?????? ??????)
      try {

        weatherParsingData = WeatherApiUtil.weatherTodayApi(region);

      } catch (Exception e) {
        e.printStackTrace();
        logger.error("================V3 data detail API Weather TODAY API ERROR ============, serial :: {}",serial);
      }

      try {
        String urlForDust = "http://kapi.kweather.co.kr/getXML_air_fcast_3times_area.php?mode=n&region=" + region;
        URL url = new URL(urlForDust);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "text/plain");


        if (connection.getResponseCode() == 200) {
          StringBuilder result = new StringBuilder();
          BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
          String line = "";
          while ((line = in.readLine()) != null) {
            result.append(line);
          }
          in.close();
          JSONParser parser = new JSONParser();
          org.json.simple.JSONObject outdoor = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) parser.parse(result.toString())).get("main");


          weatherData.put("pm10", outdoor.get("pm10Value").toString());
          weatherData.put("pm25", outdoor.get("pm25Value").toString());
          weatherData.put("date", outdoor.get("date").toString());
          valueMap.put("pm10", outdoor.get("pm10Value"));
          valueMap.put("pm25", outdoor.get("pm25Value"));
        }
      }catch(Exception e){
        e.printStackTrace();
        logger.error("================V3 data detail API Weather TODAY API ERROR ============, serial :: {}",serial);
      }

        valueMap.put("temp", weatherParsingData.get("P_4"));
        valueMap.put("humi", weatherParsingData.get("P_5"));

      }else{
        weatherData.put("pm10", CommonConstant.NULL_DATA);
        weatherData.put("pm25", CommonConstant.NULL_DATA);
        weatherData.put("date", CommonConstant.NULL_DATA);
        valueMap.put("pm10", -999);
        valueMap.put("pm25", -999);
        valueMap.put("temp",  -999);
        valueMap.put("humi",  -999);
      }
      String[] weatherElements = {"pm10", "pm25","temp","humi"};
      List<HashMap<String,Object>> elementInfoList = new ArrayList<>();
      for(String weatherElement : weatherElements){
        HashMap<String,Object> elementInfo = readOnlyMapper.selectElementInfo(weatherElement);
        elementInfo.put("value",valueMap.get(weatherElement));
        if(valueMap.isEmpty()){
          break;
        }
        int score = weatherElement.equals("temp") || weatherElement.equals("humi") ? -999 :  KweatherElemeniUtil.elementCiCalculator(Integer.valueOf(valueMap.get(weatherElement).toString()),weatherElement);
        elementInfo.put("score",score != -999 ? score : "NA");

        int grade = 0;
        if ("temp".equals(weatherElement) || "humi".equals(weatherElement)){
          grade = KweatherElementMessageManageUtil.elementLevel("", weatherElement, Double.parseDouble(
                  valueMap.get(weatherElement).toString()));
        } else{
          grade = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(score));
        }
        elementInfo.put("grade",grade);

        int grade4;
        switch (grade) {
          case 3:
            grade4 = 3;
            break;
          case 4:
            grade4 = 3;
            break;
          case 5:
            grade4 = 4;
            break;
          case 6:
            grade4 = 4;
            break;
          default:
            grade4 = grade;
            break;
        }
        if ("temp".equals(weatherElement) || "humi".equals(weatherElement)) {
          elementInfo.put("grade4", (grade4 == 0) ? "NA" : grade4);
        } else {
          elementInfo.put("grade4", (grade4 == 0) ? "NA" : grade);
        }

        elementInfo.put("index", (grade == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(weatherElement, String.valueOf(grade)));
        elementInfoList.add(elementInfo);
      }
      //weatherData.put("dongKo", weatherParsingData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherParsingData.get("P_1"));

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
      weatherData.put("elements",elementInfoList);

      int ciIndex = !collectionData.containsKey(ci) ? 0 : 
        KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(collectionData.get(ci).toString()));

      resultData.put("deviceIdx", Integer.valueOf(deviceIdx));
      resultData.put("deviceSerial", serialNum);
      resultData.put("deviceType", deviceType);
      resultData.put("stationName", stationName);
      resultData.put("country_nm","????????????");

      if(dfname != null && dfname.split(" ").length >= 2){
        resultData.put("sido_nm",dfname.split(" ")[0]);
        resultData.put("sg_nm",dfname.split(" ")[1]);
        resultData.put("emd_nm",dfname.split(" ")[2]);
        resultData.put("hang_cd",dcode);
      }else{
        resultData.put("sido_nm",CommonConstant.NULL_DATA);
        resultData.put("sg_nm",CommonConstant.NULL_DATA);
        resultData.put("emd_nm",CommonConstant.NULL_DATA);
        resultData.put("hang_cd",CommonConstant.NULL_DATA);
      }


      resultData.put("lat",lat);
      resultData.put("lon",lon);
      resultData.put("total", !collectionData.containsKey(ci) ? "NA" : Math.round(Double.parseDouble(collectionData.get(ci).toString())));
      resultData.put("totalIndex", KweatherElementMessageManageUtil.setElementLevelKorName("ci", String.valueOf(ciIndex)));
      resultData.put("totalGrade", (ciIndex == 0) ? "NA" : ciIndex);
      resultData.put("lastUpdated", !collectionData.containsKey("cusTm") ? "NA" : collectionData.get("cusTm").toString());
      Long rt = (Long.parseLong(collectionData.get("timestamp").toString())) + 5 * 60;
      resultData.put("receiveError", !collectionData.containsKey("timestamp") ? "NA" : 
        (Long.parseLong(rt.toString()+"000") < (System.currentTimeMillis())));

      resultData.put("action", (ciIndex == 0) ? "NA" : KweatherElementMessageManageUtil.CI_ACTION[ciIndex - 1]
          .concat(" ??????, ?????? ????????? ????????? ????????? ????????? ?????? ????????? ????????? ?????????. ").concat(badTargetElements));

      resultData.put("vents", ventDatas);
      resultData.put("elements", elementDatas);
      resultData.put("elementsAddition", elementDatas2);
      resultData.put("weather", weatherData);

    } catch(HttpClientErrorException e){
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.PLATFORM_API_CALL_EXCEPTION);
    }catch(NumberFormatException e){
      e.printStackTrace();
      throw new ParameterException(ParameterException.PARAMETER_EXCEPTION);
    }catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return resultData;
  }

  private String filterAlram(Vent v) {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(10 * 1000);
    factory.setReadTimeout(30 * 1000);
    RestTemplate restTemp = new RestTemplate(factory);
    HttpHeaders hd = new HttpHeaders();
    hd.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    hd.add("Content-Type", new StringBuilder(MediaType.APPLICATION_FORM_URLENCODED_VALUE).append(";charset=UTF-8").toString());
    JSONObject jo = new JSONObject();
    try {
      URI uri = URI.create(new StringBuilder(CommonConstant.API_SERVER_HOST_TOTAL).append(CommonConstant.SEARCH_PATH_SENSOR).append(CommonConstant.PARAM_SENSOR_VENT).append("/").append(v.getSerialNum()).toString());
      RequestEntity<String> req = null;
      ResponseEntity<String> res = null;
      req = new RequestEntity<>(hd, HttpMethod.GET, uri);
      res = restTemp.exchange(req, String.class);
      jo = new JSONObject(res.getBody()).getJSONObject("data");
      return jo.getString("filter_alarm");
    }catch (Exception e){
      e.printStackTrace();
    }
   return "";
  }

  public LinkedHashMap<String, Object> getIotDataDetailEncodeVersion(String serial) throws Exception {
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
    criteriaMp.put("temp", new String[]{AES256Util.encrypt("21"), AES256Util.encrypt("24"), AES256Util.encrypt("27"), AES256Util.encrypt("30"), AES256Util.encrypt("34"), AES256Util.encrypt("21"), AES256Util.encrypt("18"), AES256Util.encrypt("16"), AES256Util.encrypt("14"), AES256Util.encrypt("0")}); // ??????, ??????
    criteriaMp.put("humi", new String[]{AES256Util.encrypt("50"), AES256Util.encrypt("60"), AES256Util.encrypt("75"), AES256Util.encrypt("90"), AES256Util.encrypt("100"), AES256Util.encrypt("50"), AES256Util.encrypt("40"), AES256Util.encrypt("35"), AES256Util.encrypt("20"), AES256Util.encrypt("0")}); // ??????, ??????

    String[] elNormalArray = {"pm10", "pm25", "co2", "voc", "noise"};

    try {

      String deviceType = String.valueOf(deviceInfo.get("device_type"));
      String serialNum = String.valueOf(deviceInfo.get("serial_num"));
      String stationName = String.valueOf(deviceInfo.get("station_name"));
      String deviceIdx = String.valueOf(deviceInfo.get("device_idx"));
      String dcode = String.valueOf(deviceInfo.get("dcode"));
      String dfname = String.valueOf(deviceInfo.get("dfname"));
      String lat = String.valueOf(deviceInfo.get("lat"));
      String lon = String.valueOf(deviceInfo.get("lon"));
      String ci = "IAQ".equals(deviceType.toUpperCase())? "cici" : "coci";

      collectionData = getSerialToPlatFormData(deviceType, serialNum);
      HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
      factory.setConnectTimeout(10 * 1000);
      factory.setReadTimeout(30 * 1000);
      RestTemplate restTemp = new RestTemplate(factory);
      HttpHeaders hd = new HttpHeaders();
      hd.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      hd.add("Content-Type", new StringBuilder(MediaType.APPLICATION_FORM_URLENCODED_VALUE).append(";charset=UTF-8").toString());

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
            req = new RequestEntity<>(hd, HttpMethod.GET, uri);
            res = restTemp.exchange(req, String.class);
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

          // App ?????? ?????? ??????
          elAction = (elIndex == 0) ? "NA" : diffArray[elIndex - 1];
          if (elIndex != 0 && elIndex > 2) {
            if (("".equals(badTargetElements))) {
              badTargetElements += " ??????, ?????? ????????? ????????? ????????? ????????? ?????? ????????? ????????? ?????????. * ".concat(elAction);

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

      //??????????????? -> ???????????? ??????
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
      headers.add("auth", "a3dlYXRoZXItYXBwLWF1dGg=");
      Map<String, Object> weatherParsingData = new LinkedHashMap<>();
      HashMap<String, Object> valueMap = new HashMap<>();

      if(!dcode.equals("") && !dcode.equals("null")){
        URI kwapiUrl = URI.create("https://kwapi.kweather.co.kr/v1/gis/geo/hangaddr?hangCd="+dcode);
        RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, kwapiUrl);
        ResponseEntity<String> res = restTemplate.exchange(req, String.class);

        JSONObject jObj = new JSONObject(res.getBody());
        JSONObject data = jObj.getJSONObject("data");
        String region = data.getString("city_id");

        // App ?????? ?????? ?????? (?????? api ?????? ??? ?????? ??????)
        try {

          weatherParsingData = WeatherApiUtil.weatherTodayApi(region);

        } catch (Exception e) {
          e.printStackTrace();
          logger.error("================V3 data detail API Weather TODAY API ERROR ============, serial :: {}",serial);
        }

        try {
          String urlForDust = "http://kapi.kweather.co.kr/getXML_air_fcast_3times_area.php?mode=n&region=" + region;
          URL url = new URL(urlForDust);
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");
          connection.setRequestProperty("Content-type", "text/plain");
          if (connection.getResponseCode() == 200) {
            StringBuilder result = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
              result.append(line);
            }
            in.close();
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject outdoor = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) parser.parse(result.toString())).get("main");
            weatherData.put("pm10", AES256Util.encrypt(outdoor.get("pm10Value").toString()));
            weatherData.put("pm25", AES256Util.encrypt(outdoor.get("pm25Value").toString()));
            valueMap.put("pm10", outdoor.get("pm10Value"));
            valueMap.put("pm25", outdoor.get("pm25Value"));
          }
          valueMap.put("temp", weatherParsingData.get("P_4"));
          valueMap.put("humi", weatherParsingData.get("P_5"));

        }catch(Exception e){
          e.printStackTrace();
          logger.error("================V3 data detail API Weather TODAY API ERROR ============, serial :: {}",serial);
        }
      }else{
        weatherData.put("pm10", AES256Util.encrypt(CommonConstant.NULL_DATA));
        weatherData.put("pm25", AES256Util.encrypt(CommonConstant.NULL_DATA));
        weatherData.put("date", AES256Util.encrypt(CommonConstant.NULL_DATA));
        valueMap.put("pm10", -999);
        valueMap.put("pm25", -999);
        valueMap.put("temp",  -999);
        valueMap.put("humi",  -999);
      }

      String[] weatherElements = {"pm10", "pm25","temp","humi"};
      List<HashMap<String,Object>> elementInfoList = new ArrayList<>();
      for(String weatherElement : weatherElements){
        if (valueMap.isEmpty()) {
          break;
        }
        HashMap<String,Object> elementInfo = readOnlyMapper.selectElementInfo(weatherElement);
        elementInfo.put("value",AES256Util.encrypt(valueMap.get(weatherElement).toString()));

        int score = weatherElement.equals("temp") || weatherElement.equals("humi") ? -999 : KweatherElemeniUtil.elementCiCalculator(Integer.valueOf(valueMap.get(weatherElement).toString()),weatherElement);
        elementInfo.put("score",AES256Util.encrypt(score != -999 ? Integer.toString(score) : "NA"));

        int grade = 0;
        if ("temp".equals(weatherElement) || "humi".equals(weatherElement)){
          grade = KweatherElementMessageManageUtil.elementLevel("", weatherElement, Double.parseDouble(
                  valueMap.get(weatherElement).toString()));
        } else{
          grade = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(score));
        }
        elementInfo.put("grade",AES256Util.encrypt(Integer.toString(grade)));

        int grade4;
        switch (grade) {
          case 3:
            grade4 = 3;
            break;
          case 4:
            grade4 = 3;
            break;
          case 5:
            grade4 = 4;
            break;
          case 6:
            grade4 = 4;
            break;
          default:
            grade4 = grade;
            break;
        }
        if ("temp".equals(weatherElement) || "humi".equals(weatherElement)) {
          elementInfo.put("grade4", AES256Util.encrypt((grade4 == 0) ? "NA" : Integer.toString(grade4)));
        } else {
          elementInfo.put("grade4", AES256Util.encrypt((grade4 == 0) ? "NA" : Integer.toString(grade)));
        }

        elementInfo.put("index", AES256Util.encrypt((grade == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(weatherElement, String.valueOf(grade))));
        elementInfoList.add(elementInfo);
      }

      //weatherData.put("dongKo", AES256Util.encrypt(weatherParsingData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherParsingData.get("P_1")+""));

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
      resultData.put("country_nm",AES256Util.encrypt("????????????"));
      if(dfname.split(" ").length >=2 && dfname != null){
        resultData.put("sido_nm",AES256Util.encrypt(dfname.split(" ")[0]));
        resultData.put("sg_nm",AES256Util.encrypt(dfname.split(" ")[1]));
        resultData.put("emd_nm",AES256Util.encrypt(dfname.split(" ")[2]));
        resultData.put("hang_cd",AES256Util.encrypt(dcode));
      }else{
        resultData.put("sido_nm",AES256Util.encrypt(CommonConstant.NULL_DATA));
        resultData.put("sg_nm",AES256Util.encrypt(CommonConstant.NULL_DATA));
        resultData.put("emd_nm",AES256Util.encrypt(CommonConstant.NULL_DATA));
        resultData.put("hang_cd",AES256Util.encrypt(CommonConstant.NULL_DATA));
      }
      resultData.put("lat",AES256Util.encrypt(lat));
      resultData.put("lon",AES256Util.encrypt(lon));
      resultData.put("total", AES256Util.encrypt(!collectionData.containsKey(ci) ? "NA" : Math.round(Double.parseDouble(collectionData.get(ci).toString()))+""));
      resultData.put("totalIndex", AES256Util.encrypt(KweatherElementMessageManageUtil.setElementLevelKorName("ci", String.valueOf(ciIndex))));
      resultData.put("totalGrade", AES256Util.encrypt((ciIndex == 0) ? "NA" : ciIndex+""));
      resultData.put("lastUpdated", AES256Util.encrypt(!collectionData.containsKey("cusTm") ? "NA" : collectionData.get("cusTm").toString()));
      Long rt = (Long.parseLong(collectionData.get("timestamp").toString())) + 5 * 60;
      resultData.put("receiveError", AES256Util.encrypt(!collectionData.containsKey("timestamp") ? "NA" :
              String.valueOf(Long.parseLong(rt.toString()+"000")  < (System.currentTimeMillis()))));

      resultData.put("action", AES256Util.encrypt((ciIndex == 0) ? "NA" : KweatherElementMessageManageUtil.CI_ACTION[ciIndex - 1]
              .concat(" ??????, ?????? ????????? ????????? ????????? ????????? ?????? ????????? ????????? ?????????. ").concat(badTargetElements)));

      resultData.put("vents", ventDatas);
      resultData.put("elements", elementDatas);
      resultData.put("elementsAddition", elementDatas2);
      resultData.put("weather", weatherData);

    } catch(HttpClientErrorException e){
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.PLATFORM_API_CALL_EXCEPTION);
    }catch(NumberFormatException e){
      e.printStackTrace();
      throw new ParameterException(ParameterException.PARAMETER_EXCEPTION);
    }catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }catch (Exception e) {
      logger.error("Exception Message :: {}", e.getMessage());
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return resultData;
  }


  public LinkedHashMap<String, Object> getVentStatusData(String ventSerial) throws Exception {
    LinkedHashMap<String, Object> resDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> ventDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> stationDataObj = new LinkedHashMap<String, Object>();
    SensorDataDto ventData, iaqData;
    Map<String, Object> kesrOutInfo = new LinkedHashMap<>();
    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    String today = (new SimpleDateFormat("yyyyMMddHHmm").format(date));

    try {

      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);
      String ventModel = readOnlyMapper.selectDeviceModelByVentSerial(ventSerial);
      ventData = appVentService.selectVentCollectionApi(ventSerial);
      iaqData = appVentService.selectIaqCollectionApi(iaqSerial);
      String hangCd = readOnlyMapper.selectDcode(iaqSerial);
      String aiMode = readOnlyMapper.getVentAiMode(ventSerial);
      HashMap<String,Object> iaqInfo = readOnlyMapper.selectIaqRelatedOaq(iaqSerial);
      String dfName = iaqInfo.get("dfname").toString();

      stationDataObj.put("country_nm","????????????");
      stationDataObj.put("sido_nm",dfName.split(" ")[0]);
      stationDataObj.put("sg_nm",dfName.split(" ")[1]);
      stationDataObj.put("emd_nm",dfName.split(" ")[2]);
      stationDataObj.put("hang_cd",hangCd);

      if(iaqInfo.get("oaq_lat") != null && ventModel.equals("KESR")){
        kesrOutInfo = appVentService.getKesrOutInfo(ventSerial);
      }

      stationDataObj.put("lat",kesrOutInfo.getOrDefault("oaq_lat","NA"));
      stationDataObj.put("lon",kesrOutInfo.getOrDefault("oaq_lon","NA"));
      stationDataObj.put("oaq",kesrOutInfo.getOrDefault("oaq","NA"));
      stationDataObj.put("distance",kesrOutInfo.getOrDefault("distance","NA"));
      stationDataObj.put("outsideType",kesrOutInfo.getOrDefault("outsideType","NA"));

      String oaqSerial = kesrOutInfo.getOrDefault("oaq", "NA").toString();
      if (!oaqSerial.equals("NA")) {
        hangCd = readOnlyMapper.selectDcode(oaqSerial);
      }

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


      }


      ventDataObj.put("receiveError", ventReceiveError);
      ventDataObj.put("dateTime", ventData.getReg_date() == null ? CommonConstant.NULL_DATA : ventData.getReg_date());
      ventDataObj.put("ventModel",ventModel);
      ventDataObj.put("filterAlarm", ventData.getFilter_alarm() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getFilter_alarm()));
      ventDataObj.put("powerMode", ventData.getPower() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getPower()));
      ventDataObj.put("windMode", ventData.getAir_volume() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getAir_volume()));
      ventDataObj.put("exhMode", ventData.getExh_mode() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getExh_mode()));//????????????
      ventDataObj.put("autoMode", ventData.getAuto_mode() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getAuto_mode()));
      ventDataObj.put("airMode", ventData.getAir_mode() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getAir_mode()));//?????????
      ventDataObj.put("aiMode", aiMode == null ? CommonConstant.NULL_DATA : Integer.valueOf(aiMode));

      if(ventDataObj.get("ventModel").equals("KESR")) { //KESR????????? ??????

        if (ventData.getExh_mode().equals("0") && ventData.getAir_mode().equals("0")) {
          ventDataObj.put("ventMode", "H1"); //???????????? 0 & ??????????????? 0 => ????????????
        } else if (ventData.getExh_mode().equals("0") && ventData.getAir_mode().equals("1")) {
          ventDataObj.put("ventMode", "H2"); //???????????? 0 & ??????????????? 1 => ???????????????
        } else if (ventData.getExh_mode().equals("1") && ventData.getAir_mode().equals("0")) {
          ventDataObj.put("ventMode", "H3"); //???????????? 1 & ??????????????? 0 => ??????????????????
        }
      }else{
        ventDataObj.put("ventMode", "H1");
      }

      ventDataObj.put("windMax",ventModel.contains("AIC1")? 3 : 6);

      String setTemp = readOnlyMapper.selectSetTemp(iaqSerial);
      ventDataObj.put("tempBase",setTemp == null ? CommonConstant.NULL_DATA : setTemp);


      int ciIndex = iaqData.getCici() == null ? 0 :
              KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(iaqData.getCici()));

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
      String[] pm10Datas = {"pm10", "ug/m??", iaqData.getPm10() == null ? CommonConstant.NULL_DATA : iaqData.getPm10(),
              iaqData.getCici_pm10() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm10(),"????????????"};
      String[] pm25Datas = {"pm25", "ug/m??", iaqData.getPm25() == null ? CommonConstant.NULL_DATA : iaqData.getPm25(),
              iaqData.getCici_pm25() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm25(),"???????????????"};
      String[] co2Datas = {"co2", "ppm", iaqData.getCo2() == null ? CommonConstant.NULL_DATA : iaqData.getCo2(),
              iaqData.getCici_co2() == null ? CommonConstant.NULL_DATA : iaqData.getCici_co2(),"???????????????"};
//      String[] vocDatas = {"voc", "ppb", iaqData.getVoc() == null ? CommonConstant.NULL_DATA : iaqData.getVoc(),
//              iaqData.getCici_voc() == null ? CommonConstant.NULL_DATA : iaqData.getCici_voc(),"????????????????????????"};
      String[] tempDatas = {"temp", "???", iaqData.getTemp() == null ? CommonConstant.NULL_DATA : iaqData.getTemp(),
              iaqData.getCici_temp() == null ? CommonConstant.NULL_DATA : iaqData.getCici_temp(),"??????"};
      String[] humiDatas = {"humi", "%", iaqData.getHumi() == null ? CommonConstant.NULL_DATA : iaqData.getHumi(),
              iaqData.getCici_humi() == null ? CommonConstant.NULL_DATA : iaqData.getCici_humi(),"??????"};

      elDataList.add(pm10Datas);
      elDataList.add(pm25Datas);
      elDataList.add(co2Datas);
      elDataList.add(tempDatas);
      elDataList.add(humiDatas);

      for (String[] elData : elDataList) {
        Map<String, Object> elMp = new LinkedHashMap<>();
        elMp.put("engName", elData[0]);
        elMp.put("korName",elData[4]);
        elMp.put("score", CommonConstant.NULL_DATA.equals(elData[3]) ? CommonConstant.NULL_DATA : Math.round(Double.valueOf(elData[3])));
        elMp.put("value", CommonConstant.NULL_DATA.equals(elData[2]) ? CommonConstant.NULL_DATA : elData[2]);
        elMp.put("unit", elData[1]);
        elMp.put("index", CommonConstant.NULL_DATA.equals(elData[3]) ? 0
                : KweatherElementMessageManageUtil.setElementLevelKorName(elData[0],
                String.valueOf(KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(elData[3])))));
        elMp.put("grade4", CommonConstant.NULL_DATA.equals(elData[3]) ? 0
                : KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(elData[3])));
        elementList.add(elMp);
      }



      //??????????????? -> ???????????? ??????
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
      headers.add("auth", "a3dlYXRoZXItYXBwLWF1dGg=");

      logger.info("hangCd ={}",hangCd);
      URI kwapiUrl = URI.create("https://kwapi.kweather.co.kr/v1/gis/geo/hangaddr?hangCd="+hangCd);

      RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, kwapiUrl);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);

      JSONObject jObj = new JSONObject(res.getBody());
      JSONObject data = jObj.getJSONObject("data");
      String regionCode = data.getString("city_id");
      //??? ?????? ????????? pm10, pm25 ?????????(??????????????????)
      HashMap<String,Object> valueMap = new HashMap<>();
      Map<String, Object> weatherData = new LinkedHashMap<>();

      try {

        if (!"".equals(regionCode))
          weatherData = WeatherApiUtil.weatherAirCastApi(regionCode, new String[] {
                  "pm10Value", "pm25Value","date"});

      } catch (Exception e) {
        logger.error("==========V3 data vent, Weather F.CAST API ERROR ============");
        e.printStackTrace();
      }
      String dongOutDateTime = weatherData.get("P_3").toString();
      valueMap.put("pm10",weatherData.get("P_1") == null ? CommonConstant.NULL_DATA : weatherData.get("P_1"));
      valueMap.put("pm25",weatherData.get("P_2") == null ? CommonConstant.NULL_DATA : weatherData.get("P_2"));

      //?????? ???????????? receiveError ??????(30??? ????????? ?????? error:true)
      SimpleDateFormat dongFormat = new SimpleDateFormat("yyyyMMddHHmm");
      Date eTime = new Date();
      Date sTime = dongFormat.parse(dongOutDateTime);
      long dongTimeGap= (eTime.getTime() - sTime.getTime()) / 1000 / 60;


      //??? ?????? ????????? humi, temp ?????????
      Map<String, Object> weatherParsingData = new LinkedHashMap<>();
      try {

        weatherParsingData = WeatherApiUtil.weatherTodayApi(regionCode);
      } catch (Exception e) {
        logger.error("==========V3 data vent, Weather TODAY API ERROR ============");
        e.printStackTrace();
      }
      valueMap.put("temp",weatherParsingData.get("P_4"));
      valueMap.put("humi",weatherParsingData.get("P_5"));


      //???????????? receiveError ??????(2?????? ????????? ?????? error:true)
      String weatherDateTime = weatherParsingData.get("P_8").toString();
      SimpleDateFormat weatherFormat = new SimpleDateFormat("yyyyMMddHH");
      sTime = weatherFormat.parse(weatherDateTime);
      long weatherTimeGap= (eTime.getTime() - sTime.getTime()) / 1000 / 60;

      String ci =  "coci";

      String[] weatherElements = {"pm10", "pm25","temp","humi"};
      List<HashMap<String,Object>> outElements = new ArrayList<>();
      HashMap<String,Object> elementInfo = null;
      String outsideType = kesrOutInfo.getOrDefault("outsideType", "NA").toString();
      if(outsideType.equals("1")){
        Map<String, Object> oaq = getSerialToPlatFormData("OAQ", oaqSerial);

        for (String weatherElement : weatherElements){
          elementInfo = readOnlyMapper.selectElementInfo(weatherElement);
          int elIndex;
          int elIndexOld;

          Object elData;
          Object tempElData;

          int ciValue = -999;

          elData = (!oaq.containsKey(weatherElement) ? null :
                  Integer.valueOf(String.valueOf(Math.round(Double.parseDouble(oaq.get(weatherElement).toString())))));

          tempElData = (!oaq.containsKey(weatherElement) ? null :
                  Math.round(Double.parseDouble(oaq.get(weatherElement).toString()) * 10) / 10.0);

          if (elData != null && weatherElement.equals("pm25") || weatherElement.equals("pm10")){
            ciValue = KweatherElemeniUtil.elementCiCalculator(
                    Integer.valueOf((int) Math.round(Double.valueOf(elData.toString()))), weatherElement);
          }

          if ("temp".equals(weatherElement) || "humi".equals(weatherElement)) {
            elIndex = !oaq.containsKey(ci.concat("_").concat(weatherElement)) ? 0
                    : KweatherElementMessageManageUtil.elementLevel("", weatherElement, Double.parseDouble(
                    oaq.get(weatherElement).toString()));

          } else {
            if (ciValue != -999) {
              elIndex = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(ciValue));

            } else {
              elIndex = !oaq.containsKey(ci.concat("_").concat(weatherElement)) ? 0
                      : KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(
                      oaq.get(ci.concat("_").concat(weatherElement)).toString()));
            }
          }
          elementInfo.put("value", !oaq.containsKey(weatherElement) ? "NA" : (!"temp".equals(weatherElement) ? elData : tempElData));
          elementInfo.put("score", oaq.containsKey(ci.concat("_").concat(weatherElement)) ?
                  Math.round(Double.parseDouble(oaq.get(ci.concat("_").concat(weatherElement)).toString()))
                  : (ciValue != -999 ? ciValue : "NA"));

          elementInfo.put("grade", (elIndex == 0) ? "NA" : elIndex);

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

          if ("temp".equals(weatherElement) || "humi".equals(weatherElement)) {
            elementInfo.put("grade4", (elIndexOld == 0) ? "NA" : elIndexOld);

          } else {
            elementInfo.put("grade4", (elIndex == 0) ? "NA" : elIndex);
          }

          elementInfo.put("index", (elIndex == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(weatherElement, String.valueOf(elIndex)));
          outElements.add(elementInfo);
        }
        stationDataObj.put("outDateTime", !oaq.containsKey("cusTm") ? "NA" : oaq.get("cusTm").toString());
      }else{

        for(String weatherElement : weatherElements){
          elementInfo = readOnlyMapper.selectElementInfo(weatherElement);
          elementInfo.put("value",valueMap.get(weatherElement));

          // value??? score ?????????
          int score = weatherElement.equals("temp") || weatherElement.equals("humi") ? -999 : KweatherElemeniUtil.elementCiCalculator(Integer.valueOf(valueMap.get(weatherElement).toString()),weatherElement);
          elementInfo.put("score",score != -999 ? score : "NA");

          // value??? grade ?????????
          int grade = 0;
          if ("temp".equals(weatherElement) || "humi".equals(weatherElement)){
            grade = KweatherElementMessageManageUtil.elementLevel("", weatherElement, Double.parseDouble(
                    valueMap.get(weatherElement).toString()));
          } else{
            grade = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(score));
          }
          elementInfo.put("grade",grade);

          int grade4;
          switch (grade) {
            case 3:
              grade4 = 3;
              break;
            case 4:
              grade4 = 3;
              break;
            case 5:
              grade4 = 4;
              break;
            case 6:
              grade4 = 4;
              break;
            default:
              grade4 = grade;
              break;
          }
          if ("temp".equals(weatherElement) || "humi".equals(weatherElement)) {
            elementInfo.put("grade4", (grade4 == 0) ? "NA" : grade4);
          } else {
            elementInfo.put("grade4", (grade4 == 0) ? "NA" : grade);
          }

          elementInfo.put("index", (grade == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(weatherElement, String.valueOf(grade)));
          outElements.add(elementInfo);
        }
        stationDataObj.put("outDateTime",dongOutDateTime);
      }


      ventDataObj.put("fireAlarm", ventData.getFire_alarm() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getFire_alarm()));
      ventDataObj.put("waterAlarm", ventData.getWater_alarm() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getWater_alarm()));
      ventDataObj.put("devStat", ventData.getDev_stat() == null ? CommonConstant.NULL_DATA : Integer.valueOf(ventData.getDev_stat()));


      stationDataObj.put("airReceiveError",dongTimeGap >= 30 ? true : false);
      stationDataObj.put("weatherReceiveError",weatherTimeGap >= 120 ? true : false);
      stationDataObj.put("outElements",outElements);
      stationDataObj.put("elements", elementList);
      resDataObj.put("ventData", ventDataObj);
      resDataObj.put("stationData", stationDataObj);

    } catch(HttpClientErrorException e){
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.PLATFORM_API_CALL_EXCEPTION);
    }catch(NumberFormatException e){
      e.printStackTrace();
      throw new ParameterException(ParameterException.PARAMETER_EXCEPTION);
    }catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return resDataObj;
  }

  public LinkedHashMap<String, Object> getVentStatusDataEncodeVersion(String ventSerial) throws Exception {
    LinkedHashMap<String, Object> resDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> ventDataObj = new LinkedHashMap<String, Object>();
    LinkedHashMap<String, Object> stationDataObj = new LinkedHashMap<String, Object>();
    Map<String, Object> kesrOutInfo = new LinkedHashMap<>();
    SensorDataDto ventData, iaqData;

    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    String today = (new SimpleDateFormat("yyyyMMddHHmm").format(date));

    try {

      String iaqSerial = readOnlyMapper.ventForIaq(ventSerial);
      String ventModel = readOnlyMapper.selectDeviceModelByVentSerial(ventSerial);
      ventData = appVentService.selectVentCollectionApi(ventSerial);
      iaqData = appVentService.selectIaqCollectionApi(iaqSerial);
      String hangCd = readOnlyMapper.selectDcode(iaqSerial);
      String aiMode = readOnlyMapper.getVentAiMode(ventSerial);
      HashMap<String,Object> iaqInfo = readOnlyMapper.selectIaqRelatedOaq(iaqSerial);
      String dfName = iaqInfo.get("dfname").toString();

      stationDataObj.put("country_nm",AES256Util.encrypt("????????????"));
      stationDataObj.put("sido_nm",AES256Util.encrypt(dfName.split(" ")[0]));
      stationDataObj.put("sg_nm",AES256Util.encrypt(dfName.split(" ")[1]));
      stationDataObj.put("emd_nm",AES256Util.encrypt(dfName.split(" ")[2]));
      stationDataObj.put("hang_cd",AES256Util.encrypt(hangCd));

      if(iaqInfo.get("oaq_lat") != null && ventModel.equals("KESR")){
        kesrOutInfo = appVentService.getKesrOutInfo(ventSerial);
      }

      String oaqSerial = kesrOutInfo.getOrDefault("oaq", "NA").toString();

      if (!oaqSerial.equals("NA")) {
        hangCd = readOnlyMapper.selectDcode(oaqSerial);
      }


      stationDataObj.put("lat", AES256Util.encrypt(String.valueOf(kesrOutInfo.getOrDefault("lat", "NA"))));
      stationDataObj.put("lon", AES256Util.encrypt(String.valueOf(kesrOutInfo.getOrDefault("lon", "NA"))));
      stationDataObj.put("oaq", AES256Util.encrypt(String.valueOf(kesrOutInfo.getOrDefault("oaq", "NA"))));
      stationDataObj.put("distance", AES256Util.encrypt(String.valueOf(kesrOutInfo.getOrDefault("distance", "NA"))));
      stationDataObj.put("outsideType", AES256Util.encrypt(String.valueOf(kesrOutInfo.getOrDefault("outsideType", "NA"))));

      if (!kesrOutInfo.getOrDefault("oaq", "NA").toString().equals("NA")) {

      }

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
      ventDataObj.put("ventModel",AES256Util.encrypt(ventModel));
      ventDataObj.put("filterAlarm", AES256Util.encrypt(ventData.getFilter_alarm() == null ? CommonConstant.NULL_DATA : ventData.getFilter_alarm()));
      ventDataObj.put("powerMode", AES256Util.encrypt(ventData.getPower() == null ? CommonConstant.NULL_DATA : ventData.getPower()));
      ventDataObj.put("windMode", AES256Util.encrypt(ventData.getAir_volume() == null ? CommonConstant.NULL_DATA : ventData.getAir_volume()));
      ventDataObj.put("exhMode", AES256Util.encrypt(ventData.getExh_mode() == null ? CommonConstant.NULL_DATA : ventData.getExh_mode()));//????????????
      ventDataObj.put("autoMode", AES256Util.encrypt(ventData.getAuto_mode() == null ? CommonConstant.NULL_DATA : ventData.getAuto_mode()));
      ventDataObj.put("airMode", AES256Util.encrypt(ventData.getAir_mode() == null ? CommonConstant.NULL_DATA : ventData.getAir_mode()));//?????????
      ventDataObj.put("aiMode", AES256Util.encrypt(aiMode == null ? CommonConstant.NULL_DATA : aiMode));

      if(ventDataObj.get("ventModel").equals("KESR")) {
        if (ventData.getExh_mode().equals("0") && ventData.getAir_mode().equals("0")) {
          ventDataObj.put("ventMode", AES256Util.encrypt("H1")); //???????????? 0 & ??????????????? 0 => ????????????
        } else if (ventData.getExh_mode().equals("0") && ventData.getAir_mode().equals("1")) {
          ventDataObj.put("ventMode", AES256Util.encrypt("H2")); //???????????? 0 & ??????????????? 1 => ???????????????
        } else if (ventData.getExh_mode().equals("1") && ventData.getAir_mode().equals("0")) {
          ventDataObj.put("ventMode", AES256Util.encrypt("H3")); //???????????? 1 & ??????????????? 0 => ??????????????????
        }
      }else{
        ventDataObj.put("ventMode", AES256Util.encrypt("H1"));
      }
      ventDataObj.put("windMax",AES256Util.encrypt(ventModel.contains("AIC1")? "3" : "6"));

      String setTemp = readOnlyMapper.selectSetTemp(iaqSerial) == null ? CommonConstant.NULL_DATA : readOnlyMapper.selectSetTemp(iaqSerial);
      ventDataObj.put("tempBase",AES256Util.encrypt(setTemp));
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
      String[] pm10Datas = {"pm10", "ug/m??", iaqData.getPm10() == null ? CommonConstant.NULL_DATA : iaqData.getPm10(),
              iaqData.getCici_pm10() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm10(),"????????????"};
      String[] pm25Datas = {"pm25", "ug/m??", iaqData.getPm25() == null ? CommonConstant.NULL_DATA : iaqData.getPm25(),
              iaqData.getCici_pm25() == null ? CommonConstant.NULL_DATA : iaqData.getCici_pm25(),"???????????????"};
      String[] co2Datas = {"co2", "ppm", iaqData.getCo2() == null ? CommonConstant.NULL_DATA : iaqData.getCo2(),
              iaqData.getCici_co2() == null ? CommonConstant.NULL_DATA : iaqData.getCici_co2(),"???????????????"};
//      String[] vocDatas = {"voc", "ppb", iaqData.getVoc() == null ? CommonConstant.NULL_DATA : iaqData.getVoc(),
//              iaqData.getCici_voc() == null ? CommonConstant.NULL_DATA : iaqData.getCici_voc(),"????????????????????????"};
      String[] tempDatas = {"temp", "???", iaqData.getTemp() == null ? CommonConstant.NULL_DATA : iaqData.getTemp(),
              iaqData.getCici_temp() == null ? CommonConstant.NULL_DATA : iaqData.getCici_temp(),"??????"};
      String[] humiDatas = {"humi", "%", iaqData.getHumi() == null ? CommonConstant.NULL_DATA : iaqData.getHumi(),
              iaqData.getCici_humi() == null ? CommonConstant.NULL_DATA : iaqData.getCici_humi(),"??????"};

      elDataList.add(pm10Datas);
      elDataList.add(pm25Datas);
      elDataList.add(co2Datas);
      elDataList.add(tempDatas);
      elDataList.add(humiDatas);

      for (String[] elData : elDataList) {
        Map<String, Object> elMp = new LinkedHashMap<>();
        elMp.put("engName", AES256Util.encrypt(elData[0]));
        elMp.put("korName",AES256Util.encrypt(elData[4]));
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

      //??????????????? -> ???????????? ??????
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
      headers.add("auth", "a3dlYXRoZXItYXBwLWF1dGg=");


      URI kwapiUrl = URI.create("https://kwapi.kweather.co.kr/v1/gis/geo/hangaddr?hangCd="+hangCd);
      RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, kwapiUrl);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);

      JSONObject jObj = new JSONObject(res.getBody());
      JSONObject data = jObj.getJSONObject("data");
      String regionCode = data.getString("city_id");


      //??? ?????? ????????? pm10, pm25 ?????????
      HashMap<String,Object> valueMap = new HashMap<>();
      Map<String, Object> weatherData = new LinkedHashMap<>();

      try {

        if (!"".equals(regionCode))
          weatherData = WeatherApiUtil.weatherAirCastApi(regionCode, new String[] {
                  "pm10Grade_who", "pm25Grade_who", "pm10Value", "pm25Value"});

      } catch (Exception e) {
        e.printStackTrace();
        logger.error("==========V3 data vent(Encode version), Weather F.CAST API ERROR .");
      }
      valueMap.put("pm10",weatherData.get("P_3") == null ? CommonConstant.NULL_DATA : weatherData.get("P_3"));
      valueMap.put("pm25",weatherData.get("P_4") == null ? CommonConstant.NULL_DATA : weatherData.get("P_4"));
      String dongOutDateTime = weatherData.get("P_3").toString();
      //?????? ???????????? receiveError ??????(30??? ????????? ?????? error:true)
      SimpleDateFormat dongFormat = new SimpleDateFormat("yyyyMMddHHmm");
      Date eTime = new Date();
      Date sTime = dongFormat.parse(dongOutDateTime);
      long dongTimeGap= (eTime.getTime() - sTime.getTime()) / 1000 / 60;

      //??? ?????? ????????? humi, temp ?????????
      Map<String, Object> weatherParsingData = new LinkedHashMap<>();
      try {

        weatherParsingData = WeatherApiUtil.weatherTodayApi(regionCode);
      } catch (Exception e) {
        logger.error("==========V3 data vent(Encode version), Weather TODAY API ERROR ============");
      }

      valueMap.put("temp",weatherParsingData.get("P_4"));
      valueMap.put("humi",weatherParsingData.get("P_5"));

      //???????????? receiveError ??????(2?????? ????????? ?????? error:true)
      String weatherDateTime = weatherParsingData.get("P_8").toString();
      SimpleDateFormat weatherFormat = new SimpleDateFormat("yyyyMMddHH");
      sTime = weatherFormat.parse(weatherDateTime);
      long weatherTimeGap= (eTime.getTime() - sTime.getTime()) / 1000 / 60;



      String[] weatherElements = {"pm10", "pm25","temp","humi"};
      List<HashMap<String,Object>> outElements = new ArrayList<>();
      for(String weatherElement : weatherElements){
        HashMap<String,Object> elementInfo = readOnlyMapper.selectElementInfo(weatherElement);
        elementInfo.put("engName",AES256Util.encrypt(elementInfo.get("engName").toString()));
        elementInfo.put("unit",AES256Util.encrypt(elementInfo.get("unit").toString()));
        elementInfo.put("viewName",AES256Util.encrypt(elementInfo.get("viewName").toString()));
        elementInfo.put("korName",AES256Util.encrypt(elementInfo.get("korName").toString()));
        elementInfo.put("value",AES256Util.encrypt(valueMap.get(weatherElement).toString()));

        // value??? score ?????????
        int score = weatherElement.equals("temp") || weatherElement.equals("humi") ? -999 : KweatherElemeniUtil.elementCiCalculator(Integer.valueOf(valueMap.get(weatherElement).toString()),weatherElement);
        elementInfo.put("score",AES256Util.encrypt(score != -999 ? Integer.toString(score) : "NA"));

        // value??? grade ?????????
        int grade = 0;
        if ("temp".equals(weatherElement) || "humi".equals(weatherElement)){
          grade = KweatherElementMessageManageUtil.elementLevel("", weatherElement, Double.parseDouble(
                  valueMap.get(weatherElement).toString()));
        } else{
          grade = KweatherElementMessageManageUtil.elementLevel(Double.valueOf(score));
        }
        elementInfo.put("grade",AES256Util.encrypt(Integer.toString(grade)));

        int grade4;
        switch (grade) {
          case 3:
            grade4 = 3;
            break;
          case 4:
            grade4 = 3;
            break;
          case 5:
            grade4 = 4;
            break;
          case 6:
            grade4 = 4;
            break;
          default:
            grade4 = grade;
            break;
        }
        if ("temp".equals(weatherElement) || "humi".equals(weatherElement)) {
          elementInfo.put("grade4", AES256Util.encrypt((grade4 == 0) ? "NA" : Integer.toString(grade4)));
        } else {
          elementInfo.put("grade4", AES256Util.encrypt((grade4 == 0) ? "NA" : Integer.toString(grade)));
        }

        elementInfo.put("index", AES256Util.encrypt((grade == 0) ? "NA" : KweatherElementMessageManageUtil.setElementLevelKorName(weatherElement, String.valueOf(grade))));
        outElements.add(elementInfo);
      }


      ventDataObj.put("fireAlarm", AES256Util.encrypt(ventData.getFire_alarm() == null ? CommonConstant.NULL_DATA : ventData.getFire_alarm()));
      ventDataObj.put("waterAlarm", AES256Util.encrypt(ventData.getWater_alarm() == null ? CommonConstant.NULL_DATA : ventData.getWater_alarm()));
      ventDataObj.put("devStat", AES256Util.encrypt(ventData.getDev_stat() == null ? CommonConstant.NULL_DATA : ventData.getDev_stat()));

      stationDataObj.put("outDateTime",AES256Util.encrypt(dongOutDateTime));
      stationDataObj.put("airReceiveError",AES256Util.encrypt(String.valueOf(dongTimeGap >= 30 ? true : false)));
      stationDataObj.put("weatherReceiveError",AES256Util.encrypt(String.valueOf(weatherTimeGap >= 120 ? true : false)));

      stationDataObj.put("outElements",outElements);
      stationDataObj.put("elements", elementList);
      resDataObj.put("ventData", ventDataObj);
      resDataObj.put("stationData", stationDataObj);

    } catch(HttpClientErrorException e){
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.PLATFORM_API_CALL_EXCEPTION);
    }catch(NumberFormatException e){
      e.printStackTrace();
      throw new ParameterException(ParameterException.PARAMETER_EXCEPTION);
    }catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return resDataObj;
  }



}
