package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.system.DeviceElements;

/**
 * @FileName : DeviceElementsMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 5.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DeviceElementsMapper {

  /**
   * @Method Name : insertDeviceElements
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceModel
   */
  void insertDeviceElements(DeviceElements deviceModel);

  /**
   * @Method Name : updateDeviceElements
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceModel
   */
  void updateDeviceElements(DeviceElements deviceModel);

  /**
   * @Method Name : deleteDeviceElements
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteDeviceElements(String idx);
}
