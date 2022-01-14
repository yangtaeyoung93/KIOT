package com.airguard.model.app;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestModel {

  private String id;
  private String pw;
  private String device_type;
  private String device_code;
  private String device_imei;
  private String station_no;
  private String station_name;
  private String old_station_no;
  private String new_station_no;
  private String station_shared;
  private String main_category;
  private String sub_category;
  private String region;
  private String region_name;
  private String lon;
  private String lat;
  private String interest;
  private String serial;
  private String mode;
  private String date;
  private String group_no;
}
