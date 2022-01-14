package com.airguard.util;

import org.springframework.stereotype.Component;

@Component
abstract public class KweatherElementMessageManageUtil {

  public static final String DEVICE_TYPE_IAQ = "IAQ";
  public static final String DEVICE_TYPE_OAQ = "OAQ";

  public static final String ELEMENT_TYPE_PM10 = "pm10";
  public static final String ELEMENT_TYPE_PM25 = "pm25";
  public static final String ELEMENT_TYPE_CO2 = "co2";
  public static final String ELEMENT_TYPE_VOC = "voc";
  public static final String ELEMENT_TYPE_TEMP = "temp";
  public static final String ELEMENT_TYPE_HUMI = "humi";

  public static final String[] ELEMENT_TYPE_LIST = {
    "pm10", "pm25", "co2", "voc", "noise", "temp", "humi"  
  };

  public static final String[] ETC_ACTION = {
      "NA", "NA", "NA", "NA", "NA", "NA",
    };

  public static final String[] CI_ACTION = {
      "실내공기가 매우 쾌적하게 유지되고 있습니다.", 
      "실내공기가 대체로 쾌적하게 유지되고 있습니다.", 
      "실내공기의 지수가 낮아 어린이나 노약자가 불쾌감을 느낄 수 있습니다.",
      "실내공기의 지수가 낮아 누구나 불쾌감을 느낄 수 있습니다."
    };

  public static final String[] PM10_ACTION = {
      "미세먼지로부터 안전한 상태가 유지되고 있습니다.", 
      "미세먼지로 인한 불편함이 없습니다.", 
      "실내 미세먼지 농도가 높아 어린이나 노약자는 불편함을 느낄 수 있습니다. 공기청정기를 작동시키거나 창문을 열어 환기하시기 바랍니다.", 
      "실내 미세먼지 농도가 매우 높아 어린이, 노약자 및 호흡기 질환자의 건강에 유해한 수준입니다. 공기청정기를 작동시키거나 창문을 열어 환기시키고 실내 미세먼지 농도를 낮추기 위한 조치가 필요합니다."
    };
  public static final String[] PM25_ACTION = {
      "미세먼지로부터 안전한 상태가 유지되고 있습니다.", 
      "미세먼지로 인한 불편함이 없습니다.", 
      "실내 미세먼지 농도가 높아 어린이나 노약자는 불편함을 느낄 수 있습니다. 공기청정기를 작동시키거나 창문을 열어 환기하시기 바랍니다.", 
      "실내 미세먼지 농도가 매우 높아 어린이, 노약자 및 호흡기 질환자의 건강에 유해한 수준입니다. 공기청정기를 작동시키거나 창문을 열어 환기시키고 실내 미세먼지 농도를 낮추기 위한 조치가 필요합니다."
    };

  public static final String[] CO2_ACTION = {
      "CO2로부터 안전한 상태가 유지되고 있습니다.", 
      "CO2로 인한 불편함이 없습니다.", 
      "실내 CO2 농도가 높아 업무효율과 학습능력이 떨어질 수 있으니 창문을 열어 환기시켜 주세요.", 
      "실내 CO2 농도가 매우 높아 업무효율과 학습능력이 떨어질 수 있으니 즉시 창문을 열어 환기시켜 지속적으로 관리해 주세요."
    };

  public static final String[] VOC_ACTION = {
      "VOCs로부터 매우 안전합니다.", 
      "VOCs로 인한 불편함이 없습니다.", 
      "실내 VOCs 농도가 높으니 창문을 열어 환기를 시키거나 공기청정기를 작동시켜 주세요.", 
      "실내 VOCs 농도가 매우 높으니 창문을 열어 환기를 시키거나 공기청정기를 작동시켜 주세요. 이 단계가 장시간 지속되면 전문가에게 상담을 요청하세요."
    };

  public static final String[] NOISE_ACTION = {
      "소음단계가 쾌적한 수준으로 유지되고 있습니다.", 
      "소음단계가 일상생활에 영향을 받지 않을 정도로 유지되고 있습니다.", 
      "소음공해가 발생되고 있습니다. 소음원인을 확인하고 원인을 제거하기 바랍니다.", 
      "소음공해가 심각합니다. 즉각적인 조치가 필요하니 소음원인을 제거하거나, 필요시 귀마개를 착용해 주세요."
    };

  public static final String[] TEMP_ACTION = {
      "쾌적한 실내온도가 유지되고 있습니다.", 
      "적정 실내온도가 유지되고 있습니다.", 
      "실내온도 조절이 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.", 
      "실내온도 조절이 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.", 
      "실내온도 조절이 즉시 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요.",
      "실내온도 조절이 즉시 필요합니다. 냉난방 장치를 작동시켜 적정온도를 유지시켜 주세요."
    };

  public static final String[] HUMI_ACTION = {
      "실내습도가 쾌적하게 유지되고 있습니다.", 
      "적정 실내습도가 유지되고 있습니다.", 
      "습도로 인해 불쾌감을 느낄 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요.", 
      "습도로 인해 불쾌감을 느낄 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요.", 
      "습도로 인해 건강이 위협받을 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요.",
      "습도로 인해 건강이 위협받을 수 있으니, 가습/제습 장치를 작동시켜 습도를 조절해 주세요."
    };

  public static int elementLevel(String deviceType, String element, Double val) {
    int level;

    elementLoop : switch (element) {
      case ELEMENT_TYPE_TEMP:
        deviceTypeLoop : switch (deviceType) {
          case DEVICE_TYPE_IAQ:
            if (18 < val && val <= 21) {
              level = 1;
            } else if (21 < val && val <= 24) {
              level = 1;
            } else if (16 < val && val <= 18) {
              level = 2;
            } else if (24 < val && val <= 27) {
              level = 2;
            } else if (27 < val && val <= 30) {
              level = 3;
            } else if (14 < val && val <= 16) {
              level = 4;
            } else if (30 < val && val <= 35) {
              level = 5;
            } else if (0 <= val && val <= 14) {
              level = 6;
            } else {
              level = 0;
            }

            break deviceTypeLoop;
          default:
            if (9 < val && val < 13.5) {
              level = 1;
            } else if (13.5 < val && val <= 18) {
              level = 1;
            } else if (0 < val && val <= 9) {
              level = 2;
            } else if (18 < val && val <= 25) {
              level = 2;
            } else if (25 < val && val <= 33) {
              level = 3;
            } else if (-5 < val && val <= -0.1) {
              level = 4;
            } else if (33 < val && val <= 50) {
              level = 5;
            } else if (-30 < val && val <= -5) {
              level = 6;
            } else {
              level = 0;
            }

            break deviceTypeLoop;
        }

      break elementLoop;
      case ELEMENT_TYPE_HUMI:
      deviceTypeLoop : switch (deviceType) {
          case DEVICE_TYPE_IAQ:
            if (40 < val && val <= 50) {
              level = 1;
            } else if (50 < val && val <= 60) {
              level = 1;
            } else if (35 < val && val <= 40) {
              level = 2;
            } else if (60 < val && val <= 75) {
              level = 2;
            } else if (75 < val && val <= 90) {
              level = 3;
            } else if (20 < val && val <= 35) {
              level = 4;
            } else if (90 < val && val <= 100) {
              level = 5;
            } else if (0 <= val && val <= 20) {
              level = 6;
            } else {
              level = 0;
            }

            break deviceTypeLoop;
          default:
            if (50 < val && val <= 60) {
              level = 1;
            } else if (60 < val && val <= 70) {
              level = 1;
            } else if (40 < val && val <= 50) {
              level = 2;
            } else if (70 < val && val <= 80) {
              level = 2;
            } else if (80 < val && val <= 90) {
              level = 3;
            } else if (30 < val && val <= 40) {
              level = 4;
            } else if (90 < val && val <= 100) {
              level = 5;
            } else if (0 < val && val <= 30) {
              level = 6;
            } else {
              level = 0;
            }

            break deviceTypeLoop;
        }
        break elementLoop;
        default:
          level = 0;
    }

    return level;
  }

  public static int elementLevel(Double val) {
    int level;

    if (0 < val && val <= 50)
      level = 4;
    else if (50 < val && val <= 80)
      level = 3;
    else if (80 < val && val <= 90)
      level = 2;
    else if (90 < val && val <= 100)
      level = 1;
    else
      level = 0;

    return level;
  }

  public static String setElementLevelKorName(String element, String level) {
    String korName = "";

    if (ELEMENT_TYPE_TEMP.equals(element)) {
      switch (level) {
        case "1":
          korName = "쾌적";
          break;
        case "2":
          korName = "보통";
          break;
        case "3":
          korName = "더움";
          break;
        case "4":
          korName = "추움";
          break;
        case "5":
          korName = "매우 더움";
          break;
        case "6":
          korName = "매우 추움";
          break;
      }
    } else if (ELEMENT_TYPE_HUMI.equals(element)) {
      switch (level) {
        case "1":
          korName = "쾌적";
          break;
        case "2":
          korName = "보통";
          break;
        case "3":
          korName = "습함";
          break;
        case "4":
          korName = "건조";
          break;
        case "5":
          korName = "매우 습함";
          break;
        case "6":
          korName = "매우 건조";
          break;
      }
    } else {
      switch (level) {
        case "1":
          korName = "좋음";
          break;
        case "2":
          korName = "보통";
          break;
        case "3":
          korName = "나쁨";
          break;
        case "4":
          korName = "매우 나쁨";
          break;
      }
    }

    return korName;
  }
}
