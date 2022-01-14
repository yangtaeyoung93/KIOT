package com.airguard.model.dong;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Dong {

  String dcode;
  String dtype;
  String dname;
  String dfname;

  Double lat;
  Double lon;

  String mapType;
  String pm10Value;
  String pm25Value;
  String pm10Grade;
  String pm25Grade;
  String upDate;

  String rowNum;
  String regDate;
  PlatformDongPm pmInfo;
}
