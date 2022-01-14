package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.system.Menu;

@Mapper
public interface MenuMapper {

  /**
   * @Method Name : insertMenu
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param menu
   */
  void insertMenu(Menu menu);

  /**
   * @Method Name : updateMenu
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param menu
   */
  void updateMenu(Menu menu);

  /**
   * @Method Name : deleteMenu
   * @Date : 2020. 3. 9.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteMenu(String idx);

  /**
   * @Method Name : insertMenuAuth
   * @Date : 2020. 7. 13.
   * @Auth : Kim, DongGi
   * @param adminIdx
   * @param menuIdx
   */
  void insertMenuAuth(@Param("adminIdx") String adminIdx, @Param("menuIdx") String menuIdx);

  /**
   * @Method Name : deleteMenuAuth
   * @Date : 2020. 7. 13.
   * @Auth : Kim, DongGi
   * @param menuIdx
   */
  void deleteMenuAuth(@Param("adminIdx") String adminIdx, @Param("menuIdx") String menuIdx);
}
