package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.system.DeviceAttribute;

/**
 * @FileName : DeviceAttributeMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 5.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DeviceAttributeMapper {

  /**
   * @Method Name : insertDeviceAttribute
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceModel
   */
  void insertDeviceAttribute(DeviceAttribute deviceModel);

  /**
   * @Method Name : updateDeviceAttribute
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceModel
   */
  void updateDeviceAttribute(DeviceAttribute deviceModel);

  /**
   * @Method Name : deleteDeviceAttribute
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteDeviceAttribute(String idx);
}
