package com.airguard.model.app;

import java.util.List;

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
public class ResponseModel {

  @XmlElement(name = "result")
  private long result;

  @XmlElement(name = "error_code")
  private long errorCode;

  @XmlElement(name = "data_url")
  private String dataUrl;

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "ITEM")
  private List<ResStationModel> resStation;
}
