/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class ResultCollectionHisVo {

  private String timestamp;
  private String formatTimestamp;

  /*
   * 수집 데이터
   */
  private Double tm;

  private Double pm10;
  private Double pm25;
  private Double pm01;
  private Double pm10_raw;
  private Double pm25_raw;
  private Double pm01_raw;
  private Double pm10_ratio;
  private Double pm25_ratio;
  private Double pm10_offset;
  private Double pm25_offset;

  private Double temp;
  private Double humi;
  private Double co2;
  private Double voc;
  private Double noise;
  private Double lux;
  private Double uv;
  private Double wbgt;
  private Double co;
  private Double hcho;
  private Double o3;
  private Double rn;
  private Double no2;
  private Double so2;
  private Double visitor;

  private Double atm;
  private Double rain;
  private Double nh3;
  private Double h2s;
  private Double gps_lat;
  private Double gps_lon;

  private Double nox;
  private Double no;
  private Double tsp;

  private Double accx;
  private Double accx_max;
  private Double accy;
  private Double accy_max;
  private Double accz;
  private Double accz_max;

  private Double windd;
  private Double windd_max;
  private Double winds;
  private Double winds_max;

  private Double cici;
  private Double cici_pm10;
  private Double cici_pm25;
  private Double cici_co2;
  private Double cici_voc;
  private Double cici_temp;
  private Double cici_humi;
  private Double cici_noise;
  private Double ciai;

  private Double coci;
  private Double coci_pm10;
  private Double coci_pm25;
  private Double coci_co2;
  private Double coci_voc;
  private Double coci_temp;
  private Double coci_humi;
  private Double coci_noise;
  private Double coai;
  private Double rainfall;
  private Double day_rainfall;

  /*
   * 수집 데이터 (VENT)
   */
  private String reg_date;
  private String power;
  private String air_volume;
  private String filter_alarm;
  private String air_mode;
  private String auto_mode;
  private String exh_mode;
  private String fire_alarm;
  private String water_alarm;
  private String dev_stat;

  /*
   * 휴미컨 측정 데이터 (HUM)
   */
  private String hum_rtemp;
  private String hum_rhumi;
  private String hum_otemp;
  private String hum_ohumi;
  private String hum_co2;
  private String hum_pm10;
  private String hum_pm25;
  private String watt;
  private String hum_err_hex;
  private String hum_err_kor;

  /*
   * 연동 데이터 (IAQ - VENT)
   */
  private String cmd_w;
  private String cmd_p;
  private String cmd_m;
  private String ai_mode_devices;
}
