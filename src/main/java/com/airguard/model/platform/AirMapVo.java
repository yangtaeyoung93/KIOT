/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @FileName : AirMapVo.java
 * @Project : KIOT
 * @Date : 2021. 3. 17.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
@ToString
public class AirMapVo {

  private AirMapOaqDto kWeather;
  private AirMapKorDto airKorea;
}
