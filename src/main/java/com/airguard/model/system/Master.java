package com.airguard.model.system;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Master {
    int rowNum;
    int idx;

    String masterId;
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

    List<Group> group = new ArrayList<>();


}
