package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Group {

  int rowNum;

  int idx;

  // Column
  String groupId;
  String groupPw;
  String groupName;
  String groupEmail;
  String groupCompanyName;
  String groupDepartName;
  String groupPhoneNumber;
  String groupTelephone;
  String groupCustomUrl;
  String createDt;
  String useYn;

  // OutColum
  String groupIdx;
  String memberIdx;
  String userId;
  String gubun;
  String memberCnt;
  String pwCheck;

  // Request
  List<String> chArr;
  List<String> memberIdxs;
  List<String> nameArr;

  // Response
  String restApiMessage;
  int resultCode;
  String checkIdx;
  String checkName;
}
