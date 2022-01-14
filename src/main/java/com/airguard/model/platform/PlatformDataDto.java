/**
 * @FileName : PlatformDataSensorDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 25.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : PlatformDataSensorDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 25.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class PlatformDataDto {

  private TimeStampDto service;
  private PlatformConnectDataDto data;

}
