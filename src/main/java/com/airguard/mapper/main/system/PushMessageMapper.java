package com.airguard.mapper.main.system;

import org.apache.ibatis.annotations.Mapper;
import com.airguard.model.system.PushMessage;

@Mapper
public interface PushMessageMapper {

  int pushMessageSave(PushMessage data);

  int pushMessageUpdate(PushMessage data);
}
