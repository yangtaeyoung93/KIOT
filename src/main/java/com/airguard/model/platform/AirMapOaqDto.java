package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : AirMapOaqDto.java
 * @Project : KIOT
 * @Date : 2021. 3. 12.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
public class AirMapOaqDto {

  private TimeStampDto service;
  private SensorAirOaqDto data;

}
