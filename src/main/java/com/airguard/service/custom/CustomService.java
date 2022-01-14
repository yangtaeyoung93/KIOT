package com.airguard.service.custom;

import com.airguard.exception.SQLException;
import com.airguard.mapper.main.custom.clust.ClustMapper;
import com.airguard.mapper.readonly.DatacenterMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.common.Admin;
import com.airguard.model.custom.DataVo;
import com.airguard.model.custom.clust.dto.DeviceInfoDto;
import com.airguard.model.datacenter.DatacenterConnectDto;
import com.airguard.model.platform.PlatformSensorDto;
import com.airguard.model.platform.ResultCollectionHisVo;
import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.model.system.DeviceElements;
import com.airguard.model.system.Group;
import com.airguard.model.system.Member;
import com.airguard.service.platform.PlatformService;
import com.airguard.util.CommonConstant;
import com.airguard.util.Sha256EncryptUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URLEncoder;

@Service
public class CustomService {

  private static final Logger logger = LoggerFactory.getLogger(CustomService.class);

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

  @Autowired
  private DatacenterMapper datacenterMapper;

  @Autowired
  private ClustMapper clustMapper;

  @Autowired
  private PlatformService platformService;

  public boolean customLoginCheck(String userType, String userId, String password) {
    int checkCode = 0;
    String encPassword = Sha256EncryptUtil.ShaEncoder(password);

    switch (userType) {
      case "admin":
        Admin admin = new Admin();
        admin.setUserId(userId);
        admin.setUserPw(encPassword);
        checkCode = readOnlyMapper.loginCheckAdminId(admin);
        break;

      case "group":
        Group group = new Group();
        group.setGroupId(userId);
        group.setGroupPw(encPassword);
        checkCode = readOnlyMapper.loginCheckGroupId(group);
        break;

      case "member":
        Member member = new Member();
        member.setUserId(userId);
        member.setUserPw(encPassword);
        checkCode = readOnlyMapper.loginCheckMemberId(member);
        break;
    }

    return (checkCode == 3) ? true : false;
  }

  public ArrayList getDataFromKiotApi(String serialNum, String deviceType, String searchType,
      String fromDate, String toDate) throws Exception {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create("https://kiotapi.kweather.co.kr/air365/get_stdata.api?serialNum="
        + serialNum + "&deviceType=" + deviceType + "&searchType=" + searchType + "&searchFromDate="
        + fromDate + "&searchToDate=" + toDate);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    Gson gson = new Gson();

    return (ArrayList) gson.fromJson(jObj.toString(), Map.class).get("data");
  }

  public ArrayList getDataFromKiotApiById(int searchType, String Id) throws Exception {

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create("https://kiotapi.kweather.co.kr/air365/get_irdata.api?searchType="
        + searchType + "&searchParam=" + Id);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    Gson gson = new Gson();

    return (ArrayList) gson.fromJson(jObj.toString(), Map.class).get("data");
  }

  public List<ResultCollectionVo> getMonitorDataFromKiotApi(String userType, String groupId,
      String deviceType) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<DatacenterConnectDto> deviceList = new ArrayList<>();

    if (userType.equals("group")) {
      List<String> userIdList = datacenterMapper.selectGroupForUser(groupId);
      for (String userId : userIdList) {
        deviceList.addAll(datacenterMapper.selectUserDevice(userId));
      }
    } else {
      deviceList = datacenterMapper.selectUserDevice(groupId);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR
        + "/" + ("group".equals(userType.toLowerCase()) ? "g-" : "u-") + groupId);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);

    ResponseEntity<String> res = restTemplate.exchange(req, String.class);
    JSONObject jObj = new JSONObject(res.getBody());

    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = datacenterConnectDto.getSerialNum();
      Gson gson = new Gson();

      if (!datacenterConnectDto.getDeviceType().equals(deviceType)) {
        continue;
      }

      vo.setSerial(serial);
      vo.setTestYn(datacenterConnectDto.getTestYn());
      vo.setGroupId(datacenterConnectDto.getGroupId());
      vo.setGroupName(datacenterConnectDto.getGroupName());
      vo.setParentSpaceName(datacenterConnectDto.getParentSpaceName());
      vo.setSpaceName(datacenterConnectDto.getSpaceName());
      vo.setUserId(datacenterConnectDto.getUserId());
      vo.setStationName(datacenterConnectDto.getStationName());
      vo.setProductDt(datacenterConnectDto.getProductDt());
      vo.setVentCnt(datacenterConnectDto.getVentCnt());
      vo.setDeviceType(deviceType);
      vo.setVentsStr(datacenterConnectDto.getVentsStr());
      vo.setLon(datacenterConnectDto.getLon());
      vo.setLat(datacenterConnectDto.getLat());


      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);

        vo.setSensor(resData.getData());
        vo.setReceiveFlag(((Long.parseLong(resData.getService().getTimestamp()))
            + (5 * 60)) > (System.currentTimeMillis() / 1000));
        vo.setTimestamp(resData.getService().getTimestamp());
      }

      resCol.add(vo);

    }

    return resCol;
  }

  public List<Map<String, String>> selectDeviceModelElements(String serial) {
    List<Map<String, String>> elementDataList = new ArrayList<>();
    Map<String, String> elementData;

    for (DeviceElements data : datacenterMapper.selectDeviceModelElements(serial)) {
      elementData = new LinkedHashMap<>();
      elementData.put("elementIdx", data.getElementIdx());
      elementData.put("korName", data.getKorName());
      elementData.put("engName", data.getEngName());
      elementData.put("elementUnit", data.getElementUnit());
      elementData.put("viewName", data.getViewName());

      elementDataList.add(elementData);
    }

    return elementDataList;
  }

  public List<HashMap<String, Object>> getHumErrorCodes(String codeStr) {
    List<HashMap<String, Object>> erorrCodes = new ArrayList<>();

    List<HashMap<String, Object>> dataList = new ArrayList<>();

    HashMap<String, Object> e7 = new HashMap<String, Object>();
    e7.put("message", "압축기 고압에러");
    e7.put("name", "고압SW");
    e7.put("code", "E7");
    e7.put("hexCode", 0x1000);
    e7.put("byteList", hexToBin("1000"));

    HashMap<String, Object> e8 = new HashMap<String, Object>();
    e8.put("message", "물막힘");
    e8.put("name", "배수에러");
    e8.put("code", "E8");
    e8.put("hexCode", 0x0002);
    e8.put("byteList", hexToBin("0002"));

    HashMap<String, Object> e10 = new HashMap<String, Object>();
    e10.put("message", "압축기 저온에러");
    e10.put("name", "실내저온");
    e10.put("code", "E10");
    e10.put("hexCode", 0x2000);
    e10.put("byteList", hexToBin("2000"));

    HashMap<String, Object> e11 = new HashMap<String, Object>();
    e11.put("message", "필터교체 에러");
    e11.put("name", "필터교체");
    e11.put("code", "E11");
    e11.put("hexCode", 0x4000);
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
          erorrCodes.add(eh);
        }
      }
    }

    return erorrCodes;
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

  public HashMap<String, Object> getHumStatusData(String serial) throws Exception {
    LinkedHashMap<String, Object> resultData = new LinkedHashMap<>();
    RestTemplate restTemplate = new RestTemplate();
    List<HashMap<String, Object>> errorCodes = new ArrayList<>();
    JSONObject platData;

    String regDate = "NA";
    String pMode = "NA";
    String aMode = "NA";
    String hMode = "NA";
    String wMode = "NA";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type",
        MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url =
        URI.create(CommonConstant.API_SERVER_HOST_TOTAL.concat(CommonConstant.SEARCH_PATH_SENSOR)
            .concat(CommonConstant.PARAM_SENSOR_VENT).concat("/").concat(serial));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res;

    try {

      res = restTemplate.exchange(req, String.class);
      JSONObject jObj = new JSONObject(res.getBody());

      resultData.put("serial", serial);

      if (!jObj.isNull("data")) {
        platData = (JSONObject) jObj.get("data");
        regDate = platData.get("reg_date").toString();
        pMode = platData.get("power") == "0" ? "00" : "P".concat(platData.get("power").toString());
        aMode = platData.get("auto_mode") == "0" ? "000"
            : "A".concat(platData.get("auto_mode").toString());
        hMode = platData.get("exh_mode") == "0" ? "00"
            : "H".concat(platData.get("exh_mode").toString());
        wMode = platData.get("air_volume") == "0" ? "00"
            : "W".concat(platData.get("air_volume").toString());

        if (!"00000".equals(platData.get("filter_alarm").toString())) {
          errorCodes = getHumErrorCodes(platData.get("filter_alarm").toString());
        }
      }

    } catch (Exception e) {

    }

    resultData.put("regDate", regDate);
    resultData.put("pMode", pMode);
    resultData.put("aMode", aMode);
    resultData.put("hMode", hMode);
    resultData.put("wMode", wMode);
    resultData.put("errorCode", errorCodes);

    return resultData;
  }


  public List<HashMap<String, Object>> selectGroupHumSensorApi(String groupId, String domain)
      throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<HashMap<String, Object>> resCol = new ArrayList<>();
    List<String> userIdList = datacenterMapper.selectGroupForUser(groupId);
    List<DatacenterConnectDto> deviceList = new ArrayList<>();

    String idFormat;
    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39")) {
      idFormat = CommonConstant.G_ID_FORMAT;
    } else {
      idFormat = CommonConstant.T_G_ID_FORMAT;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL
        .concat(CommonConstant.SEARCH_PATH_SENSOR).concat("/").concat(idFormat).concat(groupId));

    logger.error("url :: {}", url);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    logger.error("response :: {}", res.getBody());
    JSONObject jObj = new JSONObject(res.getBody());
    for (String userId : userIdList) {
      deviceList.addAll(datacenterMapper.selectUserVentDevice(userId));
    }

    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      HashMap<String, Object> resultVo = new LinkedHashMap<>();
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = datacenterConnectDto.getSerialNum();
      Gson gson = new Gson();

      resultVo.put("deviceType", "VENT");
      resultVo.put("serial", serial);
      resultVo.put("iaqSerial", datacenterConnectDto.getIaqSerialNum());
      resultVo.put("stationName", datacenterConnectDto.getStationName());
      resultVo.put("groupId", datacenterConnectDto.getGroupId());
      resultVo.put("groupName", datacenterConnectDto.getGroupName());
      resultVo.put("userId", datacenterConnectDto.getUserId());
      resultVo.put("productDt", datacenterConnectDto.getProductDt());

      if (!jObj.isNull(serial)) {
        JSONObject dataObj = jObj.getJSONObject(serial).getJSONObject("data");
        JSONObject serviceObj = jObj.getJSONObject(serial).getJSONObject("service");

        HashMap<String, Object> sensorDataMp = new LinkedHashMap<String, Object>();

        resultVo.put("receiveFlag", (Long.parseLong(serviceObj.get("timestamp").toString())
            + (5 * 60)) > (System.currentTimeMillis() / 1000));

        sensorDataMp.put("regDate",
            !dataObj.has("reg_date") ? "NA" : dataObj.get("reg_date").toString());
        sensorDataMp.put("power", !dataObj.has("power") ? "NA" : dataObj.get("power"));
        sensorDataMp.put("airVolume",
            !dataObj.has("air_volume") ? "NA" : dataObj.get("air_volume"));
        sensorDataMp.put("autoMode", !dataObj.has("auto_mode") ? "NA" : dataObj.get("auto_mode"));
        sensorDataMp.put("exhMode", !dataObj.has("exh_mode") ? "NA" : dataObj.get("exh_mode"));
        sensorDataMp.put("humRtemp", !dataObj.has("hum_rtemp") ? "NA" : dataObj.get("hum_rtemp"));
        sensorDataMp.put("humRhumi", !dataObj.has("hum_rhumi") ? "NA" : dataObj.get("hum_rhumi"));
        sensorDataMp.put("humOtemp", !dataObj.has("hum_otemp") ? "NA" : dataObj.get("hum_otemp"));
        sensorDataMp.put("humOhumi", !dataObj.has("hum_ohumi") ? "NA" : dataObj.get("hum_ohumi"));
        sensorDataMp.put("humCo2", !dataObj.has("hum_co2") ? "NA" : dataObj.get("hum_co2"));
        sensorDataMp.put("humPm10", !dataObj.has("hum_pm10") ? "NA" : dataObj.get("hum_pm10"));
        sensorDataMp.put("humPm25", !dataObj.has("hum_pm25") ? "NA" : dataObj.get("hum_pm25"));
        sensorDataMp.put("watt", !dataObj.has("watt") ? "NA" : dataObj.get("watt"));

        resultVo.put("sensorData", sensorDataMp);
      }

      resCol.add(resultVo);
    }

    return resCol;
  }


  public List<HashMap<String, Object>> selectPrivateHumSensorApi(String userId, String domain)
      throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<HashMap<String, Object>> resCol = new ArrayList<>();

    String idFormat;
    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39")) {
      idFormat = CommonConstant.U_ID_FORMAT;
    } else {
      idFormat = CommonConstant.T_U_ID_FORMAT;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type",
        MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL
        .concat(CommonConstant.SEARCH_PATH_SENSOR).concat("/").concat(idFormat).concat(userId));

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    List<DatacenterConnectDto> deviceList = datacenterMapper.selectUserVentDevice(userId);
    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      HashMap<String, Object> resultVo = new LinkedHashMap<>();
      String serial = datacenterConnectDto.getSerialNum();

      resultVo.put("deviceType", "VENT");
      resultVo.put("serial", serial);
      resultVo.put("iaqSerial", datacenterConnectDto.getIaqSerialNum());
      resultVo.put("stationName", datacenterConnectDto.getStationName());
      resultVo.put("groupId", datacenterConnectDto.getGroupId());
      resultVo.put("groupName", datacenterConnectDto.getGroupName());
      resultVo.put("userId", datacenterConnectDto.getUserId());
      resultVo.put("productDt", datacenterConnectDto.getProductDt());

      if (!jObj.isNull(serial)) {
        JSONObject dataObj = jObj.getJSONObject(serial).getJSONObject("data");
        JSONObject serviceObj = jObj.getJSONObject(serial).getJSONObject("service");

        HashMap<String, Object> sensorDataMp = new LinkedHashMap<String, Object>();

        resultVo.put("receiveFlag", (Long.parseLong(serviceObj.get("timestamp").toString())
            + (5 * 60)) > (System.currentTimeMillis() / 1000));

        sensorDataMp.put("regDate", dataObj.get("reg_date").toString());
        sensorDataMp.put("power", dataObj.get("power"));
        sensorDataMp.put("airVolume", dataObj.get("air_volume"));
        sensorDataMp.put("autoMode", dataObj.get("auto_mode"));
        sensorDataMp.put("exhMode", dataObj.get("exh_mode"));
        sensorDataMp.put("humRtemp", dataObj.get("hum_rtemp"));
        sensorDataMp.put("humRhumi", dataObj.get("hum_rhumi"));
        sensorDataMp.put("humOtemp", dataObj.get("hum_otemp"));
        sensorDataMp.put("humOhumi", dataObj.get("hum_ohumi"));
        sensorDataMp.put("humCo2", dataObj.get("hum_co2"));
        sensorDataMp.put("humPm10", dataObj.get("hum_pm10"));
        sensorDataMp.put("humPm25", dataObj.get("hum_pm25"));

        resultVo.put("sensorData", sensorDataMp);
      }

      resCol.add(resultVo);
    }

    return resCol;
  }


  public List<DeviceInfoDto> iaqRealTimeBySerial(String[] serials) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
    Date today = new Date();
    String toDt = date.format(today);
    cal.setTime(today);
    cal.add(Calendar.MINUTE, -10);
    String fromDt = date.format(cal.getTime());

    List<DeviceInfoDto> result = new ArrayList<>();

    for (int i = 0; i < serials.length; i++) {
      DeviceInfoDto deviceInfoDto = new DeviceInfoDto();

      try {
        deviceInfoDto = clustMapper.selectInfoBySerial(serials[i]);
        deviceInfoDto.setSerial(serials[i]);

        List<ResultCollectionHisVo> datas;
        try {
          datas = platformService.selectSensorApi("iaq", serials[i], fromDt, toDt, "sum", "0");
          deviceInfoDto.setSensor(datas.get(datas.size() - 1));
        } catch (Exception e) {

        }
        result.add(deviceInfoDto);
      } catch (Exception e) {
        System.out.println("iaqRealTimeBySerial ERROR : " + e);
      }


    }

    return result;
  }


  public List<DeviceInfoDto> jejuRealTime() throws Exception {
    List<DeviceInfoDto> result = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    String[] param = {"jeju_iot_maq@jeju.go.kr", "jeju_iot_oaq@jeju.go.kr"};
    String urlString =
        "https://kiotapi.kweather.co.kr/air365/get_irdata.api?searchType=2&searchParam=";

    for (int i = 0; i < param.length; i++) {
      URI url = URI.create(urlString + param[i]);

      RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
      ResponseEntity<String> res = restTemplate.exchange(req, String.class);

      JSONObject jObj = new JSONObject(res.getBody());
      JSONArray ja = jObj.getJSONArray("data");

      for (int d = 0; d < ja.length(); d++) {
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        HashMap<String, Object> sensorDataMp = new LinkedHashMap<String, Object>();

        try {

          deviceInfoDto = clustMapper.selectInfoBySerial(ja.getJSONObject(d).getString("serial"));
          deviceInfoDto.setSerial(ja.getJSONObject(d).getString("serial"));

          sensorDataMp.put("temp", ja.getJSONObject(d).getString("temp"));
          sensorDataMp.put("pm25", ja.getJSONObject(d).getString("pm25"));
          sensorDataMp.put("pm10", ja.getJSONObject(d).getString("pm10"));
          sensorDataMp.put("pm01", ja.getJSONObject(d).getString("pm01"));
          sensorDataMp.put("humi", ja.getJSONObject(d).getString("humi"));
          sensorDataMp.put("tm", ja.getJSONObject(d).getString("tm"));
          sensorDataMp.put("atm", ja.getJSONObject(d).getString("atm"));

          if (param[i].equals("jeju_iot_oaq@jeju.go.kr")) {
            sensorDataMp.put("rain", ja.getJSONObject(d).getString("rain"));
          }

          deviceInfoDto.setSensor(sensorDataMp);

          result.add(deviceInfoDto);

        } catch (Exception e) {
          System.out.println("jejuRealTime ERROR : " + e);
        }
      }
    }

    return result;
  }

  public LinkedHashMap<String, Object> selectGroupPushHistory(String groupId) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> resData = new LinkedHashMap<>();
    List<HashMap<String, Object>> historyList;
    HashMap<String, Object> historyData;

    int resultCode = CommonConstant.R_SUCC_CODE;

    try {

      historyList = new ArrayList<>();

      for (HashMap<String, Object> history : readOnlyMapper.selectGroupPushHistory(groupId)) {
        historyData = new LinkedHashMap<>();
        historyData
            .put("hisIdx", Math.round(Double.parseDouble(history.get("row_num").toString())));
        historyData.putAll(new Gson()
            .fromJson(history.get("message").toString(), new TypeToken<HashMap<String, Object>>() {
            }.getType()));
        historyList.add(historyData);
      }

      resData.put("historyDatas", historyList);

      res.put("data", resData);
      res.put("result", resultCode);

    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    return res;
  }

  public List<HashMap<String, Object>> selectSeochoNoticeList() {
    return readOnlyMapper.selectSeochoNoticeList();
  }
}
