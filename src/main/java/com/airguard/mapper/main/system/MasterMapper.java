package com.airguard.mapper.main.system;


import com.airguard.model.system.Master;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @FileName : GroupMapper.java
 * @Project : KIOT
 * @Date : 2022. 5. 17.
 * @Auth : Yang TaeYoung
 */
@Mapper
public interface MasterMapper {
    void insertMaster(Master master);
    void insertMasterGroup(@Param("idx") String idx, @Param("groupIdx") String groupIdx);
    int updateMaster(Master master);
    int deleteMasterGroup(String idx);
    void deleteMaster(String idx);
    int masterLoginInfoUpdate(Master master);
}
