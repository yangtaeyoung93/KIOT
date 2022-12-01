package com.airguard.mapper.readonly;

import java.util.HashMap;
import java.util.List;

import com.airguard.model.datacenter.SeoulMetaData;
import com.airguard.model.system.PopupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.airguard.model.datacenter.DatacenterConnectDto;
import com.airguard.model.system.DeviceElements;

@Mapper
public interface DatacenterMapper {

  List<String> selectDeviceType(@Param("userId") String userId);

  List<String> selectGroupDeviceType(String groupId);

  List<DatacenterConnectDto> selectUserDevice(String userId);

  List<DatacenterConnectDto> selectUserVentDevice(String userId);

  List<String> selectGroupForUser(String groupId);

  List<DeviceElements> selectDeviceModelElements(String serial);

  List<String> selectMemberDeviceSerialList(@Param("type") String type, @Param("userId") String userId);

  PopupVO getPopUp();

  List<SeoulMetaData> airDeviceList();
  List<SeoulMetaData> OaqDeviceList();
  List<SeoulMetaData> IaqDeviceList();
}
