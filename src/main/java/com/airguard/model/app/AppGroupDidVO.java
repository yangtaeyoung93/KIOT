package com.airguard.model.app;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "object")
public class AppGroupDidVO {

  /*
   * Data Transfer Object
   */
  @XmlElement(name = "station_no")
  private String stationNo;
  @XmlElement(name = "station_name")
  private String stationName;
  @XmlElement(name = "lat")
  private String lat;
  @XmlElement(name = "lon")
  private String lon;

}
