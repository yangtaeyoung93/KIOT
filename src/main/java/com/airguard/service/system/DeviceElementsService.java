package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.system.DeviceElementsMapper;
import com.airguard.model.system.DeviceElements;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class DeviceElementsService {

  @Autowired
  DeviceElementsMapper deviceElementsMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<DeviceElements> selectDeviceElements() {
    return readOnlyMapper.selectDeviceElements();
  }

  public List<DeviceElements> selectDeviceElementsVent() {
    return readOnlyMapper.selectDeviceElementsVent();
  }

  public List<DeviceElements> selectDeviceElementsAll() {
    return readOnlyMapper.selectDeviceElementsAll();
  }

  public void insertDeviceElements(DeviceElements deviceElements) {
    deviceElementsMapper.insertDeviceElements(deviceElements);
  }

  public void updateDeviceElements(DeviceElements deviceElements) {
    deviceElementsMapper.updateDeviceElements(deviceElements);
  }

  public void deleteDeviceElements(String idx) {
    deviceElementsMapper.deleteDeviceElements(idx);
  }
}
