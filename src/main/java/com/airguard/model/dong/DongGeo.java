package com.airguard.model.dong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DongGeo {
  String dcode;
  String dtype;
  String sdcode;
  String sggcode;
  String dname;

  Double lat;
  Double lon;

  String pm10Value;
  String pm25Value;
  String pm10Grade;
  String pm25Grade;
  String upDate;
}
