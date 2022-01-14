package com.airguard.model.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardReceiveCntDto {

  private String statType;
  private String deviceType;
  private int deviceCnt;
  private int deviceCntOk;
  private int deviceCntNok;
  private String statDate;
  private String regDate;

}
