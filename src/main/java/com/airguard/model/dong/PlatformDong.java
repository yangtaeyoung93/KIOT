/**
 * @FileName : PlatformDong.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 6. 11.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.dong;

import com.airguard.model.platform.TimeStampDto;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : PlatformDong.java
 * @Project : KIOT
 * @Date : 2020. 6. 11.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class PlatformDong {

  private TimeStampDto service;
  private PlatformDongPm data;
}
