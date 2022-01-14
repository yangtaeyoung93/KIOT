/**
 * @FileName : PlatformReqData.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 28.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : PlatformReqData.java
 * @Project : KIOT
 * @Date : 2020. 4. 28.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class PlatformReqData {

  private List<String> channel;
  private String data;
  private String key;
  private String value;

}
