package com.airguard.mapper.main.system;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.airguard.model.common.Search;
import com.airguard.model.system.FileVo;
import com.airguard.model.system.MemberDevice;
import com.airguard.model.system.Vent;

@Mapper
public interface MemberDeviceMapper {

  void insertMemberDevice(MemberDevice md);

  void deleteMemberDevice(MemberDevice md);

  void insertMemberDeviceVent(@Param("memberIdx") String memberIdx,
      @Param("iaqDeviceIdx") String iaqDeviceIdx,
      @Param("ventDeviceIdx") String ventDeviceIdx);

  void updateMemberDeviceVent(Vent vent);

  void deleteMemberDeviceVent(String idx);

  void deleteMemberDeviceVentAll(Vent vent);

  void fileUpload(FileVo vo);

  void updateFile(FileVo vo);

  void deleteFile(String idx);

  int insertMemberDeviceApp(HashMap<String, Object> req);

  void updateMemberDevice(HashMap<String, Object> req);

  int updateMemberDeviceBySerial(MemberDevice md);

  List<MemberDevice> selectMemberDeviceList(Search search);
}
