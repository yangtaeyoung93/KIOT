package com.airguard.mapper.main.common;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.common.DamperDto;

/**
 * @FileName : AdminMapper.java
 * @Project : KIOT
 * @Date : 2020. 3. 10.
 * @Auth : Kim, DongGi
 */
@Mapper
public interface DamperMapper {

  List<DamperDto> selectDamper();

  int updateDamper(DamperDto damper);
}
