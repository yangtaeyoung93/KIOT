package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.system.Device;

/**
 * @FileName : DeviceMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 5.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DeviceMapper {

  /**
   * @Method Name : insertDevice
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param device
   */
  void insertDevice(Device device);

  /**
   * @Method Name : updateDevice
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param device
   */
  void updateDevice(Device device);

  /**
   * @Method Name : deleteDevice
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteDevice(String idx);

  /**
   * @Method Name : deleteDeviceVent
   * @Date : 2020. 5. 29.
   * @Auth : Kim, DongGi
   * @param ventDeviceIdx
   */
  void deleteDeviceVent(String ventDeviceIdx);

}
