package com.airguard.model.dong;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @FileName : AirGeoInfo.java
 * @Project : KIOT_ADMIN
 * @Date : 2021. 3. 12.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
@ToString
public class AirGeoInfo {

  private String timeStamp;
  private String aAirName;
  private Double lat;
  private Double lon;
  private String aAirCode;
  private String aAirCity;
  private String aAirAddr;
  private String dCode;
  private String useYn;
  private SensorAirKorDto sensor;

  private boolean receiveFlag;
}
