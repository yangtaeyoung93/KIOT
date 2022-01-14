package com.airguard.model.dong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirEqui {

  int rowNum;

  int idx;

  // Equi Data (국가 관측망)
  String aAirCode;
  String aAirName;
  String aAirCity;
  String aAirAddr;
  String aLocation;
  String aRegDate;

  Double aLat;
  Double aLon;

  Double dLat;
  Double dLon;
  String dcode;
  String dname;
  String dfname;
  String mapType;
  String pm10Value;
  String pm25Value;
  String pm10Grade;
  String pm25Grade;
  String upDate;
  String mapCode;
  String distance;

  // 참조 관측망
  String refPm10Value;
  String refPm25Value;
  String refPm10Grade;
  String refPm25Grade;
  String refUpDate;
  String refName;
  Double refLat;
  Double refLon;
}
