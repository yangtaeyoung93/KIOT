package com.airguard.model.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "kweather")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResStationModel {

  @XmlElement(name = "result")
  private long result;

  @XmlElement(name = "error_code")
  private long errorCode;

  @XmlElement(name = "station_no")
  private String stationNo;

  @XmlElement(name = "station_type")
  private String stationType;

  @XmlElement(name = "main_category")
  private String mainCategory;

  @XmlElement(name = "sub_category")
  private String subCategory;

  @XmlElement(name = "interest")
  private long interest;

  @XmlElement(name = "ITEM")
  private StationItemModel item;
}
