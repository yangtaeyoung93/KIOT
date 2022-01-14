package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDevice {

  int rowNum;

  int idx;

  // Column
  String memberIdx;
  String deviceIdx;
  String spaceIdx;
  String lat;
  String lon;
  String equipDt;
  String equipName;
  String equipAddr;
  String equipAddr2;
  String etc;
  String departName;
  String departPhoneNumber;
  String salesName;
  String setTemp;
  String createDt;
  String useYn;
  String dcode;
  String airMapYn;
  String relatedDevice;

  // OutColum
  String userId;
  String serialNum;
  String stationName;
  String userName;
  String phoneNumber;
  String userEmail;
  String userAddr;
  String space;
  String stationShared;
  String deviceTypeIdx;
  String deviceType;
  String deviceModel;
  String deviceTypeName;
  String deviceModelIdx;
  String deviceModelName;
  String testYn;
  String spaceName;
  String deviceCount;
  String iaqDeviceCount;
  String oaqDeviceCount;
  String dotDeviceCount;
  String ventDeviceCount;
  String groupSerialNum;
  String fileFlag;
  String fileName;
  String fileCnt;
  String mainImageFlag;
  String mainImageName;
  String mainImageFilePath;
  String eastImageFlag;
  String eastImageName;
  String eastImageFilePath;
  String westImageFlag;
  String westImageName;
  String westImageFilePath;
  String southImageFlag;
  String southImageName;
  String southImageFilePath;
  String northImageFlag;
  String northImageName;
  String northImageFilePath;

  String relatedOaq;

  // Request
  List<String> chArr;
  List<String> deviceIdxs;
  List<String> ventDeviceIdxs;

  // Response
  String restApiMessage;
}
