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
public class ReportModel {

  @XmlElement(name = "result")
  private long result;

  @XmlElement(name = "error_code")
  private long errorCode;

  @XmlElement(name = "data_count")
  private int dataCount;

  @XmlElement(name = "data_dt")
  private String dataDt;

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

  @XmlElement(name = "dust")
  private String dust;

  @XmlElement(name = "dust2")
  private String dust2;

  @XmlElement(name = "vocs")
  private String vocs;

  @XmlElement(name = "co2")
  private String co2;
}
