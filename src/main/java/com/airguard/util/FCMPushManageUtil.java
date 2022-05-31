package com.airguard.util;

import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.airguard.exception.SQLException;

@Component
public class FCMPushManageUtil {

  static final Logger logger = LoggerFactory.getLogger(FCMPushManageUtil.class);

  @Autowired
  private RedisManageUtil redisUtil;

  public void pushTokenDataGenerateF(String userId, String fcmToken) throws JSONException {
    JSONObject valueObj = new JSONObject();
    valueObj.put("user_id", userId);
    valueObj.put("fcm_token", fcmToken);

    redisUtil.setRedisData("TOKEN_".concat(userId).concat("_").concat(fcmToken), valueObj.toString());
  }

  public void pushTokenDataClearF(String fcmToken) {
    for (String key : redisUtil.getRedisKeyList("TOKEN_".concat("*_").concat(fcmToken))) {
      redisUtil.delRedisData(key);
    }
  }

  public void pushControlOneF(String target, HashMap<String, String> req,String deviceInfo) throws Exception {
    JSONObject elObj = new JSONObject();
    JSONObject timeObj = new JSONObject();

    String userId = "group".equals(req.get("userType")) ? "g-".concat(req.get("userId")) : req.get("userId");

    try {

      elObj.put("pm10", Integer.parseInt(req.get("pm10")));
      elObj.put("pm25", Integer.parseInt(req.get("pm25")));
      elObj.put("co2", Integer.parseInt(req.get("co2")));
      elObj.put("vocs", Integer.parseInt(req.get("vocs")));
      elObj.put("temp", Integer.parseInt(req.get("temp")));
      elObj.put("humi", Integer.parseInt(req.get("humi")));
      if(!deviceInfo.equals("OAQ")){
        elObj.put("filterAlarm", Integer.parseInt(req.get("filterAlarm")));
      }
      timeObj.put("startTime", req.get("startTime"));
      timeObj.put("endTime", req.get("endTime"));
      elObj.put("timeFlag", timeObj);

      String redisDataKey = "FLAG_".concat(userId).concat("_").concat(req.get("token")).concat("_").concat(target);
      if (!redisUtil.setRedisData(redisDataKey, elObj.toString()))
        throw new SQLException(SQLException.REDIS_SQL_EXCEPTION);
    } catch (Exception e) {
      throw new SQLException(SQLException.REDIS_SQL_EXCEPTION);
    }
  }

  public void pushControlDataGenerateF(String token, String userType, String userId, List<String> serials) throws Exception {
    HashMap<String, String> pushControlReqMp = new HashMap<>();

    pushControlReqMp.put("userType", userType);
    pushControlReqMp.put("userId", userId);
    pushControlReqMp.put("token", token);
    pushControlReqMp.put("pm10", "0");
    pushControlReqMp.put("pm25", "0");
    pushControlReqMp.put("co2", "0");
    pushControlReqMp.put("vocs", "0");
    pushControlReqMp.put("temp", "0");
    pushControlReqMp.put("humi", "0");
    pushControlReqMp.put("filterAlarm", "0");
    pushControlReqMp.put("startTime", "08:00");
    pushControlReqMp.put("endTime", "20:00");

    for (String serial : serials) {
      if ("NO_DATA".equals(redisUtil.getRedisData("FLAG_".concat("group".equals(userType) ? "g-".concat(userId) : userId)
          .concat("_").concat(token).concat("_").concat(serial)).toString())) 
        pushControlOneF(serial, pushControlReqMp,"");
    }

  }
}
