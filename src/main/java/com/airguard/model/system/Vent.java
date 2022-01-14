package com.airguard.model.system;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vent {

  // Column
  String memberIdx;
  String iaqDeviceIdx;
  String ventDeviceIdx;
  String aiMode;
  String createDt;

  // OutColum
  String serialNum;
  String deviceTypeIdx;
  String deviceType;
  String deviceModel;
  String deviceTypeName;

  // Response
  String restApiMessage;
}
