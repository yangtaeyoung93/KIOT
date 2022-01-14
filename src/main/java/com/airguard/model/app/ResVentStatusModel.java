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
public class ResVentStatusModel {

  @XmlElement(name = "RESULT")
  private long result;

  @XmlElement(name = "ERROR_CODE")
  private long errorCode;

  @XmlElement(name = "TIME")
  private String time;
  @XmlElement(name = "WIFI")
  private String wifi;
  @XmlElement(name = "FILTER")
  private String filter;
  @XmlElement(name = "POWER")
  private String power;
  @XmlElement(name = "WIND")
  private String wind;
  @XmlElement(name = "BYPASS")
  private String bypass;
  @XmlElement(name = "AUTO")
  private String auto;
  @XmlElement(name = "AUTO_CAUSE")
  private Long autoCause;
  @XmlElement(name = "KHAI")
  private String khai;
  @XmlElement(name = "IN_PM10")
  private String inPm10;
  @XmlElement(name = "IN_PM25")
  private String inPm25;
  @XmlElement(name = "IN_CO2")
  private String inCo2;
  @XmlElement(name = "IN_VOCS")
  private String inVocs;
  @XmlElement(name = "OUT_NAME")
  private String outName;
  @XmlElement(name = "OUT_PM10")
  private String outPm10;
  @XmlElement(name = "OUT_PM25")
  private String outPm25;
  @XmlElement(name = "KOR_NAME")
  private String korName;
  @XmlElement(name = "KOR_PM10")
  private String korPm10;
  @XmlElement(name = "KOR_PM25")
  private String korPm25;
  @XmlElement(name = "CLEANER")
  private String cleaner;
  @XmlElement(name = "FIRE_ALARM")
  private String fireAlarm;
  @XmlElement(name = "WATER_ALARM")
  private String waterAlarm;
  @XmlElement(name = "DEV_STAT")
  private String devStat;
}
