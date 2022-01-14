package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceModel {

  int rowNum;

  int idx;

  // Column
  int deviceTypeIdx;
  String deviceModel;
  String description;
  String imageFile;
  String createDt;
  String useYn;

  // OutColum
  String deviceType;
  List<String> deviceValue;
  List<String> attributeIdx;
  List<String> elementIdx;

  // Request
  List<String> chArr;
  List<String> nameArr;

  // Response
  String restApiMessage;
  int resultCode;
  String checkIdx;
  String checkName;
}
