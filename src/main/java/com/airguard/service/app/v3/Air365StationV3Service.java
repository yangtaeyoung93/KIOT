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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
    criteriaMp.put("temp", new Integer[]{21, 24, 27, 30, 34, 21, 18, 16, 14, 0}); // 더윰, 추움
    criteriaMp.put("humi", new Integer[]{50, 60, 75, 90, 100, 50, 40, 35, 20, 0}); // 습함, 건조

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

        weatherParsingData = WeatherApiUtil.weatherTodayApi(region, new String[] {
            "dong_ko,", "icon", "temp", "wd_ws", "humi", "snowf", "rainf"});

      } catch (Exception e) {
        logger.error("Weather TODAY API ERROR .");
      }

      String urlForDust = "http://kapi.kweather.co.kr/getXML_air_fcast_3times_area.php?mode=n&region="+region;
      URL url = new URL(urlForDust);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-type", "text/plain");

      HashMap<String,Object> valueMap = new HashMap<>();
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
        valueMap.put("pm10", outdoor.get("pm10Value"));
        valueMap.put("pm25",outdoor.get("pm25Value"));
      }
      valueMap.put("temp",weatherParsingData.get("P_4"));
      valueMap.put("humi",weatherParsingData.get("P_5"));


      String[] weatherElements = {"pm10", "pm25","temp","humi"};
      List<HashMap<String,Object>> elementInfoList = new ArrayList<>();
      for(String weatherElement : weatherElements){
        HashMap<String,Object> elementInfo = readOnlyMapper.selectElementInfo(weatherElement);
        elementInfo.put("value",valueMap.get(weatherElement));

        int score = weatherElement.equals("temp") || weatherElement.equals("humi") ? -999 : KweatherElemeniUtil.elementCiCalculator(Integer.valueOf(valueMap.get(weatherElement).toString()),weatherElement);
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
      weatherData.put("elements",elementInfoList);

      int ciIndex = !collectionData.containsKey(ci) ? 0 : 
        KweatherElementMessageManageUtil.elementLevel(Double.parseDouble(collectionData.get(ci).toString()));

      resultData.put("deviceIdx", Integer.valueOf(deviceIdx));
      resultData.put("deviceSerial", serialNum);
      resultData.put("deviceType", deviceType);
      resultData.put("stationName", stationName);
      resultData.put("country_nm","대한민국");
      resultData.put("sido_nm",dfname.split(" ")[0]);
      resultData.put("sg_nm",dfname.split(" ")[1]);
      resultData.put("emd_nm",dfname.split(" ")[2]);
      resultData.put("hang_cd",dcode);
      resultData.put("lat",lat);
      resultData.put("lon",lon);
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

  public LinkedHashMap<String, Object> getVentStatusData(String ventSerial, String regionCode) throws Exception {
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

    } catch (SQLException e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    } catch (Exception e) {
      throw new ExternalApiException(ExternalApiException.EXTERNAL_API_CALL_EXCEPTION);
    }

    return resDataObj;
  }



}