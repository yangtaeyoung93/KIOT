package com.airguard.model.app;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppStation {

  /*
   * Value Object
   */
  @XmlElement(name = "id")
  private String userId;

  @XmlElement(name = "station_no")
  private String stationNo;

  @XmlElement(name = "station_name")
  private String stationName;

  @XmlElement(name = "old_station_no")
  private String oldStationNo;

  @XmlElement(name = "new_station_no")
  private String newStationNo;

  @XmlElement(name = "station_shared")
  private String stationShared;

  @XmlElement(name = "main_category")
  private String mainCategory;

  @XmlElement(name = "sub_category")
  private String subCategory;

  @XmlElement(name = "interest")
  private String interest;

  @XmlElement(name = "region")
  private String region;

  @XmlElement(name = "region_name")
  private String regionName;

  @XmlElement(name = "lat")
  private String lat;

  @XmlElement(name = "lon")
  private String lon;

  /*
   * Data Transfer Object
   */
  private String memberIdx;
  private String deviceIdx;
  private String stationType;
  private String oldDeviceIdx;
  private String newDeviceIdx;
  private String spaceIdx;
  private String spaceName;
  private String equipDt;
  private String equipName;
  private String equipAddr;
  private String equipAddr2;
  private String createDt;

  private long count;
  private String mainCategoryIdxs;
  private String subCategoryIdxs;
  private String subCategoryNames;

  private String highSpaceIdx;
  private String ventStations;
}
