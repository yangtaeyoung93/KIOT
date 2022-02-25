package com.airguard.service.datacenter;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.airguard.exception.SQLException;
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

import com.airguard.model.datacenter.DatacenterConnectDto;
import com.airguard.model.platform.PlatformSensorDto;
import com.airguard.model.platform.ResultCollectionVo;
import com.airguard.model.system.DeviceElements;
import com.airguard.mapper.readonly.DatacenterMapper;
import com.airguard.util.CommonConstant;
import com.google.gson.Gson;

@Service
public class DatacenterService {

  @Autowired
  private DatacenterMapper mapper;

  private static final Logger logger = LoggerFactory.getLogger(DatacenterService.class);

  public List<String> selectDeviceType(String userId) {
    return mapper.selectDeviceType(userId);
  }

  public List<String> selectGroupDeviceType(String groupId) {
    return mapper.selectGroupDeviceType(groupId);
  }

  public List<String> selectGroupForUser(String groupId){ return mapper.selectGroupForUser(groupId);}

  public List<DatacenterConnectDto> selectUserVentDevice(String userId){ return mapper.selectUserVentDevice(userId);}

  public List<DeviceElements> selectDeviceModelElements(String serial) throws SQLException {

    List<DeviceElements> result;

    try {

      List<DeviceElements> list = mapper.selectDeviceModelElements(serial);

      if (list.size() < 1) {
        throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
      }
      result = list;
    } catch (Exception e) {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    return result;
  }

  public List<DeviceElements> selectDeviceModelElements(String type, String userId)
          throws SQLException {
    List<DeviceElements> result = new ArrayList<>();
    List<String> serialList = mapper.selectMemberDeviceSerialList(type, userId);

    if (serialList.size() > 0) {
      for (String serial : serialList) {
        result.addAll(mapper.selectDeviceModelElements(serial));
      }
    } else {
      throw new SQLException(SQLException.NULL_TARGET_EXCEPTION);
    }

    return result;
  }

  public List<ResultCollectionVo> selectPrivateSensorApi(String userId, String paramType,
                                                         String domain) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<DatacenterConnectDto> deviceList =
            mapper.selectUserDevice(userId) == null ? new ArrayList<>()
                    : mapper.selectUserDevice(userId);

    String idFormat;
    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39") || domain.equals("220.95.238.50")) {
      idFormat = CommonConstant.U_ID_FORMAT;
    } else {
      idFormat = CommonConstant.T_U_ID_FORMAT;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
            CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/" + idFormat
                    + userId);

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());

    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      if (datacenterConnectDto.getSerialNum() == null) {
        continue;
      }

      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = datacenterConnectDto.getSerialNum();
      Gson gson = new Gson();

      if (!datacenterConnectDto.getDeviceType().equals(paramType)) {
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
      vo.setDeviceType(paramType);
      vo.setVentCnt(datacenterConnectDto.getVentCnt());
      vo.setVentsStr(datacenterConnectDto.getVentsStr());

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

  public List<ResultCollectionVo> selectGroupSensorApi(String groupId, String paramType,
                                                       String domain) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<String> userIdList = mapper.selectGroupForUser(groupId);
    List<DatacenterConnectDto> deviceList = new ArrayList<>();

    String idFormat;
    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39") || domain.equals("220.95.238.50")) {
      idFormat = CommonConstant.G_ID_FORMAT;
    } else {
      idFormat = CommonConstant.T_G_ID_FORMAT;
    }

    for (String userId : userIdList) {
      deviceList.addAll(mapper.selectUserDevice(userId));
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
            CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/" + idFormat
                    + groupId);
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = datacenterConnectDto.getSerialNum();
      Gson gson = new Gson();

      if (!datacenterConnectDto.getDeviceType().equals(paramType)) {
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
      vo.setDeviceType(paramType);
      vo.setVentsStr(datacenterConnectDto.getVentsStr());
      vo.setLon(datacenterConnectDto.getLon());
      vo.setLat(datacenterConnectDto.getLat());

      if (!jObj.isNull(serial)) {
        PlatformSensorDto resData = gson.fromJson(jObj.getString(serial), PlatformSensorDto.class);
        vo.setSensor(resData.getData());
        vo.setReceiveFlag(((Long.parseLong(resData.getService().getTimestamp())) + (5 * 60)) > (
                System.currentTimeMillis() / 1000));
        vo.setTimestamp(resData.getService().getTimestamp());
      }

      resCol.add(vo);
    }

    return resCol;
  }

  public List<ResultCollectionVo> selectPrivateVentSensorApi(String userId, String domain)
          throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<DatacenterConnectDto> deviceList = mapper.selectUserVentDevice(userId);

    String idFormat;
    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39") || domain.equals("220.95.238.50")) {
      idFormat = CommonConstant.U_ID_FORMAT;
    } else {
      idFormat = CommonConstant.T_U_ID_FORMAT;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
            CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/" + idFormat
                    + userId);
    logger.debug("URL : {}", url.toString());

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = datacenterConnectDto.getSerialNum();
      Gson gson = new Gson();

      vo.setSerial(serial);
      vo.setIaqSerial(datacenterConnectDto.getIaqSerialNum());
      vo.setTestYn(datacenterConnectDto.getTestYn());
      vo.setGroupId(datacenterConnectDto.getGroupId());
      vo.setGroupName(datacenterConnectDto.getGroupName());
      vo.setParentSpaceName(datacenterConnectDto.getParentSpaceName());
      vo.setSpaceName(datacenterConnectDto.getSpaceName());
      vo.setUserId(datacenterConnectDto.getUserId());
      vo.setStationName(datacenterConnectDto.getStationName());
      vo.setProductDt(datacenterConnectDto.getProductDt());
      vo.setAiMode(datacenterConnectDto.getAiMode());
      vo.setDeviceType("VENT");

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

  public List<ResultCollectionVo> selectGroupVentSensorApi(String groupId, String domain)
          throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    List<ResultCollectionVo> resCol = new ArrayList<>();
    List<String> userIdList = mapper.selectGroupForUser(groupId);
    List<DatacenterConnectDto> deviceList = new ArrayList<>();

    String idFormat;
    if (domain.equals("220.95.238.45") || domain.equals("220.95.238.39") || domain.equals("220.95.238.50")) {
      idFormat = CommonConstant.G_ID_FORMAT;
    } else {
      idFormat = CommonConstant.T_G_ID_FORMAT;
    }

    for (String userId : userIdList) {
      deviceList.addAll(mapper.selectUserVentDevice(userId));
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

    URI url = URI.create(
            CommonConstant.API_SERVER_HOST_TOTAL + CommonConstant.SEARCH_PATH_SENSOR + "/" + idFormat
                    + groupId);
    logger.debug("selectPrivateVentSensorApi URL, {}", url.toString());

    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = restTemplate.exchange(req, String.class);

    JSONObject jObj = new JSONObject(res.getBody());
    for (DatacenterConnectDto datacenterConnectDto : deviceList) {
      ResultCollectionVo vo = new ResultCollectionVo();
      String serial = datacenterConnectDto.getSerialNum();
      Gson gson = new Gson();

      vo.setSerial(serial);
      vo.setIaqSerial(datacenterConnectDto.getIaqSerialNum());
      vo.setTestYn(datacenterConnectDto.getTestYn());
      vo.setGroupId(datacenterConnectDto.getGroupId());
      vo.setGroupName(datacenterConnectDto.getGroupName());
      vo.setParentSpaceName(datacenterConnectDto.getParentSpaceName());
      vo.setSpaceName(datacenterConnectDto.getSpaceName());
      vo.setUserId(datacenterConnectDto.getUserId());
      vo.setStationName(datacenterConnectDto.getStationName());
      vo.setProductDt(datacenterConnectDto.getProductDt());
      vo.setAiMode(datacenterConnectDto.getAiMode());
      vo.setDeviceType("VENT");

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

  public Map<String, Object> collectionDataListAvg(List<ResultCollectionVo> data) {
    Map<String, Object> result = new LinkedHashMap<>();
    int iaqCnt = 0;
    int oaqCnt = 0;

    double iaqPm10 = 0;
    double iaqPm25 = 0;
    double iaqHcho = 0;
    double iaqCo = 0;
    double iaqCo2 = 0;
    double iaqRn = 0;
    double iaqTemp = 0;
    double iaqHumi = 0;

    double oaqPm10 = 0;
    double oaqPm25 = 0;
    double oaqHcho = 0;
    double oaqCo = 0;
    double oaqCo2 = 0;
    double oaqRn = 0;
    double oaqTemp = 0;
    double oaqHumi = 0;

    String updateDt = "";

    for (ResultCollectionVo vo : data) {
      String deviceType = vo.getDeviceType();

      if (deviceType.equals("IAQ")) {
        iaqCnt++;
        updateDt = vo.getSensor().getTm();
        iaqPm10 += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getPm10() == null) ? "0"
                        : vo.getSensor().getPm10());
        iaqPm25 += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getPm25() == null) ? "0"
                        : vo.getSensor().getPm25());
        iaqHcho += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getHcho() == null) ? "0"
                        : vo.getSensor().getHcho());
        iaqCo += Double.parseDouble((vo.getSensor() == null || vo.getSensor().getCo() == null) ? "0"
                : vo.getSensor().getCo());
        iaqCo2 += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getCo2() == null) ? "0"
                        : vo.getSensor().getCo2());
        iaqRn += Double.parseDouble((vo.getSensor() == null || vo.getSensor().getRn() == null) ? "0"
                : vo.getSensor().getRn());
        iaqTemp += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getTemp() == null) ? "0"
                        : vo.getSensor().getTemp());
        iaqHumi += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getHumi() == null) ? "0"
                        : vo.getSensor().getHumi());

      } else if (deviceType.equals("OAQ")) {
        oaqCnt++;
        updateDt = vo.getSensor().getTm();
        oaqPm10 += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getPm10() == null) ? "0"
                        : vo.getSensor().getPm10());
        oaqPm25 += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getPm25() == null) ? "0"
                        : vo.getSensor().getPm25());
        oaqHcho += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getHcho() == null) ? "0"
                        : vo.getSensor().getHcho());
        oaqCo += Double.parseDouble((vo.getSensor() == null || vo.getSensor().getCo() == null) ? "0"
                : vo.getSensor().getCo());
        oaqCo2 += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getCo2() == null) ? "0"
                        : vo.getSensor().getCo2());
        oaqRn += Double.parseDouble((vo.getSensor() == null || vo.getSensor().getRn() == null) ? "0"
                : vo.getSensor().getRn());
        oaqTemp += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getTemp() == null) ? "0"
                        : vo.getSensor().getTemp());
        oaqHumi += Double.parseDouble(
                (vo.getSensor() == null || vo.getSensor().getHumi() == null) ? "0"
                        : vo.getSensor().getHumi());
      }
    }

    result.put("iaqCnt", iaqCnt);
    result.put("iaqPm10", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqPm10 / iaqCnt));
    result.put("iaqPm25", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqPm25 / iaqCnt));
    result.put("iaqHcho", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqHcho / iaqCnt));
    result.put("iaqCo", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqCo / iaqCnt));
    result.put("iaqCo2", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqCo2 / iaqCnt));
    result.put("iaqRn", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqRn / iaqCnt));
    result.put("iaqTemp", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqTemp / iaqCnt));
    result.put("iaqHumi", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(iaqHumi / iaqCnt));

    result.put("oaqCnt", oaqCnt);
    result.put("oaqPm10", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqPm10 / oaqCnt));
    result.put("oaqPm25", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqPm25 / oaqCnt));
    result.put("oaqHcho", (iaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqHcho / oaqCnt));
    result.put("oaqCo", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqCo / oaqCnt));
    result.put("oaqCo2", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqCo2 / oaqCnt));
    result.put("oaqRn", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqRn / oaqCnt));
    result.put("oaqTemp", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqTemp / oaqCnt));
    result.put("oaqHumi", (oaqCnt == 0) ? CommonConstant.NULL_DATA : Math.round(oaqHumi / oaqCnt));

    result.put("updateDt", updateDt);
    return result;
  }
}
