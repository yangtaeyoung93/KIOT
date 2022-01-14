package com.airguard.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.common.AdminMapper;
import com.airguard.mapper.main.system.DeviceMapper;
import com.airguard.mapper.main.system.DeviceModelMapper;
import com.airguard.mapper.main.system.DeviceTypeMapper;
import com.airguard.mapper.main.system.GroupDidMapper;
import com.airguard.mapper.main.system.GroupMapper;
import com.airguard.mapper.main.system.MemberDeviceMapper;
import com.airguard.mapper.main.system.MemberMapper;
import com.airguard.mapper.main.system.SpaceMapper;
import com.airguard.model.system.Space;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class CheckService {

  @Autowired
  AdminMapper adminMapper;

  @Autowired
  MemberMapper memberMapper;

  @Autowired
  GroupMapper groupMapper;

  @Autowired
  GroupDidMapper groupDidMapper;

  @Autowired
  DeviceMapper deviceMapper;

  @Autowired
  DeviceModelMapper deviceModelMapper;

  @Autowired
  DeviceTypeMapper deviceTypeMapper;

  @Autowired
  MemberDeviceMapper memberDeviceMapper;

  @Autowired
  SpaceMapper spaceMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public int checkAdminUserId(String userId) {
    int resultCode = 0;
    int result = readOnlyMapper.checkAdminUserId(userId);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkUserId(String userId) {
    return readOnlyMapper.checkUserId(userId);
  }

  public int checkGroupId(String groupId) {
    int resultCode = 0;
    int result = readOnlyMapper.checkGroupId(groupId);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkDidCode(String didCode) {
    int resultCode = 0;
    int result = readOnlyMapper.checkDidCode(didCode);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkSerialNum(String serialNum) {
    int resultCode = 0;
    int result = readOnlyMapper.checkSerialNum(serialNum);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkDeviceModel(String deviceModel, String deviceTypeIdx) {
    int resultCode = 0;
    int result = readOnlyMapper.checkDeviceModel(deviceModel, deviceTypeIdx);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkDeviceType(String deviceType) {
    int resultCode = 0;
    int result = readOnlyMapper.checkDeviceType(deviceType);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkDeviceTypeName(String deviceTypeName) {
    int resultCode = 0;
    int result = readOnlyMapper.checkDeviceTypeName(deviceTypeName);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkStationName(String memberIdx, String stationName) {
    int resultCode = 0;
    int result = readOnlyMapper.checkStationName(memberIdx, stationName);

    if (result == 0) {
      resultCode = 1;
    }

    return resultCode;
  }

  public int checkSpaceName(Space space) {
    return readOnlyMapper.checkSpaceName(space);
  }

}
