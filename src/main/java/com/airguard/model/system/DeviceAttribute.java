package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAttribute {

  int rowNum;

  int idx;

  // Column
  String attributeName;
  String attributeCode;
  String attributeValue;
  String inputType;

  // OutColum
  String deviceModelIdx;
  String deviceValue;
  String attributeIdx;
  String gubun;

  // Request
  List<String> chArr;

  // Response
  String restApiMessage;
}
