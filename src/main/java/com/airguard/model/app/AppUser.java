package com.airguard.model.app;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUser {

  /*
   * Value Object
   */
  @XmlElement(name = "id")
  private String userId;

  @XmlElement(name = "pw")
  private String userPw;

  @XmlElement(name = "station_no")
  private String stationNo;

  @XmlElement(name = "station_name")
  private String stationName;

  @XmlElement(name = "lat")
  private String lat;

  @XmlElement(name = "lon")
  private String lon;

  @XmlElement(name = "region")
  private String region;

  @XmlElement(name = "region_name")
  private String regionName;

  @XmlElement(name = "station_shared")
  private String stationShared;

  @XmlElement(name = "device_type")
  private String appDeviceType;

  @XmlElement(name = "device_code")
  private String appDeviceCode;

  @XmlElement(name = "device_imei")
  private String appDeviceImei;

  /*
   * Data Transfer Object
   */
  private String idx;
  private String memberIdx;
  private String deviceIdx;
  private String deviceType;
}
