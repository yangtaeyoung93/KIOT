package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airguard.mapper.main.system.SpaceMapper;
import com.airguard.model.common.Search;
import com.airguard.model.system.Space;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class SpaceService {

  @Autowired
  SpaceMapper spaceMapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<Space> selectSpaceList(Search search) {
    return readOnlyMapper.selectSpaceSearchList(search);
  }

  public void insertSpace(Space space) {
    spaceMapper.insertSpace(space);
  }

  public void updateSpace(Space space) {
    spaceMapper.updateSpace(space);
  }

  public int selectLowSpaceAuth(String idx) {
    return readOnlyMapper.selectLowSpaceAuth(idx);
  }

  public List<Space> selectHighSpace(String search) {
    return readOnlyMapper.selectHighSpace(search);
  }

  public void deleteSpace(String idx) {
    spaceMapper.deleteSpace(idx);
  }
}
