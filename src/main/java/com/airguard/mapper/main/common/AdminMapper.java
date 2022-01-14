package com.airguard.mapper.main.common;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.common.Admin;

/**
 * @FileName : AdminMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 10.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface AdminMapper {

  /**
   * @Method Name : adminLoginInfoUpdate
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param admin
   */
  void adminLoginInfoUpdate(Admin admin);

  /**
   * @Method Name : insertAdmin
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param admin
   */
  void insertAdmin(Admin admin);

  /**
   * @Method Name : updateAdmin
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param admin
   */
  void updateAdmin(Admin admin);

  /**
   * @Method Name : deleteAdmin
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteAdmin(String idx);
}
