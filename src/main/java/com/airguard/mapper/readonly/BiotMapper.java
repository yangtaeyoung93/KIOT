package com.airguard.mapper.readonly;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.biot.DongCollectionDto;

@Mapper
public interface BiotMapper {

  int apiAuthCheck(@Param("idType") String idType, @Param("id") String id,
      @Param("apiType") String apiType, @Param("allowIp") String allowIp);

  List<HashMap<String, String>> selectMemberDeviceList(String memberId);

  List<HashMap<String, String>> selectGroupDeviceList(String groupId);

  List<DongCollectionDto> selectDongCollectionList();

  List<DongCollectionDto> selectDongLocationList();
}
