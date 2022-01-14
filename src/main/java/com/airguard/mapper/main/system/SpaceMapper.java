package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.system.Space;

@Mapper
public interface SpaceMapper {

  void insertSpace(Space space);

  void updateSpace(Space space);

  void deleteSpace(String idx);

}
