/**
 * @FileName : PlatformSensorHisDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 23.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : PlatformSensorHisDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 23.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class PlatformSensorHisDto {

  private String metric;
  private TagsVo tags;
}
