package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Menu {

  int rowNum;

  int idx;

  // Column
  String menuName;
  String highRankMenu;
  String menuLevel;
  String menuUrl;
  String menuTag;
  String menuOrder;
  String menuEng;

  // OutColum
  String highRankMenuName;
  String adminIdx;
  String menuIdx;
  int menuAuth;
  String fullMenuName;
  String authFlag;

  // Request
  List<String> chArr;

  // Response
  String restApiMessage;
}
