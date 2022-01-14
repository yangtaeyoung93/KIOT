package com.airguard.service.dashboard;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import com.airguard.mapper.main.platform.PlatformMapper;
import com.airguard.model.common.Search;
import com.airguard.model.dashboard.DashboardDeviceCntDto;
import com.airguard.model.dashboard.DashboardDeviceHisDto;
import com.airguard.model.dashboard.DashboardDeviceVentHisDto;
import com.airguard.model.dashboard.DashboardReceiveCntDto;
import com.airguard.model.dashboard.DashboardReceiveDto;
import com.airguard.model.dashboard.DashboardUserDto;
import com.airguard.model.dashboard.DashboardUserLoginDto;
import com.airguard.model.platform.CollectionDto;
import com.airguard.model.platform.PlatformSensorDto;
import com.airguard.mapper.readonly.DashboardMapper;
import com.airguard.util.CommonConstant;
import com.google.gson.Gson;

@Service
public class DashboardService {

  private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

  @Autowired
  DashboardMapper mapper;

  @Autowired
  PlatformMapper platformMapper;

  public List<DashboardReceiveDto> selectReceiveDashboard(Search search) {
    return mapper.selectReceiveDashboard(search);
  }

  public DashboardUserDto selectUserDashboardCnt() {
    return mapper.selectUserDashboardCnt();
  }

  public List<DashboardUserLoginDto> selectUserDashboardLoginCnt() {
    return mapper.selectUserDashboardLoginCnt();
  }

  public DashboardDeviceCntDto selectDeviceDashboardCnt() {
    return mapper.selectDeviceDashboardCnt();
  }

  public List<DashboardDeviceHisDto> selectDeviceDashboardHisCnt() {
    return mapper.selectDeviceDashboardHisCnt();
  }

  public List<DashboardDeviceVentHisDto> selectDeviceDashboardHisVentCnt() {
    return mapper.selectDeviceDashboardHisVentCnt();
  }

  public Map<String, Object> selectReceiveCntDashboard() {
    List<DashboardReceiveCntDto> resObj = mapper.selectReceiveCntDashboard();

    Map<String, Object> resultMap = new HashMap<>();
    Map<String, Object> statAllMap = new HashMap<>();
    Map<String, Object> statUserMap = new HashMap<>();

    Map<String, Integer> statAllCntIaqMap = new HashMap<>();
    Map<String, Integer> statAllCntOaqMap = new HashMap<>();
    Map<String, Integer> statAllCntDotMap = new HashMap<>();
    Map<String, Integer> statAllCntVentMap = new HashMap<>();
    Map<String, Integer> statAllCntAllMap = new HashMap<>();

    Map<String, Integer> statUserCntIaqMap = new HashMap<>();
    Map<String, Integer> statUserCntOaqMap = new HashMap<>();
    Map<String, Integer> statUserCntDotMap = new HashMap<>();
    Map<String, Integer> statUserCntVentMap = new HashMap<>();
    Map<String, Integer> statUserCntAllMap = new HashMap<>();

    for (DashboardReceiveCntDto ob : resObj) {
      if (ob.getStatType().equals("ALL")) {
        switch (ob.getDeviceType()) {
          case "IAQ":
            statAllCntIaqMap.put("allCnt", ob.getDeviceCnt());
            statAllCntIaqMap.put("receiveCnt", ob.getDeviceCntOk());
            statAllCntIaqMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          case "OAQ":
            statAllCntOaqMap.put("allCnt", ob.getDeviceCnt());
            statAllCntOaqMap.put("receiveCnt", ob.getDeviceCntOk());
            statAllCntOaqMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          case "DOT":
            statAllCntDotMap.put("allCnt", ob.getDeviceCnt());
            statAllCntDotMap.put("receiveCnt", ob.getDeviceCntOk());
            statAllCntDotMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          case "VENT":
            statAllCntVentMap.put("allCnt", ob.getDeviceCnt());
            statAllCntVentMap.put("receiveCnt", ob.getDeviceCntOk());
            statAllCntVentMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          default:
            statAllCntAllMap.put("allCnt", ob.getDeviceCnt());
            statAllCntAllMap.put("receiveCnt", ob.getDeviceCntOk());
            statAllCntAllMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
        }

      } else {
        switch (ob.getDeviceType()) {
          case "IAQ":
            statUserCntIaqMap.put("allCnt", ob.getDeviceCnt());
            statUserCntIaqMap.put("receiveCnt", ob.getDeviceCntOk());
            statUserCntIaqMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          case "OAQ":
            statUserCntOaqMap.put("allCnt", ob.getDeviceCnt());
            statUserCntOaqMap.put("receiveCnt", ob.getDeviceCntOk());
            statUserCntOaqMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          case "DOT":
            statUserCntDotMap.put("allCnt", ob.getDeviceCnt());
            statUserCntDotMap.put("receiveCnt", ob.getDeviceCntOk());
            statUserCntDotMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          case "VENT":
            statUserCntVentMap.put("allCnt", ob.getDeviceCnt());
            statUserCntVentMap.put("receiveCnt", ob.getDeviceCntOk());
            statUserCntVentMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
          default:
            statUserCntAllMap.put("allCnt", ob.getDeviceCnt());
            statUserCntAllMap.put("receiveCnt", ob.getDeviceCntOk());
            statUserCntAllMap.put("unReceiveCnt", ob.getDeviceCntNok());

            break;
        }
      }
    }

    statAllMap.put("iaq", statAllCntIaqMap);
    statAllMap.put("oaq", statAllCntOaqMap);
    statAllMap.put("dot", statAllCntDotMap);
    statAllMap.put("vent", statAllCntVentMap);
    statAllMap.put("all", statAllCntAllMap);

    statUserMap.put("iaq", statUserCntIaqMap);
    statUserMap.put("oaq", statUserCntOaqMap);
    statUserMap.put("dot", statUserCntDotMap);
    statUserMap.put("vent", statUserCntVentMap);
    statUserMap.put("all", statUserCntAllMap);

    resultMap.put("all", statAllMap);
    resultMap.put("user", statUserMap);

    return resultMap;
  }

  public Map<String, Object> getReceiveCnt(int minute) throws Exception {
    Map<String, Object> resultMap = new HashMap<>();

    Map<String, Object> dataMap = new HashMap<>();
    Map<String, Object> userDataMap = new HashMap<>();

    Map<String, Integer> totalCntMap = new HashMap<>();
    Map<String, Integer> userTotalCntMap = new HashMap<>();

    Map<String, Map<String, Integer>> iaqCntOb = selectTotalSensorApi(
        CommonConstant.PARAM_SENSOR_IAQ, minute);
    Map<String, Map<String, Integer>> oaqCntOb = selectTotalSensorApi(
        CommonConstant.PARAM_SENSOR_OAQ, minute);
    Map<String, Map<String, Integer>> dotCntob = selectTotalSensorApi(
        CommonConstant.PARAM_SENSOR_DOT, minute);
    Map<String, Map<String, Integer>> ventCntOb =
        selectTotalSensorVentApi(CommonConstant.PARAM_SENSOR_VENT, minute);

    Map<String, Integer> iaqCntMap = iaqCntOb.get("data");
    Map<String, Integer> oaqCntMap = oaqCntOb.get("data");
    Map<String, Integer> dotCntMap = dotCntob.get("data");
    Map<String, Integer> ventCntMap = ventCntOb.get("data");

    Map<String, Integer> iaqCntMapFilter = iaqCntOb.get("dataUser");
    Map<String, Integer> oaqCntMapFilter = oaqCntOb.get("dataUser");
    Map<String, Integer> dotCntMapFilter = dotCntob.get("dataUser");
    Map<String, Integer> ventCntMapFilter = ventCntOb.get("dataUser");

    dataMap.put("iaq", iaqCntMap);
    dataMap.put("oaq", oaqCntMap);
    dataMap.put("dot", dotCntMap);
    dataMap.put("vent", ventCntMap);

    userDataMap.put("iaq", iaqCntMapFilter);
    userDataMap.put("oaq", oaqCntMapFilter);
    userDataMap.put("dot", dotCntMapFilter);
    userDataMap.put("vent", ventCntMapFilter);

    int totalReceiveCnt = iaqCntMap.get("receiveCnt") + oaqCntMap.get("receiveCnt")
        + dotCntMap.get("receiveCnt") + ventCntMap.get("receiveCnt");
    int totalUnReceiveCnt = iaqCntMap.get("unReceiveCnt") + oaqCntMap.get("unReceiveCnt")
        + dotCntMap.get("unReceiveCnt") + ventCntMap.get("unReceiveCnt");
    int totalAllReceiveCnt = iaqCntMap.get("allCnt") + oaqCntMap.get("allCnt")
        + dotCntMap.get("allCnt") + ventCntMap.get("allCnt");

    int totalFilterReceiveCnt =
        iaqCntMapFilter.get("receiveCnt") + oaqCntMapFilter.get("receiveCnt")
            + dotCntMapFilter.get("receiveCnt") + ventCntMapFilter.get("receiveCnt");
    int totalFilterUnReceiveCnt =
        iaqCntMapFilter.get("unReceiveCnt") + oaqCntMapFilter.get("unReceiveCnt")
            + dotCntMapFilter.get("unReceiveCnt") + ventCntMapFilter.get("unReceiveCnt");
    int totalFilterAllReceiveCnt = iaqCntMapFilter.get("allCnt") + oaqCntMapFilter.get("allCnt")
        + dotCntMapFilter.get("allCnt") + ventCntMapFilter.get("allCnt");

    totalCntMap.put("receiveCnt", totalReceiveCnt);
    totalCntMap.put("unReceiveCnt", totalUnReceiveCnt);
    totalCntMap.put("allCnt", totalAllReceiveCnt);

    userTotalCntMap.put("receiveCnt", totalFilterReceiveCnt);
    userTotalCntMap.put("unReceiveCnt", totalFilterUnReceiveCnt);
    userTotalCntMap.put("allCnt", totalFilterAllReceiveCnt);

    dataMap.put("total", totalCntMap);
    userDataMap.put("total", userTotalCntMap);

    resultMap.put("data", dataMap);
    resultMap.put("dataUser", userDataMap);

    return resultMap;
  }

  public Map<String, Map<String, Integer>> selectTotalSensorApi(String paramType, int minute)
      throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Map<String, Integer>> resultMap = new HashMap<>();
    Map<String, Integer> dataMap = new HashMap<>();
    Map<String, Integer> dataFilterMap = new HashMap<>();

    List<CollectionDto> deviceList = platformMapper.selectCollectionDevice("00", "N");

    String deviceType = "";
    switch (paramType) {
      case CommonConstant.PARAM_SENSOR_IAQ:
        deviceType = "IAQ";
        break;
      case CommonConstant.PARAM_SENSOR_OAQ:
        deviceType = "OAQ";
        break;
      case CommonConstant.PARAM_SENSOR_DOT:
        deviceType = "DOT";
        break;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + paramType);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    String jObj2 = res.getBody();

    JSONObject jObj = new JSONObject(jObj2);

    Gson gson = new Gson();

    int receiveCnt = 0;
    int unReceiveCnt = 0;
    int receiveFilterCnt = 0;
    int unReceiveFilterCnt = 0;

    long currentUt = System.currentTimeMillis() / 1000;
    int minuteUt = minute * 60;

    Iterator<?> i = jObj.keys();
    while (i.hasNext()) {
      String key = i.next().toString();
      PlatformSensorDto resData = gson.fromJson(jObj.getString(key), PlatformSensorDto.class);
      long getUt = Long.parseLong(resData.getService().getTimestamp());

      if ((getUt + minuteUt) > currentUt) {
        receiveCnt++; // (플랫폼 시간 + 5분) > 현재 시간, ==> 수신
      } else {
        unReceiveCnt++;
      }
    }

    logger.debug("paramType : {}", paramType);

    for (CollectionDto collectionDto : deviceList) {
      String serial = collectionDto.getSerialNum();
      if (!deviceType.equals(collectionDto.getDeviceType())) {
        continue;
      }

      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);
        long getUt = Long.parseLong(resData.getService().getTimestamp());

        if ((getUt + minuteUt) > currentUt) {
          receiveFilterCnt++;
        } else {
          unReceiveFilterCnt++;
        }
      } else {
        unReceiveFilterCnt++;
      }
    }

    dataMap.put("receiveCnt", receiveCnt);
    dataMap.put("unReceiveCnt", unReceiveCnt);
    dataMap.put("allCnt", (receiveCnt + unReceiveCnt));

    dataFilterMap.put("receiveCnt", receiveFilterCnt);
    dataFilterMap.put("unReceiveCnt", unReceiveFilterCnt);
    dataFilterMap.put("allCnt", (receiveFilterCnt + unReceiveFilterCnt));

    resultMap.put("dataUser", dataFilterMap);
    resultMap.put("data", dataMap);
    return resultMap;
  }

  public Map<String, Map<String, Integer>> selectTotalSensorVentApi(String paramType, int minute)
      throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Map<String, Integer>> resultMap = new HashMap<>();
    Map<String, Integer> dataMap = new HashMap<>();
    Map<String, Integer> dataFilterMap = new HashMap<>();

    List<CollectionDto> deviceList = platformMapper.selectCollectionDeviceVent();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
        CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + paramType);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    Gson gson = new Gson();

    int receiveCnt = 0;
    int unReceiveCnt = 0;
    int receiveFilterCnt = 0;
    int unReceiveFilterCnt = 0;

    long currentUt = System.currentTimeMillis() / 1000;
    int minuteUt = minute * 60;

    Iterator<?> i = jObj.keys();
    while (i.hasNext()) {
      String key = i.next().toString();
      PlatformSensorDto resData = gson.fromJson(jObj.getString(key), PlatformSensorDto.class);
      long getUt = Long.parseLong(resData.getService().getTimestamp());

      if ((getUt + minuteUt) > currentUt) {
        receiveCnt++;
      } else {
        unReceiveCnt++;
      }
    }

    for (CollectionDto collectionDto : deviceList) {
      String serial = collectionDto.getSerialNum();

      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);
        long getUt = Long.parseLong(resData.getService().getTimestamp());

        if ((getUt + minuteUt) > currentUt) {
          receiveFilterCnt++;
        } else {
          unReceiveFilterCnt++;
        }
      } else {
        unReceiveFilterCnt++;
      }
    }

    dataMap.put("receiveCnt", receiveCnt);
    dataMap.put("unReceiveCnt", unReceiveCnt);
    dataMap.put("allCnt", (receiveCnt + unReceiveCnt));

    dataFilterMap.put("receiveCnt", receiveFilterCnt);
    dataFilterMap.put("unReceiveCnt", unReceiveFilterCnt);
    dataFilterMap.put("allCnt", (receiveFilterCnt + unReceiveFilterCnt));

    resultMap.put("dataUser", dataFilterMap);
    resultMap.put("data", dataMap);
    return resultMap;
  }

}
