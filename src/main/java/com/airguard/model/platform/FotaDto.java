/**
 * @FileName : FotaDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 28.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : FotaDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 28.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class FotaDto {

  private String serial;
  private Long update;
  private Integer reset;
  private Integer fota;
  private String firmversion;

}
