package com.airguard.mapper.main.app;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.app.AppVent;

@Mapper
public interface VentMapper {

  /*
   * 환기청정기, 기기 등록
   */
  void addVent(AppVent vent);

  /*
   * 환기청정기, 기기 해제
   */
  void delVent(AppVent vent);
}
