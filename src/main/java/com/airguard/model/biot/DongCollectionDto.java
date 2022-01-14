/**
 * @FileName : DongCollectionDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 5. 14.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.biot;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : DongCollectionDto.java
 * @Project : KIOT
 * @Date : 2020. 5. 14.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class DongCollectionDto {

  private String dcode;
  private int dtype;
  private String dname;
  private Long upDate;

  private int pm10Value;
  private int pm25Value;

  private String lon;
  private String lat;

  private String sdcode;
  private String sggcode;

}
