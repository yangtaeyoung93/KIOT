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
public class DashboardUserLoginDto {

  private String lastLoginDate;
  private Long loginCnt;
}
