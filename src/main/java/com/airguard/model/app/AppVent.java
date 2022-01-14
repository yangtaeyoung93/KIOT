package com.airguard.model.app;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppVent {

  /*
   * Value Object
   */
  @XmlElement(name = "id")
  private String userId;

  @XmlElement(name = "station_no")
  private String stationNo;

  @XmlElement(name = "serial")
  private String ventSerial;

  @XmlElement(name = "mode")
  private String aiMode;

  /*
   * Data Transfer Object
   */
  private String memberIdx;
  private String iaqDeviceIdx;
  private String ventDeviceIdx;
  private String controlMode;
}
