package com.airguard.util;

public class CommonConstant {

  /*
   * ETC ==> APP
   * */
  public static final String NULL_DATA = "NA";
  public static final String SUCCESS = "SUCCESS";

  /*
   * AES256 암호화 Key
   * */
  public static final String AES256_KEY = "kiot2020!kiot2020!";

  /*
   * Kweather SMS Module Server
   * */
  public static final String SMS_SERVER_URL = "http://220.95.238.45:8003";
  public static final String SMS_SERVER_API_NAME = "/api/send/sms/message";

  /*
   * App, API
   */
  public static final String NULL_PARAM_EX_MSG = "There is no Form Data .";
  public static final String WRONG_PARAM_EX_MSG = "Please check Form Data .";
  public static final String NULL_TARGET_EX_MSG = "There is no Target .";
  public static final String SQL_EX_MSG = "SQL Exception .";
  public static final String SERVER_EX_MSG = "Server Exception .";
  public static final String RUNTIME_EX_MSG = "RunTime Exception .";
  public static final String DUPLICATE_EX_MSG = "duplicate entry Exception .";
  public static final String CODE_EX_MSG = "Please check the code .";
  public static final String COOKIE_EX_MSG = "Please check Cookie .";

  public static byte R_SUCC_CODE = 1;
  public static byte R_FAIL_CODE = 0;

  /*
   * 플랫폼 API
   */
  public static final String API_SERVER_HOST_AIR365_STAT_API = "https://kiotapi.kweather.co.kr/air365/get_stdata.api";
  public static final String API_SERVER_HOST_TOTAL = "http://kiotdpd.kweather.co.kr:30101";
  public static final String API_SERVER_HOST_DEVICE = "http://kiototsdb.kweather.co.kr:24242";
  public static final String API_SERVER_HOST_REDIS = "http://kiotigrt.kweather.co.kr:20001";

  public static final String REDIS_SET_ID = "server_kwkiotcluster-redis";
  public static final String REDIS_PUB_ID = "server_kwkiotcluster-redis-pub";
  public static final String MQTT_ID = "server_kwkiotcluster-mqtt-seq";

  public static final String PARAM_SENSOR_TYPE = "/kma-aq";
  public static final String PARAM_SENSOR_IAQ = "/kw-isk";
  public static final String PARAM_SENSOR_OAQ = "/kw-osk";
  public static final String PARAM_SENSOR_DOT = "/kw-osd";
  public static final String PARAM_SENSOR_VENT = "/kw-vsk";
  public static final String PARAM_SENSOR_CONNECT = "/kw-ivk";

  public static final String METRIC_TYPE_IAQ = "kw-iaq-sensor-kiot.";
  public static final String METRIC_TYPE_OAQ = "kw-oaq-sensor-kiot.";
  public static final String METRIC_TYPE_DOT = "kw-oaq-sensor-dot.";
  public static final String METRIC_TYPE_VENT = "kw-vent-sensor-kiot.";

  public static final String SEARCH_PATH_SENSOR = "/v1/groups";
  public static final String SEARCH_PATH_QUERY = "/api/query";
  public static final String SEARCH_PATH_DONG_SENSOR = "/v1/sensors";
  public static final String SEARCH_PATH_REDIS = "/v1/servers";

  public static final String API_KEY_CONNECT = "ingress-router/v1/memstore:kw/iaq/cmd/v1/";
  public static final String API_CHANNEL_CONNECT = "ingress-router/v1/memstore.put:kw/iaq/cmd/v1/";

  public static final String API_KEY_FOTA = "ingress-router/v1/memstore:kw/ctl/fota/v1/";
  public static final String API_CHANNEL_FOTA = "ingress-router/v1/memstore.put:kw/ctl/fota/v1/";

  public static final String T_U_ID_FORMAT = "udev-";
  public static final String T_G_ID_FORMAT = "gdev-";
  public static final String U_ID_FORMAT = "u-";
  public static final String G_ID_FORMAT = "g-";

  public static final String[] VENT_AI_CODE = {"A0", "A1"};
  public static final String[] VENT_STATUS_CODE = {"A0", "A1", "P0", "P1", "W1", "W2", "W3", "W4", "W5", "W6"};

  public static final String[] VENT_JNT_STATUS_CODE = {"P0", "P1", "W1", "W2", "W3", "W4", "W5", "W6"};
  public static final String[] VENT_TAES_STATUS_CODE = {"P0", "P1", "W1", "W2", "W3", "W4", "W5", "W6"};
  public static final String[] VENT_AHU_STATUS_CODE = {"A0", "A1", "W1", "W2", "W3", "W4"};
  public static final String[] VENT_KWG_STATUS_CODE = {"A0", "A1", "P0", "P1", "W1", "W2", "W3"};
  public static final String[] VENT_KESR_STATUS_CODE = {"P0", "P1", "W1", "W2", "W3", "W4", "W5", "W6"};
  public static final String[] VENT_KWV_AIC1_STATUS_CODE = {"A0", "A1", "P0", "P1", "W1", "W2", "W3"};
  public static final String[] VENT_HUM_STATUS_CODE = {"P0", "P1", "W1", "W2", "W3"};

  public static final String PLATFORM_API_TYPE_CONNECT = "CONNECT";
  public static final String PLATFORM_API_TYPE_FOTA_RE = "FOTA_RESET";
  public static final String PLATFORM_API_TYPE_FOTA_UP = "FOTA_UPGRADE";
  public static final String PLATFORM_API_TYPE_VENT_CONTROL = "VENT_CONTROL";

  public static final String PUBLISHER_USER = "USER";
  public static final String PUBLISHER_GROUP = "GROUP";

  /*
   * API Url 정의
   */
  public static final String URL_SYSTEM_ADMIN = "/system/admin";
  public static final String URL_SYSTEM_DEVICE = "/system/device";
  public static final String URL_SYSTEM_DEVICE_MODEL = "/system/device/model";
  public static final String URL_SYSTEM_DEVICE_TYPE = "/system/device/type";
  public static final String URL_SYSTEM_DEVICE_ATTRIBUTE = "/system/device/attribute";
  public static final String URL_SYSTEM_DEVICE_ELEMENTS = "/system/device/elements";
  public static final String URL_SYSTEM_FOTA = "/system/fota";
  public static final String URL_SYSTEM_GROUP = "/system/group";
  public static final String URL_SYSTEM_GROUP_DID = "/system/group/did";
  public static final String URL_SYSTEM_MEMBER = "/system/member";
  public static final String URL_SYSTEM_MEMBER_DEVICE = "/system/member/device";
  public static final String URL_SYSTEM_ADMIN_MENU = "/system/admin/menu";
  public static final String URL_SYSTEM_SPACE = "/system/space";

  public static final String URL_DASHBOARD = "/dashboard";
  public static final String URL_DATACENTER = "/datacenter";
  public static final String URL_DONG = "/dong";
  public static final String URL_COLLECTION = "/collection";

  public static final String URL_API_DASHBOARD = "/api/dashboard";
  public static final String URL_API_DATACENTER = "/api/datacenter";
  public static final String URL_API_DONG = "/api/dong";
  public static final String URL_API_COLLECTION = "/api/collection";

  public static final String URL_API_PLATFORM = "/api/platform";
  public static final String URL_API_CONTROL = "/api/control";
  public static final String URL_API_APP = "/api/app";
  public static final String URL_API_APP_AIR365 = "/api/air365";
  public static final String URL_API_APP_AIR365_V2 = "/api/air365/v2";

  /*
   * 유효성 검사
   */
  public static final String REG_PHONE = "^01(?:0|1|[6-9])[.-]?(?:\\d{3}|\\d{4})[.-]?(\\d{4})$";
  public static final String REG_EMAIL = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
  public static final String REG_NUMBER = "^[0-9]*$";
  public static final String REG_LAT_LON = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";

}
