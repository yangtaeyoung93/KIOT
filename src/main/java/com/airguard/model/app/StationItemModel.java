package com.airguard.model.app;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "kweather_iot")
public class StationItemModel {

  @XmlElement(name = "count")
  private long count;

  @XmlElement(name = "category")
  private List<CategoryModel> categoryList;
}
