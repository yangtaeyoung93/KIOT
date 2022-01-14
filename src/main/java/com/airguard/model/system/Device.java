package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Device {

  int rowNum;

  int idx;

  // Column
  String deviceModelIdx;
  String serialNum;
  String productDt;
  String createDt;
  String testYn;
  String useYn;

  // OutColum
  String deviceModel;

  String deviceTypeIdx;
  String deviceType;
  String deviceTypeName;

  // Request
  List<String> chArr;
  List<String> nameArr;

  // Response
  String restApiMessage;
  int resultCode;
  String checkIdx;
  String checkName;
}
