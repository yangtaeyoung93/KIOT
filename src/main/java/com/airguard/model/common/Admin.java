package com.airguard.model.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin {

  int rowNum;

  int idx;

  // Column
  String userId;
  String userPw;
  String userName;
  String loginIp;
  String loginDt;
  String createDt;
  String useYn;

  // OutColum
  String menuCnt;
  String totalMenuCnt;

  // Request
  List<String> chArr;

  // Response
  String restApiMessage;
}
