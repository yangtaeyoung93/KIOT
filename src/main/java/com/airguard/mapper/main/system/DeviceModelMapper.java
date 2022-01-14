package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.system.DeviceModel;

/**
 * @FileName : DeviceModelMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 5.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DeviceModelMapper {

  /**
   * @Method Name : insertDeviceModel
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceModel
   */
  void insertDeviceModel(DeviceModel deviceModel);

  /**
   * @Method Name : updateDeviceModel
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceModel
   */
  void updateDeviceModel(DeviceModel deviceModel);

  /**
   * @Method Name : deleteDeviceModel
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteDeviceModel(String idx);

  /**
   * @Method Name : deleteDeviceModelAttribute
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteDeviceModelAttribute(String idx);

  /**
   * @Method Name : deleteDeviceModelElements
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteDeviceModelElements(String idx);

  /**
   * @Method Name : addDeviceModelAttribute
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param attributeIdx
   * @param deviceModelIdx
   * @param attributeValue
   */
  void addDeviceModelAttribute(@Param("attributeIdx") String attributeIdx,
      @Param("deviceModelIdx") String deviceModelIdx,
      @Param("attributeValue") String attributeValue);

  /**
   * @Method Name : addDeviceModelElement
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param elementIdx
   * @param deviceModelIdx
   */
  void addDeviceModelElement(@Param("elementIdx") String elementIdx,
      @Param("deviceModelIdx") String deviceModelIdx);

}
