/**
 * @FileName : HisVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 23.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : HisVo.java
 * @Project : KIOT
 * @Date : 2020. 4. 23.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class HisVo {

  private String time;
  private String tm;

  private String pm10;
  private String pm25;
  private String pm01;
  private String pm01_raw;
  private String pm25_raw;
  private String pm10_raw;

  private String temp;
  private String humi;
  private String co2;
  private String voc;
  private String noise;
  private String lux;
  private String uv;
  private String wbgt;
  private String co;
  private String hcho;
  private String o3;
  private String rn;
  private String no2;
  private String so2;

  private String accx;
  private String accx_max;
  private String accy;
  private String accy_max;
  private String accz;
  private String accz_max;

  private String windd;
  private String windd_max;
  private String winds;
  private String winds_max;

  private String cici;
  private String cici_pm10;
  private String cici_pm25;
  private String cici_co2;
  private String cici_voc;
  private String cici_temp;
  private String cici_humi;
  private String cici_noise;

  private String coci;
  private String coci_pm10;
  private String coci_pm25;
  private String coci_co2;
  private String coci_voc;
  private String coci_temp;
  private String coci_humi;
  private String coci_noise;
}
