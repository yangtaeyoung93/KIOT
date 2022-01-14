/**
 * @FileName : SensorDataDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.control;

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
public class ControlDataDto {

  /*
   * 관제 데이터
   */
  private String serial;
  private String servertime;
  private String firmversion;
  private String starttime;
  private String imei;
  private String ctn;
  private String sensorstatus;
  private String pversion;
}
