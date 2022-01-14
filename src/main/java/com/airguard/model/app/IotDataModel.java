package com.airguard.model.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "kweather_iot")
@XmlAccessorType(XmlAccessType.FIELD)
public class IotDataModel {

  @XmlElement(name = "result")
  private long result;

  @XmlElement(name = "date")
  private String date;

  @XmlElement(name = "temp")
  private String temp;

  @XmlElement(name = "humi")
  private String humi;

  @XmlElement(name = "pm10")
  private String pm10;

  @XmlElement(name = "pm25")
  private String pm25;

  @XmlElement(name = "noise")
  private String noise;

  @XmlElement(name = "co2")
  private String co2;

  @XmlElement(name = "vocs")
  private String vocs;

  @XmlElement(name = "co")
  private String co;

  @XmlElement(name = "hcho")
  private String hcho;

  @XmlElement(name = "rn")
  private String rn;

  @XmlElement(name = "total")
  private String study_idx;

  @XmlElement(name = "kweather")
  private String kweather;

  @XmlElement(name = "main_category")
  private String mainCategory;

  @XmlElement(name = "sub_category")
  private String subCategory;

  @XmlElement(name = "station_shared")
  private String stationShared;

}
