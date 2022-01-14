/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.control;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class ResultControlVo {

  private String serial;
  private ControlDataDto control;
  private String timestamp;
  private String testYn;
  private String userId;
  private String stationName;
}
