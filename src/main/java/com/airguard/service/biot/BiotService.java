package com.airguard.service.biot;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.airguard.model.biot.DongCollectionDto;
import com.airguard.model.platform.PlatformSensorDto;
import com.airguard.mapper.readonly.BiotMapper;
import com.airguard.util.CommonConstant;
import com.google.gson.Gson;

@Service
public class BiotService {

  @Autowired
  private BiotMapper mapper;

  private static final String DONG_PM = "p";
  private static final String DONG_LOC = "l";

  public int apiAuthCheck(String idType, String id, String apiType, String allowIp) {
    return mapper.apiAuthCheck(idType, id, apiType, allowIp);
  }

  public Map<String, Object> selectIaqSensorApi(String idType, String id) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> resMap = new LinkedHashMap<>();

    List<HashMap<String, String>> deviceList;
    List<Map<String, Object>> iaqList = new ArrayList<>();

    if ((idType.toUpperCase()).equals("USER")) {
      deviceList = mapper.selectMemberDeviceList(id);
    } else {
      deviceList = mapper.selectGroupDeviceList(id);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR
        + CommonConstant.PARAM_SENSOR_IAQ);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    for (HashMap<String, String> device : deviceList) {
      Map<String, Object> iaqMap = new LinkedHashMap<>();
      String serial = device.get("serial_num");
      String stationName = device.get("station_name");
      Gson gson = new Gson();

      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);
        iaqMap.put("serial_no", serial);
        iaqMap.put("station_name", stationName);
        iaqMap.put("date", resData.getData().getTm());
        iaqMap.put("temp", resData.getData().getTemp() == null ? null
            : Double.parseDouble(resData.getData().getTemp()));
        iaqMap.put("humi", resData.getData().getHumi() == null ? null
            : Integer.parseInt(resData.getData().getHumi()));
        iaqMap.put("pm10", resData.getData().getPm10() == null ? null
            : Integer.parseInt(resData.getData().getPm10()));
        iaqMap.put("pm25", resData.getData().getPm25() == null ? null
            : Integer.parseInt(resData.getData().getPm25()));
        iaqMap.put("pm01", resData.getData().getPm01() == null ? null
            : Integer.parseInt(resData.getData().getPm01()));
        iaqMap.put("noise", resData.getData().getNoise() == null ? null
            : Integer.parseInt(resData.getData().getNoise()));
        iaqMap.put("co2", resData.getData().getCo2() == null ? null
            : Integer.parseInt(resData.getData().getCo2()));
        iaqMap.put("voc", resData.getData().getVoc() == null ? null
            : Integer.parseInt(resData.getData().getVoc()));
        iaqMap.put("co", resData.getData().getCo() == null ? null : Integer.parseInt(resData.getData().getCo()));
        iaqMap.put("hcho", resData.getData().getHcho() == null ? null
            : Integer.parseInt(resData.getData().getHcho()));
        iaqMap.put("cici", resData.getData().getCici() == null ? null
            : Integer.parseInt(resData.getData().getCici()));

        iaqList.add(iaqMap);
      }
    }

    resMap.put("result", 1L);
    resMap.put("group_id", id);
    resMap.put("iaq_count", iaqList.size());
    resMap.put("iaq_list", iaqList);

    return resMap;
  }

  public Map<String, Object> selectOaqSensorApi(String idType, String id) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> resMap = new LinkedHashMap<>();

    List<HashMap<String, String>> deviceList;
    List<Map<String, Object>> oaqList = new ArrayList<>();

    if ((idType.toUpperCase()).equals("USER")) {
      deviceList = mapper.selectMemberDeviceList(id);
    } else {
      deviceList = mapper.selectGroupDeviceList(id);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR
        + CommonConstant.PARAM_SENSOR_OAQ);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    for (HashMap<String, String> device : deviceList) {
      Map<String, Object> oaqMap = new LinkedHashMap<>();
      Gson gson = new Gson();
      String serial = device.get("serial_num");
      String stationName = device.get("station_name");

      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);
        oaqMap.put("serial_no", serial);
        oaqMap.put("station_name", stationName);
        oaqMap.put("date", resData.getData().getTm());
        oaqMap.put("temp", resData.getData().getTemp() == null ? null
            : Double.parseDouble(resData.getData().getTemp()));
        oaqMap.put("humi", resData.getData().getHumi() == null ? null
            : Integer.parseInt(resData.getData().getHumi()));
        oaqMap.put("pm10", resData.getData().getPm10() == null ? null
            : Integer.parseInt(resData.getData().getPm10()));
        oaqMap.put("pm25", resData.getData().getPm25() == null ? null
            : Integer.parseInt(resData.getData().getPm25()));
        oaqMap.put("noise", resData.getData().getNoise() == null ? null
            : Integer.parseInt(resData.getData().getNoise()));
        oaqMap.put("coci", resData.getData().getCoci() == null ? null
            : Integer.parseInt(resData.getData().getCoci()));

        /*
         * 산림복지진흥원 요청 데이터
         * */
        if (resData.getData().getWindd() != null) {
          oaqMap.put("wd", resData.getData().getWindd());
        }
        if (resData.getData().getWinds() != null) {
          oaqMap.put("ws", resData.getData().getWinds());
        }
        if (resData.getData().getLux() != null) {
          oaqMap.put("lx", resData.getData().getLux());
        }
        if (resData.getData().getUv() != null) {
          oaqMap.put("uv", resData.getData().getUv());
        }
        if (resData.getData().getWbgt() != null) {
          oaqMap.put("wbgt", resData.getData().getWbgt());
        }

        oaqList.add(oaqMap);
      }
    }

    resMap.put("result", 1L);
    resMap.put("group_id", id);
    resMap.put("oaq_count", oaqList.size());
    resMap.put("oaq_list", oaqList);

    return resMap;
  }

  public Map<String, Object> selectDongCollectionList(String dataType) {
    Map<String, Object> resMap = new LinkedHashMap<>();
    List<Map<String, Object>> siList = new ArrayList<>();

    List<DongCollectionDto> siDataList = new ArrayList<>();
    List<DongCollectionDto> guDataList = new ArrayList<>();
    List<DongCollectionDto> dongDataList = new ArrayList<>();

    List<DongCollectionDto> dCollectionList = new ArrayList<>();
    if (dataType.equals(DONG_PM)) {
      dCollectionList = mapper.selectDongCollectionList();
    } else if (dataType.equals(DONG_LOC)) {
      dCollectionList = mapper.selectDongLocationList();
    }

    for (DongCollectionDto data : dCollectionList) {
      if (data.getDtype() == 1) {
        siDataList.add(data);
      } else if (data.getDtype() == 2) {
        guDataList.add(data);
      } else {
        dongDataList.add(data);
      }
    }

    Long update = 0L;
    for (DongCollectionDto si : siDataList) {
      Map<String, Object> siMap = new LinkedHashMap<>();
      List<Map<String, Object>> guList = new ArrayList<>();

      siMap.put("rCode", si.getDcode());
      siMap.put("rName", si.getDname());

      for (DongCollectionDto gu : guDataList) {
        if (si.getSdcode().equals(gu.getSdcode())) {
          Map<String, Object> guMap = new LinkedHashMap<>();
          List<Map<String, Object>> dongList = new ArrayList<>();

          guMap.put("gCode", gu.getDcode());
          guMap.put("gName", gu.getDname());

          for (DongCollectionDto dong : dongDataList) {
            if (gu.getSdcode().equals(dong.getSdcode())
                && gu.getSggcode().equals(dong.getSggcode())) {
              Map<String, Object> dongMap = new LinkedHashMap<>();

              if (update < dong.getUpDate()) {
                update = dong.getUpDate();
              }

              dongMap.put("dCode", dong.getDcode());
              dongMap.put("dName", dong.getDname());

              if (dataType.equals(DONG_PM)) {
                dongMap.put("pm10Value", dong.getPm10Value());
                dongMap.put("pm25Value", dong.getPm25Value());
              } else if (dataType.equals(DONG_LOC)) {
                dongMap.put("dLongitude", dong.getLon());
                dongMap.put("dLatitude", dong.getLat());
              }

              dongList.add(dongMap);
            }
          }

          guMap.put("dongList", dongList);
          guList.add(guMap);
        }
      }

      siMap.put("guList", guList);
      siList.add(siMap);
    }

    resMap.put("result", 1L);
    resMap.put("update", update.toString());
    resMap.put("rList", siList);

    return resMap;
  }
}
