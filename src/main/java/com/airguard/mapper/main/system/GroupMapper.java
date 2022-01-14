package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.airguard.model.system.Group;

/**
 * @FileName : GroupMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 6.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface GroupMapper {

    /**
     * @param group group
     * @Method Name : insertGroup
     * @Date : 2020. 3. 6.
     * @Auth : Kim, DongGi
     */
    void insertGroup(Group group);

    /**
     * @param groupIdx  groupIdx
     * @param memberIdx memberIdx
     * @Method Name : insertGroupMember
     * @Date : 2020. 3. 9.
     * @Auth : Kim, DongGi
     */
    void insertGroupMember(@Param("groupIdx") String groupIdx, @Param("memberIdx") String memberIdx);

    /**
     * @param group group
     * @Method Name : updateGroup
     * @Date : 2021. 4. 24.
     * @Auth : Yoo, HS
     */
    int updateGroup(Group group);

    /**
     * @param idx idx
     * @Method Name : deleteGroup
     * @Date : 2020. 3. 6.
     * @Auth : Kim, DongGi
     */
    void deleteGroup(String idx);

    /**
     * @param idx idx
     * @Method Name : deleteGroupMember
     * @Date : 2020. 3. 9.
     * @Auth : Kim, DongGi
     */
    int deleteGroupMember(String idx);

}
