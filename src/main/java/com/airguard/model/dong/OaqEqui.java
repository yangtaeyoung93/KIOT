package com.airguard.model.dong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OaqEqui {

  String equiInfoKey;
  String regDate;

  // Equi Data (케이웨더 OAQ 관측망)
  String oSerial;
  String oUserId;
  String oUserName;

  Double oLat;
  Double oLon;

  String oLocation;
  String oRegDate;

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
