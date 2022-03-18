package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.airguard.model.system.Member;

/**
 * @FileName : MemberMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 6.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface MemberMapper {

  /**
   * @Method Name : memberLoginInfoUpdate
   * @Date : 2020. 3. 10.
   * @Auth : Kim, DongGi
   * @param member
   */
  void memberLoginInfoUpdate(Member member);

  /**
   * @Method Name : insertMember
   * @Date : 2020. 3. 6.
   * @Auth : Kim, DongGi
   * @param member
   */
  void insertMember(Member member);

  /**
   * @Method Name : updateMember
   * @Date : 2020. 3. 6.
   * @Auth : Kim, DongGi
   * @param member
   */
  void updateMember(Member member);

  /**
   * @Method Name : deleteMember
   * @Date : 2020. 3. 6.
   * @Auth : Kim, DongGi
   * @param idx
   */
  int deleteMember(String idx);

  void updateMemberPassword(@Param("idx") String idx, @Param("password") String password);

  void updateMemberLoginCount(@Param("userId") String userId, @Param("success") Integer success);

  /**
   * @Method Name : deleteAppDeviceInfo
   * @Date : 2020. 4. 5.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteAppDeviceInfo(String idx);

  void resetLoginCount(Member member);
}
