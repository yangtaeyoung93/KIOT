package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceType {

  int rowNum;

  int idx;

  // Column
  String deviceType;
  String deviceTypeName;
  String description;
  String createDt;
  String useYn;

  // Request
  List<String> chArr;

  // Response
  String restApiMessage;
}
