/**
 * @FileName : RedisVentDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.redis;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : RedisVentDto.java
 * @Project : KIOT
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class RedisVentDto {

  private String model;
  private String serial;
  private String req;
  private Integer aiMode;

}
