package com.airguard.service.system;

import com.airguard.exception.SQLException;
import com.airguard.mapper.main.system.GroupMapper;
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
  GroupMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;


  public List<Master> selectMasterList(Search search) {
    return readOnlyMapper.selectMasterList(search);
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
  public void insertGroup(Group group) {
    String encPwd = Sha256EncryptUtil.ShaEncoder(group.getGroupPw());
    group.setGroupPw(encPwd);

    mapper.insertGroup(group);

    for (String memberIdx : group.getMemberIdxs()) {
      mapper.insertGroupMember(Integer.toString(group.getIdx()), memberIdx);
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void updateGroup(Group group, Boolean memberReset) throws Exception {

    if (!group.getGroupPw().isEmpty() && !"".equals(group.getGroupPw().trim()) && group.getGroupPw() != null) {
      String encPwd = Sha256EncryptUtil.ShaEncoder(group.getGroupPw());
      group.setGroupPw(encPwd);

    } else {
      group.setGroupPw(null);
    }

    if (mapper.updateGroup(group) != 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    if (memberReset) {
      mapper.deleteGroupMember(Integer.toString(group.getIdx()));
      for (String memberIdx : group.getMemberIdxs()) {
        mapper.insertGroupMember(Integer.toString(group.getIdx()), memberIdx);
      }
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public int deleteGroup(String idx) {

    if (readOnlyMapper.deleteGroupDidCheck(idx) != 0) {
      return 2;

    } else if (readOnlyMapper.deleteGroupMemberCheck(idx) != 0) {
      return 3;

    } else {
      mapper.deleteGroup(idx);
      mapper.deleteGroupMember(idx);
      return 1;
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void groupLoginInfoUpdate(Group group) throws SQLException {

    if (mapper.groupLoginInfoUpdate(group) != 1) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

  }

}
