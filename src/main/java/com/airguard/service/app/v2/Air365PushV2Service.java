package com.airguard.service.app.v2;

import com.airguard.exception.SQLException;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.airguard.util.FCMPushManageUtil;
import com.airguard.util.RedisManageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Air365PushV2Service {

  private static final Logger logger = LoggerFactory.getLogger(Air365PushV2Service.class);

  @Autowired
  private RedisManageUtil redisUtil;

  @Autowired
  private FCMPushManageUtil pushUtil;

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

  public LinkedHashMap<String, Object> pushControl(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();

    int resultCode = CommonConstant.R_SUCC_CODE;

    String userType = req.get("userType");
    String target = req.get("target");
    String deviceType = req.get("deviceType");

    try {

      if ("group".equals(userType)) {
        if ("all".equals(target)) {
          for (HashMap<String, String> deviceInfo : readOnlyMapper
              .getSerialListToFcmToken(req.get("token"), req.get("userId"))) {
            if ("all".equals(deviceType) || deviceInfo.get("device_type").equals(deviceType)) {
              pushUtil.pushControlOneF(deviceInfo.get("serial_num"), req);
            }
          }

        } else {
          pushUtil.pushControlOneF(target, req);
        }

      } else {
        if ("all".equals(target)) {
          for (HashMap<String, String> deviceInfo : readOnlyMapper
              .getSerialListToFcmToken(req.get("token"), req.get("userId"))) {
            if ("all".equals(deviceType) || deviceInfo.get("device_type").equals(deviceType)) {
              pushUtil.pushControlOneF(deviceInfo.get("serial_num"), req);
            }
          }

        } else {
          pushUtil.pushControlOneF(target, req);
        }
      }

      res.put("result", resultCode);

    } catch (Exception e) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    return res;
  }

  public HashMap<String, Object> getPushControlDataF(Object receiveControlData, String target) {
    HashMap<String, Object> receiveData = new HashMap<>();

    if (!"NO_DATA".equals(receiveControlData.toString())) {
      ObjectMapper objMapper = new ObjectMapper();

      try {

        receiveData = objMapper.readValue(receiveControlData.toString(), HashMap.class);
        receiveData.put("serial", target);

      } catch (IOException e) {
        e.printStackTrace();
      }

    } else {
      HashMap<String, Object> timeMap = new LinkedHashMap<>();

      timeMap.put("startTime", "08:00");
      timeMap.put("endTime", "20:00");

      receiveData.put("pm10", 0);
      receiveData.put("pm25", 0);
      receiveData.put("co2", 0);
      receiveData.put("vocs", 0);
      receiveData.put("temp", 0);
      receiveData.put("humi", 0);
      receiveData.put("timeFlag", timeMap);
      receiveData.put("serial", target);

    }

    return receiveData;
  }

  public LinkedHashMap<String, Object> pushControlView(HashMap<String, String> req)
      throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    List<Object> resultDatas;

    int resultCode = CommonConstant.R_SUCC_CODE;

    String target = req.get("target");
    String userId =
        "group".equals(req.get("userType")) ? "g-".concat(req.get("userId")) : req.get("userId");
    String token = req.get("token");
    Object receiveControlData;

    try {

      resultDatas = new ArrayList<>();
      if ("all".equals(target)) {
        for (String key : redisUtil
            .getRedisKeyList("FLAG_".concat(userId).concat("_").concat(token).concat("_*"))) {
          String[] strarray = key.split("_");

          receiveControlData = redisUtil.getRedisData(key);

          resultDatas.add(getPushControlDataF(receiveControlData, strarray[strarray.length - 1]));
        }

      } else {
        receiveControlData = redisUtil.getRedisData(
            "FLAG_".concat(userId).concat("_").concat(token).concat("_").concat(target));
        resultDatas.add(getPushControlDataF(receiveControlData, target));
      }

      res.put("data", resultDatas);

      res.put("result", resultCode);

    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    return res;
  }

  public LinkedHashMap<String, Object> pushHistoryList(HashMap<String, String> req)
      throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<>();
    LinkedHashMap<String, Object> resData = new LinkedHashMap<>();
    List<HashMap<String, Object>> historyList;
    HashMap<String, Object> historyData;

    int resultCode = CommonConstant.R_SUCC_CODE;
    int nextHisIdx = 0;
    String userId = req.get("userId");
    String serial = req.get("serial");

    int totalCount = readOnlyMapper.selectPushHistoryListCnt(userId, serial);
    String hisIdx =
        "0".equals(req.get("hisIdx")) ? Integer.toString(totalCount) : req.get("hisIdx");

    try {

      historyList = new ArrayList<>();

      for (HashMap<String, Object> history : readOnlyMapper
          .selectPushHistoryList(userId, serial, hisIdx)) {
        historyData = new LinkedHashMap<>();
        historyData
            .put("hisIdx", Math.round(Double.parseDouble(history.get("row_num").toString())));
        historyData.putAll(new Gson()
            .fromJson(history.get("message").toString(), new TypeToken<HashMap<String, Object>>() {
            }.getType()));
        historyList.add(historyData);
      }

      if (!historyList.isEmpty()) {
        nextHisIdx = (
            Integer.parseInt(historyList.get(historyList.size() - 1).get("hisIdx").toString()) - 1);
      }

      resData.put("totalCount", totalCount);
      resData.put("nextHisIdx", nextHisIdx);
      resData.put("historyDatas", historyList);

      res.put("data", resData);
      res.put("result", resultCode);

    } catch (Exception e) {
      throw new SQLException(SQLException.LIMIT_TARGET_EXCEPTION);
    }

    return res;
  }
}
