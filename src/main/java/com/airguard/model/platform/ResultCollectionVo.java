/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT_ADMIN
 * @Date : 2020. 4. 22.
 * @Auth : Kim, DongGi
 */
package com.airguard.model.platform;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @FileName : ResultSensorVo.java
 * @Project : KIOT
 * @Date : 2021. 3. 11.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
@ToString
public class ResultCollectionVo {

  private String serial;
  private String ventModel;
  private String memberIdx;
  private String deviceIdx;
  private String deviceType;
  private SensorDataDto sensor;
  private TimeStampDto service;
  private String timestamp;
  private String dataTime;
  private String groupId;
  private String groupName;
  private String masterIdx;
  private String masterName;
  private String spaceName;
  private String parentSpaceName;
  private String groupCompanyName;
  private String groupDepartName;
  private String userId;
  private String createDt;
  private String stationName;
  private String productDt;
  private String ventsStr;
  private String iaqSerial;
  private String ventCnt;
  private String aiMode;
  private String testYn;
  private String etc;
  private String lon;
  private String lat;
  private String dCode;
  private String airMapYn;
  private List<PlatformVentDto> vents;
  private boolean receiveFlag;
}
