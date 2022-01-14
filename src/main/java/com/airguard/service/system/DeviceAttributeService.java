package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.system.DeviceAttributeMapper;
import com.airguard.model.system.DeviceAttribute;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class DeviceAttributeService {

  @Autowired
  DeviceAttributeMapper deviceAttributeMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<DeviceAttribute> selectDeviceAttributeAll() {
    return readOnlyMapper.selectDeviceAttributeAll();
  }

  public List<DeviceAttribute> selectDeviceAttribute() {
    return readOnlyMapper.selectDeviceAttribute();
  }

  public List<DeviceAttribute> selectDeviceAttributeVent() {
    return readOnlyMapper.selectDeviceAttributeVent();
  }

  public void insertDeviceAttribute(DeviceAttribute deviceAttribute) {
    deviceAttributeMapper.insertDeviceAttribute(deviceAttribute);

  }

  public void updateDeviceAttribute(DeviceAttribute deviceAttribute) {
    deviceAttributeMapper.updateDeviceAttribute(deviceAttribute);
  }

  public void deleteDeviceAttribute(String idx) {
    deviceAttributeMapper.deleteDeviceAttribute(idx);
  }
}
