package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Space {

  int rowNum;

  int idx;

  // Column
  int deviceTypeIdx;
  int parentSpaceIdx;
  int spaceLevel;
  String spaceName;
  String spaceOrder;
  String createDt;
  String useYn;

  // OutColum
  String deviceType;
  int parentSpaceLevel;
  String parentSpaceName;
  String parentDeviceType;

  // Request
  List<String> chArr;

  // Response
  String restApiMessage;
}
