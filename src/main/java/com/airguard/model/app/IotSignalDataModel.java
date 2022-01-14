package com.airguard.model.app;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "kweather_iot")
@XmlAccessorType(XmlAccessType.FIELD)
public class IotSignalDataModel {

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

  @XmlElement(name = "accx")
  private String accx;

  @XmlElement(name = "accy")
  private String accy;

  @XmlElement(name = "accz")
  private String accz;
}
