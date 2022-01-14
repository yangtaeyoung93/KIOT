package com.airguard.util;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisManageUtil {

  static final Logger logger = LoggerFactory.getLogger(RedisManageUtil.class);

  public static final String REDIS_NO_DATA = "NO_DATA";

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  public Set<String> getRedisKeyList(String keyPatterns) {
    Set<String> keys;
    keys = redisTemplate.keys(keyPatterns);
    return keys;
  }

  public boolean setRedisData(String key, Object value) {
    ValueOperations<String, Object> vop = redisTemplate.opsForValue();
    boolean result;

    try {

      vop.set(key, value);
      result = true;

    } catch (Exception e) {
      result = false;
    }

    return result;
  }

  public Object getRedisData(String key) {
    Object result;
    ValueOperations<String, Object> vop = redisTemplate.opsForValue();

    result = (vop.get(key) == null ? REDIS_NO_DATA : vop.get(key));

    return result;
  }

  public boolean delRedisData(String key) {
    boolean result;

    try {

      result = redisTemplate.delete(key);

    } catch (Exception e) {
      result = false;
    }

    return result;
  }

  public Set<String> getRedisDatas(String key) {
    Set<String> datas;

    try {

      datas = redisTemplate.keys("*".concat(key).concat("*"));

    } catch (Exception e) {
      datas = new HashSet<>();
    }

    return datas;
  }
}
