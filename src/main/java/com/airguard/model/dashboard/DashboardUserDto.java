/**
 * @FileName : DashboardDto.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 5. 1.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.dashboard;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : DashboardDto.java
 * @Project : KIOT
 * @Date : 2020. 5. 1.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
public class DashboardUserDto {

  private Long memberCnt;
  private Long groupCnt;
  private Long groupMemberCnt;
  private Long didCnt;

  private Long conIaqCnt;
  private Long conOaqCnt;
  private Long conDotCnt;
  private Long conVentCnt;
  private Long allIaqCnt;
  private Long allOaqCnt;
  private Long allDotCnt;
  private Long allVentCnt;

}
