package com.airguard.model.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "object")
public class AppGroupDid {

  /*
   * Data Transfer Object
   */
  @XmlElement(name = "station_no")
  private String stationNo;
  @XmlElement(name = "station_name")
  private String stationName;
  @XmlElement(name = "region_no")
  private String regionNo;
  @XmlElement(name = "region_name")
  private String regionName;

  public AppGroupDid(String stationNo, String stationName, String regionNo, String regionName) {
    this.stationNo = stationNo;
    this.stationName = stationName;
    this.regionNo = regionNo;
    this.regionName = regionName;
  }
}
