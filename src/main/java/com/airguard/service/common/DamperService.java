package com.airguard.service.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.common.DamperMapper;
import com.airguard.model.common.DamperDto;

@Service
public class DamperService {

  @Autowired
  DamperMapper mapper;

  public List<DamperDto> selectDamper() {
    return mapper.selectDamper();
  }

  public int updateDamper(DamperDto damper) {
    return mapper.updateDamper(damper);
  }
}
