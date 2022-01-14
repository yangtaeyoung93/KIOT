/**
 * @FileName : PlatformSensorDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.control;

import com.airguard.model.platform.TimeStampDto;

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
public class PlatformControlDto {

  private TimeStampDto service;
  private ControlDataDto data;

}
