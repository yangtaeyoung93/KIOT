package com.airguard.model.dong;

import com.airguard.model.platform.SensorDataDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirGeo {

  private String aAirCode;
  private String aAirName;
  private String aSerial;
  private String mapType;
  private String mapCode;

  private String serial;
  private String upDate;
  private String distance;

  private Double lat;
  private Double lon;

  private String pm10Grade;
  private String pm25Grade;
  private String pm10Value;
  private String pm25Value;

  private Double pm10Idw;
  private Double pm25Idw;
  private Double pm10Avg3;
  private Double pm25Avg3;
  private Double pm10Ratio;
  private Double pm25Ratio;
  private Integer pm10Offset;
  private Integer pm25Offset;
  private Double pm10RatioRaw;
  private Double pm25RatioRaw;

  private String useYn;
}
