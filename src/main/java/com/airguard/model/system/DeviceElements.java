package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceElements {

  int rowNum;

  int idx;

  // Column
  String korName;
  String engName;
  String viewName;
  String elementUnit;
  String elementConvert;
  String validDigits;
  String dataMin;
  String dataMax;

  // OutColum
  String deviceModelIdx;
  String deviceTypeIdx;
  String elementIdx;
  String gubun;

  // Request
  List<String> chArr;

  // Response
  String restApiMessage;
}
