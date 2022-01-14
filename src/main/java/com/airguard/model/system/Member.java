package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {

  int rowNum;

  int idx;

  // Column
  String userId;
  String userPw;
  String region;
  String regionName;
  String userName;
  String phoneNumber;
  String telephone;
  String userEmail;
  String userAddr;
  String stationShared;
  int loginCount;
  String loginIp;
  String loginDt;
  String createDt;
  String useYn;

  // OutColum
  String groupName;
  String memberIdx;
  String appDeviceIdx;
  String appDeviceCode;
  String appDeviceType;
  String appDeviceCreateDt;
  String pwCheck;

  // Request
  List<String> chArr;
  List<String> nameArr;

  // Response
  String restApiMessage;
  int resultCode;
  String checkIdx;
  String checkName;
}
