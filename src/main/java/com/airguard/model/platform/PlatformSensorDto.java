package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : PlatformSensorDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class PlatformSensorDto {

  private TimeStampDto service;
  private SensorDataDto data;

}
