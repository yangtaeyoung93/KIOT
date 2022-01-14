package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.system.GroupDid;

/**
 * @FileName : GroupDidMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 10.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface GroupDidMapper {

  /**
   * @Method Name : insertGroupDid
   * @Date : 2020. 3. 10.
   * @Auth : Kim, DongGi
   * @param gd
   */
  void insertGroupDid(GroupDid gd);

  /**
   * @Method Name : insertGroupDidMember
   * @Date : 2020. 3. 10.
   * @Auth : Kim, DongGi
   * @param groupIdx
   * @param didGroupIdx
   * @param memberIdx
   */
  void insertGroupDidMember(@Param("groupIdx") String groupIdx,
                            @Param("didGroupIdx") String didGroupIdx, @Param("memberIdx") String memberIdx);

  /**
   * @Method Name : updateGroupDid
   * @Date : 2020. 3. 10.
   * @Auth : Kim, DongGi
   * @param gd
   */
  void updateGroupDid(GroupDid gd);

  /**
   * @Method Name : deleteGroupDid
   * @Date : 2020. 3. 10.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteGroupDid(String idx);

  /**
   * @Method Name : insertGroupDidMemberOne
   * @Date : 2020. 4. 9.
   * @Auth : Kim, DongGi
   * @param gd
   */
  void insertGroupDidMemberOne(GroupDid gd);

  /**
   * @Method Name : deleteGroupDidMember
   * @Date : 2020. 3. 10.
   * @Auth : Kim, DongGi
   * @param idx
   */
  void deleteGroupDidMember(String idx);

  /**
   * @Method Name : deleteGroupDidMemberOne
   * @Date : 2020. 3. 27.
   * @Auth : Kim, DongGi
   */
  void deleteGroupDidMemberOne(@Param("groupDidIdx") String groupDidIdx,
                               @Param("memberIdx") String memberIdx);

}
