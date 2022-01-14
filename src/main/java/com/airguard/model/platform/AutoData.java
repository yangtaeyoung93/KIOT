package com.airguard.model.platform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.ANY,
    setterVisibility = JsonAutoDetect.Visibility.ANY)
public class AutoData {
  String tm;
  String pm01_raw;
  String pm25_raw;
  String pm10_raw;
  String temp;
  String humi;
  String co2;
  String voc;
  String noise;
  String windd;
  String windd_max;
  String winds;
  String winds_max;
  String lux;
  String uv;
  String accx;
  String accx_max;
  String accy;
  String accy_max;
  String accz;
  String accz_max;
  String wbgt;
  String co;
  String hcho;
  String o3;
  String rn;
  String no2;
  String so2;
  String pm10;
  String pm25;
  String pm01;
  String cici_pm10;
  String cici_pm25;
  String cici_co2;
  String cici_voc;
  String cici_temp;
  String cici_humi;
  String cici_noise;
  String cici;
  String cmd_w;
  String cmd_p;
  String ai_mode_devices;

}
