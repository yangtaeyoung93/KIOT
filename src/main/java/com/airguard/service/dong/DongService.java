package com.airguard.service.dong;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.airguard.model.platform.StationNameVo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.airguard.mapper.main.dong.DongMapper;
import com.airguard.model.dong.*;
import com.airguard.model.platform.AirMapKorDto;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class DongService {

  private static final Logger logger = LoggerFactory.getLogger(DongService.class);

  @Autowired
  DongMapper mapper;

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

  public List<SiData> siList() {
    return mapper.siList();
  }

  public List<GuData> guList(SearchDong search) {
    return mapper.guList(search);
  }

  public List<DongData> dongList(SearchDong search) {
    return mapper.dongList(search);
  }

  public List<Dong> selectDongSearch(SearchDong search) {
    return mapper.selectDongSearch(search);
  }

  public List<Dong> selectSiGunGuSearch(SearchDong search) {
    return mapper.selectSiGunGuSearch(search);
  }

  public List<DongGeo> selectDongList() {
    return mapper.selectDongList();
  }

  public List<AirGeo> selectAirEquiList() {
    return mapper.selectAirEquiList();
  }

  public List<OaqGeo> selectOaqEquiList() {
    return mapper.selectOaqEquiList();
  }

  public List<AirCity> selectAirEquiCnt(String value) {
    return mapper.selectAirEquiCnt(value);
  }

  public List<OaqCity> selectOaqEquiCnt() {
    return mapper.selectOaqEquiCnt();
  }

  public List<AirEqui> refAirEquiSearch(SearchDong search) {
    return mapper.refAirEquiSearch(search);
  }

  public List<OaqEqui> refOaqEquiSearch(SearchDong search) {
    return mapper.refOaqEquiSearch(search);
  }

  /*
   * ==============================================
   */

  public List<SiData> siListDev() {
    return readOnlyMapper.siList();
  }

  public List<GuData> guListDev(SearchDong search) {
    return readOnlyMapper.guList(search);
  }

  public List<DongData> dongListDev(SearchDong search) {
    int isCode = 2;
    if (search.getSearchSdcode().equals("null") && !search.getSearchSggcode().equals("null")) {
      isCode = 1;
    } else if (search.getSearchSdcode().equals("null") && search.getSearchSggcode()
        .equals("null")) {
      isCode = 0;
    }
    return readOnlyMapper.dongList(search, isCode);
  }

  public List<Dong> selectDongSearchDev(SearchDong search) {
    return readOnlyMapper.selectDongSearch(search);
  }

  public List<Dong> selectSiGunGuSearchDev(SearchDong search) {
    return readOnlyMapper.selectSiGunGuSearch(search);
  }

  public List<DongGeo> selectDongListDev() {
    return readOnlyMapper.selectDongList();
  }

  public List<AirGeo> selectAirEquiListDev() {
    return readOnlyMapper.selectAirEquiList();
  }

  public List<OaqGeo> selectOaqEquiListDev() {
    return readOnlyMapper.selectOaqEquiList();
  }

  public List<AirCity> selectAirEquiCntDev(String value) {
    return readOnlyMapper.selectAirEquiCnt(value);
  }

  public List<OaqCity> selectOaqEquiCntDev() {
    return readOnlyMapper.selectOaqEquiCnt();
  }

  public List<AirEqui> refAirEquiSearchDev(SearchDong search) {
    return readOnlyMapper.refAirEquiSearch(search);
  }

  public List<OaqEqui> refOaqEquiSearchDev(SearchDong search) {
    return readOnlyMapper.refOaqEquiSearch(search);
  }

  public List<HashMap<String, Object>> selectOpenApiDongList() {
    return readOnlyMapper.selectOpenApiDongList();
  }

  /*
   * ==============================================
   */

  public List<OaqGeo> selectDongPmList(String deviceType) {
    return readOnlyMapper.selectDongPmList(deviceType);
  }

  public List<?> selectPmOffsetRef(String deviceType, String serial) {
    if (deviceType.equals("o")) {
      return readOnlyMapper.refOaqEquiPmSearch(serial);
    } else {
      return readOnlyMapper.refAirEquiPmSearch(serial);
    }
  }

  public List<AirGeoInfo> selectOaqGeo() {
    return readOnlyMapper.selectOaqGeo("0000000000");
  }

  public List<CmiData> oaqRefHistoryDev(String equiKey, String mapType, String selectedDate)
      throws Exception {
    List<CmiData> oList = new ArrayList<>();
    String apiType = mapType.equals("O") ? "kiot." : "dot.";

    RestTemplate restTemplate = new RestTemplate();

    HashMap<String, Object> resultMap = new HashMap<>();
    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    URI url = URI
        .create(CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + "?start="
            + selectedDate
            + "&m=sum:kw-oaq-sensor-" + apiType + equiKey + URLEncoder
            .encode("{sensor=tm|pm10|pm25}", "UTF-8"));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    resultMap.put("statusCode", res.getStatusCodeValue());
    resultMap.put("header", res.getHeaders());
    resultMap.put("body", res.getBody());

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());
    ObjectMapper objectMapper = new ObjectMapper();
    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object tag = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map tagMap = objectMapper.convertValue(tag, Map.class);

      String sensor = (String) tagMap.get("sensor");
      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "tm":
            tmMap.put(key, (Double) reMap.get(key));
            break;
          case "pm10":
            pm10Map.put(key, (Double) reMap.get(key));
            break;
          case "pm25":
            pm25Map.put(key, (Double) reMap.get(key));
            break;
        }
      }
    }

    for (String key : pm10Map.keySet()) {
      CmiData vo = new CmiData();

      long timestamp = Long.parseLong(key);
      Date date = new Date(timestamp * 1000L);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);
      vo.setRegDate(formattedDate);
      vo.setEquiInfoKey(equiKey);
      vo.setPm10Value(pm10Map.get(key) == null ? "0" : pm10Map.get(key).toString());
      vo.setPm25Value(pm25Map.get(key) == null ? "0" : pm25Map.get(key).toString());

      oList.add(vo);
    }

    return oList;
  }

  public List<OracleData> airRefHistoryDev(String airId, String selectedDate) throws Exception {
    List<OracleData> oList = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();

    HashMap<String, Object> resultMap = new HashMap<>();
    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    URI url = URI
        .create(CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + "?start="
            + selectedDate + "&m=sum:airkorea-aq." + airId + URLEncoder
            .encode("{sensor=tm|pm10|pm25}", "UTF-8"));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    resultMap.put("statusCode", res.getStatusCodeValue());
    resultMap.put("header", res.getHeaders());
    resultMap.put("body", res.getBody());

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());
    ObjectMapper objectMapper = new ObjectMapper();
    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object tag = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map tagMap = objectMapper.convertValue(tag, Map.class);

      String sensor = (String) tagMap.get("sensor");
      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "tm":
            tmMap.put(key, (Double) reMap.get(key));
            break;
          case "pm10":
            pm10Map.put(key, (Double) reMap.get(key));
            break;
          case "pm25":
            pm25Map.put(key, (Double) reMap.get(key));
            break;
        }
      }
    }

    for (String key : pm10Map.keySet()) {
      OracleData vo = new OracleData();

      long timestamp = Long.parseLong(key);
      Date date = new Date(timestamp * 1000L);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);
      vo.setRegDate(formattedDate);
      vo.setAirCode(airId);
      vo.setPm10Value(pm10Map.get(key) == null ? "0" : pm10Map.get(key).toString());
      vo.setPm25Value(pm25Map.get(key) == null ? "0" : pm25Map.get(key).toString());

      oList.add(vo);
    }

    return oList;
  }

  public List<DongHisData> dongHistoryDev(String dcode, String selectedDate) throws Exception {
    List<DongHisData> oList = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();

    HashMap<String, Object> resultMap = new HashMap<>();
    Map<String, Double> tmMap = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    URI url = URI
        .create(CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + "?start="
            + selectedDate + "&m=sum:kma-aq." + dcode + URLEncoder
            .encode("{sensor=pm10|pm25}", "UTF-8"));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    resultMap.put("statusCode", res.getStatusCodeValue());
    resultMap.put("header", res.getHeaders());
    resultMap.put("body", res.getBody());

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());
    ObjectMapper objectMapper = new ObjectMapper();
    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object tag = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map tagMap = objectMapper.convertValue(tag, Map.class);

      String sensor = (String) tagMap.get("sensor");
      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "tm":
            tmMap.put(key, (Double) reMap.get(key));
            break;
          case "pm10":
            pm10Map.put(key, (Double) reMap.get(key));
            break;
          case "pm25":
            pm25Map.put(key, (Double) reMap.get(key));
            break;
        }
      }
    }

    for (String key : pm10Map.keySet()) {
      DongHisData vo = new DongHisData();

      long timestamp = Long.parseLong(key);
      Date date = new Date(timestamp * 1000L);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);
      vo.setRegDate(formattedDate);
      vo.setDcode(dcode);
      vo.setPm10Value(pm10Map.get(key) == null ? "0" : pm10Map.get(key).toString());
      vo.setPm25Value(pm25Map.get(key) == null ? "0" : pm25Map.get(key).toString());

      oList.add(vo);
    }

    return oList;
  }

  public List<?> dataList() throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    JSONObject jObj = new JSONObject();
    List<Dong> resList = new ArrayList<>();
    List<Dong> dongDataList = readOnlyMapper.selectDongDataList();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI
        .create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_DONG_SENSOR
            + CommonConstant.PARAM_SENSOR_TYPE);

    try {

      RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);
      jObj = new JSONObject(res.getBody());
    } catch (Exception e) {
      logger.error(
          "Call dataList API (with. platform) FAIL : [ ErrorMsg : API Url & response datas Check, URL : {} ]",
          url);
      return resList;
    }

    for (Dong dong : dongDataList) {
      Dong vo = new Dong();
      String dcode = dong.getDcode();

      Gson gson = new Gson();
      vo.setDcode(dcode);
      vo.setRowNum(dong.getRowNum());
      vo.setDname(dong.getDname());
      vo.setDfname(dong.getDfname());
      vo.setLon(dong.getLon());
      vo.setLat(dong.getLat());

      if (!jObj.isNull(dcode)) {
        PlatformDong platform = gson.fromJson(jObj.getString(dcode), PlatformDong.class);
        long timestamp = Long.parseLong(platform.getService().getTimestamp());
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);

        vo.setRegDate(formattedDate);
        vo.setPmInfo(platform.getData());
      }

      resList.add(vo);
    }

    return resList;
  }

  public List<DongDetail> historyData(String dcode, String startTime, String endTime,
      String standard,
      String type) throws Exception {
    List<DongDetail> oList = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();

    HashMap<String, Object> resultMap = new LinkedHashMap<>();
    Map<String, Double> pm10Map = new LinkedHashMap<>();
    Map<String, Double> pm25Map = new LinkedHashMap<>();
    Map<String, Double> pm10GradeMap = new LinkedHashMap<>();
    Map<String, Double> pm25GradeMap = new LinkedHashMap<>();
    Map<String, Double> coMap = new LinkedHashMap<>();
    Map<String, Double> no2Map = new LinkedHashMap<>();
    Map<String, Double> o3Map = new LinkedHashMap<>();
    Map<String, Double> so2Map = new LinkedHashMap<>();
    Map<String, Double> khaiMap = new LinkedHashMap<>();
    Map<String, Double> coGradeMap = new LinkedHashMap<>();
    Map<String, Double> no2GradeMap = new LinkedHashMap<>();
    Map<String, Double> o3GradeMap = new LinkedHashMap<>();
    Map<String, Double> so2GradeMap = new LinkedHashMap<>();
    Map<String, Double> khaiGradeMap = new LinkedHashMap<>();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    String queryString;
    URI url = null;

    if (type.equals("kWeather")) {
      queryString = "?start=" + URLEncoder.encode(startTime, "UTF-8") + "&end=" + URLEncoder
          .encode(endTime, "UTF-8")
          + "&m=" + URLEncoder.encode("avg:".concat(standard), "UTF-8") + ":kma-aq." + dcode;
      url = URI.create(
          CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + queryString
              + URLEncoder.encode("{sensor=pm*}", "UTF-8"));
    } else if (type.equals("airKor")) {
      queryString = "?start=" + URLEncoder.encode(startTime, "UTF-8") + "&end=" + URLEncoder
          .encode(endTime, "UTF-8")
          + "&m=" + URLEncoder.encode("avg:".concat(standard), "UTF-8") + ":airkorea-aq." + dcode;
      url = URI.create(
          CommonConstant.API_SERVER_HOST_DEVICE + CommonConstant.SEARCH_PATH_QUERY + queryString
              + URLEncoder.encode("{sensor=*}", "UTF-8"));
    }

    assert url != null;
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    resultMap.put("statusCode", res.getStatusCodeValue());
    resultMap.put("header", res.getHeaders());
    resultMap.put("body", res.getBody());

    Gson gson = new Gson();
    TypeToken<List<Map>> typeToken = new TypeToken<List<Map>>() {
    };
    List<Map> collectionList = gson.fromJson(res.getBody(), typeToken.getType());
    ObjectMapper objectMapper = new ObjectMapper();

    assert collectionList != null;
    for (Map map : collectionList) {
      JSONObject jObj = getJsonStringFromMap(map);

      Object ob = jObj.get("dps");
      Object tag = jObj.get("tags");

      Map reMap = objectMapper.convertValue(ob, Map.class);
      Map tagMap = objectMapper.convertValue(tag, Map.class);

      String sensor = (String) tagMap.get("sensor");
      for (String key : (Iterable<String>) reMap.keySet()) {
        switch (sensor) {
          case "pm10":
            pm10Map.put(key, (Double) reMap.get(key));
            break;
          case "pm25":
            pm25Map.put(key, (Double) reMap.get(key));
            break;
          case "pm10_grade":
            pm10GradeMap.put(key, (Double) reMap.get(key));
            break;
          case "pm25_grade":
            pm25GradeMap.put(key, (Double) reMap.get(key));
            break;
          case "co":
            coMap.put(key, (Double) reMap.get(key));
            break;
          case "o3":
            o3Map.put(key, (Double) reMap.get(key));
            break;
          case "no2":
            no2Map.put(key, (Double) reMap.get(key));
            break;
          case "so2":
            so2Map.put(key, (Double) reMap.get(key));
            break;
          case "khai":
            khaiMap.put(key, (Double) reMap.get(key));
            break;
          case "co_grade":
            coGradeMap.put(key, (Double) reMap.get(key));
            break;
          case "o3_grade":
            o3GradeMap.put(key, (Double) reMap.get(key));
            break;
          case "no2_grade":
            no2GradeMap.put(key, (Double) reMap.get(key));
            break;
          case "so2_grade":
            so2GradeMap.put(key, (Double) reMap.get(key));
            break;
          case "khai_grade":
            khaiGradeMap.put(key, (Double) reMap.get(key));
            break;
        }
      }
    }
    for (String key : pm10Map.keySet()) {
      DongDetail vo = new DongDetail();

      long timestamp = Long.parseLong(key);
      Date date = new Date(timestamp * 1000L);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
      String formattedDate = sdf.format(date);
      vo.setRegDate(formattedDate);
      if (pm10Map.get(key) != null) {
        vo.setPm10(pm10Map.get(key).toString());
      }
      if (pm25Map.get(key) != null) {
        vo.setPm25(pm25Map.get(key).toString());
      }
      if (pm10GradeMap.get(key) != null) {
        vo.setPm10_grade(pm10GradeMap.get(key).toString());
      }
      if (pm25GradeMap.get(key) != null) {
        vo.setPm25_grade(pm25GradeMap.get(key).toString());
      }
      if (coMap.get(key) != null) {
        vo.setCo(coMap.get(key).toString());
      }
      if (o3Map.get(key) != null) {
        vo.setO3(o3Map.get(key).toString());
      }
      if (so2Map.get(key) != null) {
        vo.setSo2(so2Map.get(key).toString());
      }
      if (no2Map.get(key) != null) {
        vo.setNo2(no2Map.get(key).toString());
      }
      if (khaiMap.get(key) != null) {
        vo.setKhai(khaiMap.get(key).toString());
      }
      if (coGradeMap.get(key) != null) {
        vo.setCo_grade(coGradeMap.get(key).toString());
      }
      if (o3GradeMap.get(key) != null) {
        vo.setO3_grade(o3GradeMap.get(key).toString());
      }
      if (so2GradeMap.get(key) != null) {
        vo.setSo2_grade(so2GradeMap.get(key).toString());
      }
      if (no2GradeMap.get(key) != null) {
        vo.setNo2_grade(no2GradeMap.get(key).toString());
      }
      if (khaiGradeMap.get(key) != null) {
        vo.setKhai_grade(khaiGradeMap.get(key).toString());
      }
      oList.add(vo);
    }
    return oList;

  }

  public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
    JSONObject jsonObject = new JSONObject();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      try {
        jsonObject.put(key, value);
      } catch (JSONException e) {
        logger.error("JSON E, " + e.getMessage());
      }
    }

    return jsonObject;
  }

  public void downloadDongHisDataMultiDownload(String type, String scode, String gcode,
      String dcode, String startTime,
      String endTime, String standard, String pmType, HttpServletResponse res) throws Exception {
    SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
    SimpleDateFormat formatSdf = new SimpleDateFormat("yyyyMMdd");

    SXSSFWorkbook workbook = new SXSSFWorkbook(10000);
    workbook.setCompressTempFiles(true);
    Map<String, List<DongDetail>> datas = new LinkedHashMap<>();
    List<Dong> targetList;

    boolean hourStandardFlag = false;
    String paramStandard;

    try {

      paramStandard = standard;

      if (standard.equals("hour-standard")) {
        hourStandardFlag = true;
        paramStandard = "1h-first-none";
      }

      targetList = readOnlyMapper.selectExcelDownDongList(scode, gcode, dcode, type);

      for (Dong target : targetList) {
        datas.put(target.getDfname(),

            historyData(target.getDcode(), startTime, endTime, paramStandard,
                "kWeather"));
      }

      dongHisDataSheetMaker(workbook, datas,
          makeDataDownDateTimesKeyF(startTime, endTime, standard), hourStandardFlag,
          pmType);

      String startDtFormat = formatSdf.format(parseSdf.parse(startTime));
      Calendar cal = Calendar.getInstance();
      cal.setTime(parseSdf.parse(endTime));
      cal.add(Calendar.DATE, 1);
      String endDtFormat = formatSdf.format(cal.getTime());

      res.setHeader("Set-Cookie", "fileDownload=true; path=/");
      res.setHeader("Content-Disposition",
          "attachment;fileName=DATA" + "_" + pmType.toUpperCase() + "_" + startDtFormat + "_"
              + endDtFormat + ".xlsx");
      res.setContentType("application/vnd.ms-excel; charset=UTF-8");

      OutputStream out = new BufferedOutputStream(res.getOutputStream());
      workbook.write(out);
      out.flush();
      out.close();

    } catch (Exception e) {
      res.setHeader("Set-Cookie", "fileDownload=false; path=/");
      res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
      res.setHeader("Content-Type", "text/html; charset=utf-8");
      OutputStream out = null;
      try {

        out = res.getOutputStream();
        byte[] data = "fail..".getBytes();
        out.write(data, 0, data.length);

      } catch (Exception ignore) {
        ignore.printStackTrace();

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

  public List<String> makeDataDownDateTimesKeyF(String startTime, String endTime, String standard)
      throws Exception {
    List<String> timeKeys = new LinkedList<>();
    SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
    SimpleDateFormat formatSdf = new SimpleDateFormat("yyyyMMddHHmmss");

    boolean loopFlag = true;
    Calendar cal;
    String endTimeStr = formatSdf.format(parseSdf.parse(endTime));

    int index = 0;
    standardLoop:
    switch (standard) {
      case "sum":
        timesLoop:
        while (loopFlag) {

          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.MINUTE, (index * 10));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break;
          }

          timeKeys.add(time);
          index++;
        }

        break;
      case "hour-standard":
        timesLoop:
        while (loopFlag) {
          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.MINUTE, (index * 60));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break timesLoop;
          }

          timeKeys.add(time);
          index++;
        }

        break;
      case "5m-avg-none":
        timesLoop:
        while (loopFlag) {
          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.MINUTE, (index * 5));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break;
          }
          logger.error("time ={}",time);
          timeKeys.add(time);
          index++;
        }

        break;
      case "1h-avg-none":
        timesLoop:
        while (loopFlag) {
          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.HOUR, (index));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break;
          }

          timeKeys.add(time);
          index++;
        }

        break;
      case "1d-avg-none":
        timesLoop:
        while (loopFlag) {
          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.DATE, (index));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break;
          }

          timeKeys.add(time);
          index++;
        }

        break standardLoop;
      case "1n-avg-none":
        timesLoop:
        while (loopFlag) {
          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.MONTH, (index));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break;
          }

          timeKeys.add(time);
          index++;
        }

        break;
      default:
        timesLoop:
        while (loopFlag) {
          cal = Calendar.getInstance();
          cal.setTime(parseSdf.parse(startTime));
          cal.add(Calendar.MINUTE, (index * 10));
          String time = formatSdf.format(cal.getTime());

          if (Long.parseLong(endTimeStr) < Long.parseLong(time)) {
            break;
          }

          timeKeys.add(time);
          index++;
        }

        break;
    }

    return timeKeys;
  }

  public void dongHisDataSheetMaker(SXSSFWorkbook workbook, Map<String, List<DongDetail>> datas,
      List<String> timeKeys,
      boolean hourStandardFlag, String pmType) throws Exception {

    int rowIndex = 0;

    Sheet sheet = workbook.createSheet("sheet");

    int widthSize = 4000;
    sheet.setColumnWidth(0, widthSize);
    sheet.setColumnWidth(1, widthSize);
    sheet.setColumnWidth(2, widthSize);

    CellStyle styleOfBoardFillFontBlack11 = workbook.createCellStyle();
    styleOfBoardFillFontBlack11.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
    styleOfBoardFillFontBlack11.setFillPattern(CellStyle.SOLID_FOREGROUND);

    try {

      Row row = sheet.createRow(rowIndex);

      Cell cellHeadSi = row.createCell(0);
      cellHeadSi.setCellStyle(styleOfBoardFillFontBlack11);
      cellHeadSi.setCellValue("광역시도");
      Cell cellHeadGu = row.createCell(1);
      cellHeadGu.setCellStyle(styleOfBoardFillFontBlack11);
      cellHeadGu.setCellValue("시군구");
      Cell cellHeadDong = row.createCell(2);
      cellHeadDong.setCellStyle(styleOfBoardFillFontBlack11);
      cellHeadDong.setCellValue("읍면동");



      int keyIndex = 3;

      for (String s : datas.keySet()) {
        for (DongDetail d : datas.get(s)) {
          sheet.setColumnWidth(keyIndex, widthSize);
          String timeKey = d.getRegDate().substring(0,d.getRegDate().length()-2);
          Cell cellHeadTime = row.createCell(keyIndex++);
          cellHeadTime.setCellStyle(styleOfBoardFillFontBlack11);
          cellHeadTime.setCellValue(timeKey.substring(0, 4) + "-" + timeKey.substring(4, 6) + "-"
                  + timeKey.substring(6, 8) + (" " + timeKey.substring(8, 10)) + ":" + timeKey
                  .substring(10, 12));
        }
       break;
      }

      rowIndex++;
      row = sheet.createRow(rowIndex);

      for (String s : datas.keySet()) {
        keyIndex = 3;

        row = sheet.createRow(rowIndex++);
        String[] dfNames = s.split("\\s");

        Cell cell0 = row.createCell(0);
        cell0.setCellValue(dfNames[0]);
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(dfNames[1]);
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(dfNames[2]);

        for (DongDetail dongData : datas.get(s)) {
          if (hourStandardFlag && !(dongData.getRegDate().substring(10)).equals("0000")) {
            continue;
          }

          Cell cellDatas = row.createCell(keyIndex++);
          if ("pm10".equals(pmType)) {
            cellDatas.setCellValue(setfloor(dongData.getPm10()));
          } else if ("pm25".equals(pmType)) {
            cellDatas.setCellValue(setfloor(dongData.getPm25()));
          }

        }
      }

    } catch (Exception e) {
      logger.error("null");
    }
  }
  public String setfloor(String value){

    return String.format("%.2f", Double.parseDouble(value));
  }
  public List<AirGeoInfo> selectAirKorApi(String dCode) throws Exception {

    if (dCode == null) {
      dCode = "0000000000";
    } else if (dCode.length() == 2) {
      dCode = dCode + "00000000";
    }

    RestTemplate restTemplate = new RestTemplate();
    List<AirGeoInfo> resCol = new ArrayList<>();
    List<AirGeoInfo> airKorList = readOnlyMapper.selectOaqGeo(dCode);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI
        .create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_DONG_SENSOR
            + "/airkorea-aq");

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    airLoop : for (AirGeoInfo airGeoInfo : airKorList) {
      AirGeoInfo vo = new AirGeoInfo();
      String code = airGeoInfo.getAAirCode();
      Gson gson = new Gson();

      vo.setAAirCode(airGeoInfo.getAAirCode());
      vo.setAAirName(airGeoInfo.getAAirName());
      vo.setAAirCity(airGeoInfo.getAAirCity());
      vo.setAAirAddr(airGeoInfo.getAAirAddr());
      vo.setLat(airGeoInfo.getLat());
      vo.setLon(airGeoInfo.getLon());
      vo.setUseYn(airGeoInfo.getUseYn());
      vo.setDCode(airGeoInfo.getDCode());

      if (!jObj.isNull(code)) {
        AirMapKorDto resData = gson.fromJson(jObj.getString(code), AirMapKorDto.class);
        vo.setSensor(resData.getData());
        vo.setTimeStamp(resData.getService().getTimestamp());

        vo.setReceiveFlag(((Long.parseLong(vo.getTimeStamp().toString())) + (119 * 60)) > (
            System.currentTimeMillis() / 1000));

      } else {
        continue airLoop;
      }

      resCol.add(vo);
    }

    return resCol;
  }

  public List<StationNameVo> selectAirKorName(String dCode)  {

    if (dCode == null) {
      dCode = "0000000000";
    } else if (dCode.length() == 2) {
      dCode = dCode + "00000000";
    }

    List<StationNameVo> resCol = new ArrayList<>();
    List<AirGeoInfo> airKorList = readOnlyMapper.selectOaqGeo(dCode);


    airLoop : for (AirGeoInfo airGeoInfo : airKorList) {
      StationNameVo vo = new StationNameVo();

      vo.setSerial(airGeoInfo.getAAirCode());
      vo.setStationName(airGeoInfo.getAAirName());

      resCol.add(vo);
    }

    return resCol;
  }
  public List<Map<String, Object>> selectAirKorApiLightly(String dCode) throws Exception {
    List<Map<String, Object>> resCol = new ArrayList<>();
    if (dCode == "") {
      dCode = "0000000000";
    } else if (dCode.length() == 2) {
      dCode = dCode + "00000000";
    }

    RestTemplate restTemplate = new RestTemplate();
    List<AirGeoInfo> airKorList = readOnlyMapper.selectOaqGeo(dCode);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url = URI
        .create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_DONG_SENSOR.concat("/airkorea-aq"));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    airLoop : for (AirGeoInfo airGeoInfo : airKorList) {
      Map<String, Object> dataMp = new LinkedHashMap<>();
      Map<String, Object> latlonMp = new LinkedHashMap<>();

      String code = airGeoInfo.getAAirCode();

      dataMp.put("serial", airGeoInfo.getAAirCode());
      latlonMp.put("lat", airGeoInfo.getLat());
      latlonMp.put("lon", airGeoInfo.getLon());
      dataMp.put("latlng", latlonMp);

      dataMp.put("stationName", airGeoInfo.getAAirName());
      dataMp.put("deviceType", "AK");

      if (!jObj.isNull(code)) {
        JSONObject dataObj = jObj.getJSONObject(code).getJSONObject("data");
        dataMp.put("pm10", !dataObj.has("pm10") ? null : dataObj.getInt("pm10"));
        dataMp.put("pm25", !dataObj.has("pm25") ? null : dataObj.getInt("pm25"));

      } else {
        continue airLoop;
      }

      resCol.add(dataMp);
    }

    return resCol;
  }
}
