/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import java.math.BigDecimal;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @FileName : AirMapOaqVo.java
 * @Project : KIOT
 * @Date : 2021. 3. 11.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
@ToString
public class AirMapOaqVo {

  private BigDecimal timeStamp;
  private String serial;
  private String stationName;
  private String lat;
  private String lon;
  private String dCode;
  private String deviceType;
  private String addr;
  private String addr2;
  private String etc;
  private Object sensor;

  private String mainImage;
  private String eastImage;
  private String westImage;
  private String southImage;
  private String northImage;

  private boolean receiveFlag;
}
