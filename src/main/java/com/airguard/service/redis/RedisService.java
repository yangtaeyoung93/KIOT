package com.airguard.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.airguard.mapper.readonly.RedisMapper;

@Service
public class RedisService {

  @Autowired
  RedisMapper mapper;

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

}
