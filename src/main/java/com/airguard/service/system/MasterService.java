package com.airguard.service.system;

import com.airguard.exception.SQLException;
import com.airguard.mapper.main.system.GroupMapper;
import com.airguard.mapper.main.system.MasterMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.common.Search;
import com.airguard.model.system.Group;
import com.airguard.model.system.Master;
import com.airguard.util.Sha256EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MasterService {

  private static final Logger logger = LoggerFactory.getLogger(MasterService.class);

  @Autowired
  MasterMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;


  public List<Master> selectMasterList(Search search) {
    return readOnlyMapper.selectMasterList(search);
  }

  public List<Master> selectMasterGroupOne(String idx,String flag) {
    return readOnlyMapper.selectMasterGroupOne(idx,flag);
  }
  public List<Group> selectGroupCustomList(String searchValue) {
    return readOnlyMapper.selectGroupCustomList(searchValue);
  }


  public Master selectMasterOne(String idx) {
    return readOnlyMapper.selectMasterOne(idx);
  }

  public List<Map<String, Object>> selectGroupMembers(String idx) {
    return readOnlyMapper.selectGroupMembers(idx);
  }

  public List<Group> selectGroupMemberList() {
    return readOnlyMapper.selectGroupMemberList();
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void insertMaster(Master master) {
    String encPwd = Sha256EncryptUtil.ShaEncoder(master.getMasterPw());
    master.setMasterPw(encPwd);

    mapper.insertMaster(master);

    for (String groupIdx : master.getGroupIdxs()) {
      mapper.insertMasterGroup(Integer.toString(master.getIdx()), groupIdx);
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void updateGroup(Master master, Boolean groupReset) throws Exception {

    if (!master.getMasterPw().isEmpty() && !"".equals(master.getMasterPw().trim()) && master.getMasterPw() != null) {
      String encPwd = Sha256EncryptUtil.ShaEncoder(master.getMasterPw());
      master.setMasterPw(encPwd);

    } else {
      master.setMasterPw(null);
    }

    if (mapper.updateMaster(master) != 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    if (groupReset) {
      mapper.deleteMasterGroup(Integer.toString(master.getIdx()));
      for (String groupIdx : master.getGroupIdxs()) {
        mapper.insertMasterGroup(Integer.toString(master.getIdx()), groupIdx);
      }
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void updateMaster(Master master, Boolean groupReset) throws Exception {

    if (!master.getMasterPw().isEmpty() && !"".equals(master.getMasterPw().trim()) && master.getMasterPw() != null) {
      String encPwd = Sha256EncryptUtil.ShaEncoder(master.getMasterPw());
      master.setMasterPw(encPwd);
    } else {
      master.setMasterPw(null);
    }

    if (mapper.updateMaster(master) != 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    if (groupReset) {
      try {

      mapper.deleteMasterGroup(Integer.toString(master.getIdx()));
        for (String groupIdx : master.getGroupIdxs()) {
          mapper.insertMasterGroup(Integer.toString(master.getIdx()), groupIdx);
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public int deleteMaster(String idx) {

    if (readOnlyMapper.deleteGroupDidCheck(idx) != 0) {
      return 2;

    } else if (readOnlyMapper.deleteGroupMemberCheck(idx) != 0) {
      return 3;

    } else {
      mapper.deleteMaster(idx);
      mapper.deleteMasterGroup(idx);
      return 1;
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void masterLoginInfoUpdate(Master master) throws SQLException {

    if (mapper.masterLoginInfoUpdate(master) != 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

  }

  public List<Master> selectMasterGroupList() {
    return readOnlyMapper.selectMasterGroupList();
  }
}
