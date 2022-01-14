package com.airguard.mapper.main.app;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.airguard.model.app.AppStation;

@Mapper
public interface StationMapper {

  /*
   * 스테이션 이름 변경
   */
  void chgStationName(AppStation station);

  /*
   * 스테이션 변경
   */
  void delStationInfo(String oldDeviceIdx);

  void delVentInfo(String oldDeviceIdx);

  void addStationInfo(AppStation station);

  /*
   * 스테이션 공유 설정
   */
  void stationShared(AppStation station);

  /*
   * 스테이션 설치장소 설정
   */
  void setStationInfo(AppStation station);

  /*
   * 위치(지역) 설정
   */
  void memberRegionUpdate(AppStation station);

  void infoRegionUpdate(AppStation station);

  /*
   * 측정 항목 조회
   */
  List<HashMap<String, Object>> getElements(String stationNo);

  void delMemberDevice(String deviceIdx);
}
