package com.airguard.mapper.main.app;

import java.util.HashMap;
import org.apache.ibatis.annotations.Mapper;

import com.airguard.model.app.AppUser;

@Mapper
public interface UserMapper {

  /*
   * 회원 가입
   */
  int addUser(AppUser user);

  int addUserDeviceInfo(AppUser user);

  int addAppDeviceInfo(AppUser user);

  /*
   * 회원 탈퇴
   */
  void delUser(String userId);

  void delAppDevice(String memberIdx);

  void delDevice(String serialNum);

  void delDeviceInfo(String memberIdx);

  void delVentDeviceInfo(String memberIdx);

  /*
   * 로그아웃
   */
  void delAppDeviceInfo(String deviceImei);

  /*
   * 비밀번호 변경
   */
  void chgPasswd(AppUser user);

  /* ===================================== */

  void insertFcmTokenInfo(HashMap<String, String> req);

  int deleteFcmTokenInfo(HashMap<String, String> req);

  int insertUserInfo(HashMap<String, String> req);

  int updateUserInfo(HashMap<String, String> req);
}
