package com.airguard.model.system;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Master {
    int rowNum;
    int idx;

    String masterId;
    String masterIdx;
    String masterPw;
    String masterName;
    String masterEmail;
    String masterPhoneNumber;
    String masterCompanyName;
    String groupDepartName;
    String createDt;
    String useYn;
    String loginIp;
    String loginDt;
    String air365Yn;

    String groupIdx;
    String memberIdx;
    String groupId;
    String groupName;
    String gubun;
    String groupCnt;
    String pwCheck;

    // Request
    List<String> chArr;
    List<String> groupIdxs;
    List<String> nameArr;

    // Response
    String restApiMessage;
    int resultCode;
    String checkIdx;
    String checkName;

}
