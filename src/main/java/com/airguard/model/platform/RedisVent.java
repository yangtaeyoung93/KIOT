/**
 * @FileName : RedisVent.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 28.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import com.airguard.model.redis.RedisChannelDto;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : RedisVent.java
 * @Project : KIOT
 * @Date : 2020. 4. 28.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class RedisVent {

  private String model;
  private String serial;
  private Integer ai_mode;
  private RedisChannelDto channel;

}
