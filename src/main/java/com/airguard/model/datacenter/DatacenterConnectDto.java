package com.airguard.model.datacenter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatacenterConnectDto {

  private String memberIdx;
  private String devcieIdx;
  private String iaqSerialNum;
  private String spaceIdx;
  private String groupId;
  private String groupName;
  private String spaceName;
  private String parentSpaceName;
  private String stationName;
  private String testYn;
  private String userId;
  private String serialNum;
  private String ventModel;
  private String productDt;
  private String ventCnt;
  private String ventsStr;
  private String aiMode;
  private String deviceType;
  private String lon;
  private String lat;
}
