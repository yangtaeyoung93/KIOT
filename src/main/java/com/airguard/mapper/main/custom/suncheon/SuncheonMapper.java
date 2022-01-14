package com.airguard.mapper.main.custom.suncheon;

import com.airguard.model.custom.suncheon.dto.DeviceDto;
import com.airguard.model.custom.suncheon.dto.MenuDto;
import com.airguard.model.custom.suncheon.dto.UpdateDto;
import com.airguard.model.custom.suncheon.dto.UserDto;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SuncheonMapper {

  UserDto selectUserByUserId(@Param("userId") String userId);

  List<UserDto> selectAllUser();

  int insertUser(UserDto userDto);

  int updateUserByIdx(UserDto userDto);

  int deleteUserByIdx(@Param("idx") int idx);

  DeviceDto selectDeviceByIdx(@Param("idx") int idx);

  List<DeviceDto> selectAllDevice(@Param("key") String key, @Param("value") String value);

  int insertDevice(DeviceDto deviceDto);

  int updateDeviceByIdx(DeviceDto deviceDto);

  int deleteDeviceByIdx(@Param("idx") int idx);

  List<UpdateDto> selectDeviceUpdateByIdx(@Param("deviceIdx") int deviceIdx);

  int insertDeviceUpdate(@Param("deviceIdx") int deviceIdx, @Param("updateDt") String updateDt,
      @Param("updateInfo") String updateInfo);

  List<MenuDto> selectMenu();

}
