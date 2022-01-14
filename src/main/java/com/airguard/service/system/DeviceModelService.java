package com.airguard.service.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.airguard.mapper.main.system.DeviceModelMapper;
import com.airguard.model.common.Search;
import com.airguard.model.system.DeviceAttribute;
import com.airguard.model.system.DeviceElements;
import com.airguard.model.system.DeviceModel;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class DeviceModelService {

  @Autowired
  DeviceModelMapper deviceModelMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<DeviceModel> selectDeviceModel(Search search) {
    return readOnlyMapper.selectDeviceModel(search);
  }

  public DeviceModel selectDeviceModelOne(String idx) {
    return readOnlyMapper.selectDeviceModelOne(idx);
  }

  public List<DeviceModel> selectTypeNameDeviceModel(String deviceType) {
    return readOnlyMapper.selectTypeNameDeviceModel(deviceType);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void insertDeviceModel(DeviceModel deviceModel) {

    deviceModelMapper.insertDeviceModel(deviceModel);

    for (int i = 0; i < deviceModel.getAttributeIdx().size(); i++) {
      deviceModelMapper.addDeviceModelAttribute(deviceModel.getAttributeIdx().get(i),
          Integer.toString(deviceModel.getIdx()), deviceModel.getDeviceValue().get(i));
    }

    for (String elementIdx : deviceModel.getElementIdx()) {
      deviceModelMapper.addDeviceModelElement(elementIdx, Integer.toString(deviceModel.getIdx()));
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void updateDeviceModel(DeviceModel deviceModel) {
    String idx = Integer.toString(deviceModel.getIdx());

    deviceModelMapper.updateDeviceModel(deviceModel);
    deviceModelMapper.deleteDeviceModelAttribute(idx);
    deviceModelMapper.deleteDeviceModelElements(idx);

    for (int i = 0; i < deviceModel.getAttributeIdx().size(); i++) {
      deviceModelMapper.addDeviceModelAttribute(deviceModel.getAttributeIdx().get(i), idx,
          deviceModel.getDeviceValue().get(i));
    }

    for (String elementIdx : deviceModel.getElementIdx()) {
      deviceModelMapper.addDeviceModelElement(elementIdx, idx);
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public int deleteDeviceModel(String idx) {

    if (readOnlyMapper.deleteDeviceModelCheck(idx) != 0) {
      return 2;

    } else {
      deviceModelMapper.deleteDeviceModelAttribute(idx);
      deviceModelMapper.deleteDeviceModelElements(idx);
      deviceModelMapper.deleteDeviceModel(idx);
      return 1;
    }
  }

  public List<DeviceAttribute> selectOneDeviceModelAttribute(String idx) {
    return readOnlyMapper.selectOneDeviceModelAttribute(idx);
  }

  public List<DeviceAttribute> selectOneDeviceModelAttributeVent(String idx) {
    return readOnlyMapper.selectOneDeviceModelAttributeVent(idx);
  }

  public List<DeviceElements> selectOneDeviceModelElements(String idx) {
    return readOnlyMapper.selectOneDeviceModelElements(idx);
  }

  public List<DeviceElements> selectOneDeviceModelElementsVent(String idx) {
    return readOnlyMapper.selectOneDeviceModelElementsVent(idx);
  }
}
