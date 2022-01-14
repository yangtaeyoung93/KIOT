/**
 * @FileName : FileVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 6. 19.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.system;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : FileVo.java
 * @Project : KIOT
 * @Date : 2020. 6. 19.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class FileVo {

  private String idx;
  private String deviceIdx;
  private String serverFilePath;
  private String serverFileName;
  private String clientFileName;
  private String fileExt;
  private String regDate;
  private String modifyDate;
  private String fileType;
  private String serialNum;
}
