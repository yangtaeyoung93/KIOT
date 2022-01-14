package com.airguard.mapper.main.platform;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.control.ControlDto;
import com.airguard.model.platform.CollectionDto;
import com.airguard.model.redis.RedisVentDto;

@Mapper
public interface PlatformMapper {

  List<CollectionDto> selectCollectionDevice(@Param("siDo") String siDo, @Param("airMapYn") String airMapYn);

  List<CollectionDto> selectCollectionDeviceVent();

  List<CollectionDto> selectCollectionDeviceConnect();

  List<ControlDto> selectControlDevice(String deviceType);

  String selectIaqSerial(String iaqDeviceIdx);

  String selectIaqIdx(String iaqDeviceSerialNum);

  List<RedisVentDto> selectVentList(String iaqDeviceIdx);

  RedisVentDto selectVentOne(@Param("ventDeviceIdx") String ventDeviceIdx);

  void updateventAiMode(@Param("aiMode") String aiMode, @Param("ventSerial") String ventSerial);

  String ventSerialToIaqSerial(String ventSerial);

  String userIdToGroupId(String userId);

  String memberIdxToGroupId(String memberIdx);

  String idxToUserId(String idx);

  String idxToGroupId(String idx);

  void offsetUpdate(HashMap<String, Object> req);

  List<CollectionDto> selectUserDeviceList(String userId);

  List<String> selectUserVentList(String userId);

  List<CollectionDto> selectGroupDeviceList(String groupId);

  List<String> selectGroupVentList(String groupId);
}
