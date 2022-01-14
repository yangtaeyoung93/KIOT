/**
 * @FileName : RedisDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.redis;

import java.util.List;

import com.airguard.model.platform.RedisVent;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : RedisDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class RedisDto {

  private String serial;
  private Integer set_temp;
  private String ref_dcode;
  private String ref_oaq;
  private Long update;
  private Integer ai_mode_devices;
  private List<RedisVent> vent;

}
