package com.airguard.mapper.main.custom.clust;

import com.airguard.model.custom.clust.dto.DeviceInfoDto;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClustMapper {

    DeviceInfoDto selectInfoBySerial(@Param("serial") String serial);
    
}
