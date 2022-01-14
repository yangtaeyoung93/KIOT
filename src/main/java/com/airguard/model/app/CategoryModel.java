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
public class CategoryModel {

  @XmlElement(name = "main")
  private String main;

  @XmlElement(name = "main_num")
  private String mainNum;

  @XmlElement(name = "sub")
  private String sub;

  @XmlElement(name = "sub_num")
  private String subNum;
}
