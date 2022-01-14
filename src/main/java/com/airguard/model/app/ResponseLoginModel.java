/**
 * @FileName : ResLoginModel.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 5. 12.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : ResLoginModel.java
 * @Project : KIOT
 * @Date : 2020. 5. 12.
 * @Auth : Kim, DongGi
 */
@Getter
@Setter
@XmlRootElement(name = "kweather_iot")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseLoginModel {

  private long result;

  @XmlElement(name = "error_code")
  private long errorCode;

  @XmlElement(name = "data_url")
  private String dataUrl;

  @XmlElement(name = "station_no")
  private String stationNo;

  @XmlElement(name = "station_name")
  private String stationName;

  @XmlElement(name = "region_id")
  private String regionId;

  @XmlElement(name = "region_name")
  private String regionName;
}
