package com.airguard.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotifiTimeCheckUtil {

  static final Logger logger = LoggerFactory.getLogger(NotifiTimeCheckUtil.class);

  public static Map<String, Object> parsingTimeData(String dataStr) {
    Map<String, Object> parseData = new HashMap<String, Object>();
    boolean result;
    String startTime = "";
    String endTime = "";

    try {

      JSONObject jObj = new JSONObject(dataStr);
      startTime = jObj.get("startTime").toString();
      endTime = jObj.get("endTime").toString();
      result = true;

    } catch (JSONException e) {
      result = false;
      logger.error("==== parsingTimeData :: JSON Exception ====");

    }

    parseData.put("startTime", startTime);
    parseData.put("endTime", endTime);
    parseData.put("reuslt", result);

    return parseData;
  }

  public static boolean isNotifiTimeRangeCheck(String dataStr) {
    LocalTime currTime = LocalTime.now();
    Map<String, Object> timeData = parsingTimeData(dataStr);

    String from = timeData.get("startTime").toString();
    String to = "24:00".equals(timeData.get("endTime").toString()) ? "23:59" : timeData.get("endTime").toString();

    logger.info("local :: {}, start :: {}:00, end :: {}:00", currTime, timeData.get("startTime"), timeData.get("endTime"));

    return (!from.equals(to)) && (currTime.isAfter(LocalTime.parse(new StringBuilder(from).append(":").append("00").toString()))) && 
        (currTime.isBefore(LocalTime.parse(new StringBuilder(to).append(":").append("00").toString())));
  }
}
