/**
 * @FileName : SensorDataDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : SensorDataDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
/**
 * 2022.1.26 UnrecognizedPropertyException으로 인해 JsonIgnoreProperties 추가
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorDataDto {

  /*
   * 수집 데이터
   */
  private String tm;

  private String pm10;
  private String pm25;
  private String pm01;
  private String pm01_raw;
  private String pm25_raw;
  private String pm10_raw;
  private String pm10_grade;
  private String pm25_grade;

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

  private String pm10_offset;
  private String pm25_offset;
  private String pm10_ratio;
  private String pm25_ratio;

  private String ai_mode_devices;

  private String ciai;
  private String coai;

  private String visitor;
  private String rainfall;

  private String atm;
  private String rain;
  private String nh3;
  private String h2s;
  private String gps_lat;
  private String gps_lon;

  private String nox;
  private String no;
  private String tsp;

  /*
   * 수집 데이터 (VENT)
   */
  private String reg_date;
  private String power;
  private String air_volume;
  private String exh_mode;
  private String auto_mode;
  private String filter_alarm;
  private String air_mode;
  private String cmd_w;
  private String cmd_p;
  private String cmd_m;
  private String fire_alarm;
  private String water_alarm;
  private String dev_stat;
}
