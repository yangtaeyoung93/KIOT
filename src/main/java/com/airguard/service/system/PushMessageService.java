package com.airguard.service.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.airguard.mapper.main.system.PushMessageMapper;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.system.PushMessage;

@Service
public class PushMessageService {

  @Autowired
  private ReadOnlyMapper readOnlyMapper;

  @Autowired
  private PushMessageMapper mapper;

  public List<PushMessage> selectPushMessageList() {
    return readOnlyMapper.selectPushMessageList();
  }

  public PushMessage selectPushMessageSearch(String idx) {
    return readOnlyMapper.selectPushMessageSearch(idx);
  }

  public int pushMessageSave(PushMessage data) {
    return mapper.pushMessageSave(data);
  }

  public int pushMessageUpdate(PushMessage data) {
    return mapper.pushMessageUpdate(data);
  }
}
