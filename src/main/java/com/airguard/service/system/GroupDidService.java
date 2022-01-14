package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.airguard.mapper.main.system.GroupDidMapper;
import com.airguard.model.common.Search;
import com.airguard.model.system.GroupDid;
import com.airguard.mapper.readonly.ReadOnlyMapper;

@Service
public class GroupDidService {

  @Autowired
  GroupDidMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public List<GroupDid> selectGroupDidList(Search search) {
    return readOnlyMapper.selectGroupDidList(search);
  }

  public List<GroupDid> selectGroupDidOne(String idx) {
    return readOnlyMapper.selectGroupDidOne(idx);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void insertGroupDid(GroupDid gd) {
    mapper.insertGroupDid(gd);
    for (String member : gd.getMembers()) {
      mapper.insertGroupDidMember(gd.getGroupIdx(), Integer.toString(gd.getIdx()), member);
    }
  }

  public void updateGroupDid(GroupDid gd) {
    mapper.updateGroupDid(gd);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void deleteGroupDid(String idx) {
    mapper.deleteGroupDid(idx);
    mapper.deleteGroupDidMember(idx);
  }

  public void insertGroupDidMemberOne(GroupDid gd) {
    mapper.insertGroupDidMemberOne(gd);
  }

  public GroupDid groupDidMemberCheck(GroupDid gd) {
    return readOnlyMapper.groupDidMemberCheck(gd);
  }

  public void deleteGroupDidMemberOne(String groupDidIdx, String memberIdx) {
    mapper.deleteGroupDidMemberOne(groupDidIdx, memberIdx);
  }

  public List<GroupDid> selectGroupList() {
    return readOnlyMapper.selectInsertGroupList();
  }

  public List<GroupDid> selectGroupMemberList(String idx) {
    return readOnlyMapper.selectInsertGroupMemberList(idx);
  }

  public List<GroupDid> selectGroupDidMemberList(String idx) {
    return readOnlyMapper.selectGroupDidMemberList(idx);
  }

}
