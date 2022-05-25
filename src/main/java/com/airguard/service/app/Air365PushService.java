package com.airguard.service.app;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.RedisManageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Air365PushService {

  private static final Logger logger = LoggerFactory.getLogger(Air365PushService.class);

  @Autowired
  RedisManageUtil redisUtil;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public LinkedHashMap<String, Object> pushControlOneF(String target, HashMap<String, String> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 0;

    try {

      JSONObject elObj = new JSONObject();
      JSONObject timeObj = new JSONObject();

      elObj.put("pm10", Integer.parseInt(req.get("pm10")));
      elObj.put("pm25", Integer.parseInt(req.get("pm25")));
      elObj.put("co2", Integer.parseInt(req.get("co2")));
      elObj.put("vocs", Integer.parseInt(req.get("vocs")));
      elObj.put("temp", Integer.parseInt(req.get("temp")));
      elObj.put("humi", Integer.parseInt(req.get("humi")));
      elObj.put("filterAlarm", Integer.parseInt(req.get("filterAlarm")));
      timeObj.put("startTime", req.get("startTime"));
      timeObj.put("endTime", req.get("endTime"));
      elObj.put("timeFlag", timeObj);


      if (redisUtil.setRedisData("FLAG_".concat(req.get("token").toString()).concat("_").concat(target), elObj.toString())) 
        resultCode = 1;

    } catch (JSONException e) {
      logger.error("==== PUSH CONTROL EXCEPTION {} ====", e.getMessage());
    }

    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> pushControl(HashMap<String, String> req) {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 1;

    String target = req.get("target").toString();
    String deviceType = req.get("deviceType").toString();
    
    if ("all".equals(target)) {
      List<HashMap<String, String>> deviceInfoList = readOnlyMapper.getSerialListToFcmToken(req.get("token").toString(), req.get("userId").toString());
      for (HashMap<String, String> deviceInfo : deviceInfoList)
        if ("all".equals(deviceType) || deviceInfo.get("device_type").equals(deviceType)) 
          pushControlOneF(deviceInfo.get("serial_num"), req);

    } else {
      pushControlOneF(target, req);
    }

    res.put("result", resultCode);
    return res;
  }

  public LinkedHashMap<String, Object> pushControlView(HashMap<String, String> req) throws Exception {
    LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();
    int resultCode = 1;

    HashMap<String, Object> receiveData = new HashMap<String, Object>();

    String target = req.get("target").toString();
    String token = req.get("token").toString();

    Object receiveControlData = redisUtil.getRedisData("FLAG_".concat(token).concat("_").concat(target));

    if (!"NO_DATA".equals(receiveControlData.toString())) {
      ObjectMapper objMapper = new ObjectMapper();
      receiveData = objMapper.readValue(receiveControlData.toString(), HashMap.class);

    } else {
      HashMap<String, Object> timeMap = new LinkedHashMap<String, Object>();
      timeMap.put("startTime", "00:00");
      timeMap.put("endTime", "00:00");

      receiveData.put("pm10", 1);
      receiveData.put("pm25", 1);
      receiveData.put("co2", 1);
      receiveData.put("vocs", 1);
      receiveData.put("temp", 1);
      receiveData.put("humi", 1);
      receiveData.put("filterAlarm", 1);
      receiveData.put("timeFlag", timeMap);

    }

    res.put("data", receiveData);
    res.put("result", resultCode);

    return res;
  }
}
