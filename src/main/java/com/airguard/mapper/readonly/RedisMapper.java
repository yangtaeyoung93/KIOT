package com.airguard.mapper.readonly;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.redis.RedisVentDto;

@Mapper
public interface RedisMapper {

  String selectIaqSerial(String iaqDeviceIdx);

  List<RedisVentDto> selectVentList(String iaqDeviceIdx);

}
