package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import com.airguard.model.system.DeviceType;

/**
 * @FileName : DeviceTypeMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 5.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DeviceTypeMapper {

  /**
   * @Method Name : insertCategory
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceType
   */
  void insertCategory(DeviceType deviceType);

  /**
   * @Method Name : updateCategory
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param deviceType
   */
  void updateCategory(DeviceType deviceType);

  /**
   * @Method Name : deleteCategory
   * @Date : 2020. 3. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteCategory(String idx);
}
