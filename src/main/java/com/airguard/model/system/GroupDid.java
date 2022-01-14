package com.airguard.model.system;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDid {

  int rowNum;

  int idx;

  // Column
  String groupIdx;
  String groupDidIdx;
  String memberIdx;
  String groupId;
  String didName;
  String didCode;
  String createDt;
  String useYn;

  // OutColum
  String strIdx;
  String groupName;
  String didMemberCnt;
  String didCnt;
  String userId;
  String groupDidName;
  String groupDidCode;

  // Request
  List<String> chArr;
  List<String> members;
  List<String> groupIdxs;

  // Response
  String restApiMessage;
}
