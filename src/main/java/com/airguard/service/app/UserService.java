package com.airguard.service.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.airguard.mapper.main.app.UserMapper;
import com.airguard.model.app.ResponseModel;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.CommonConstant;
import com.airguard.util.Sha256EncryptUtil;
import com.airguard.model.app.AppUser;
import com.airguard.model.app.ResponseGroupLoginModel;
import com.airguard.model.app.ResponseLoginModel;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  UserMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public String getUserIdToGroupId(String userId) {
    return readOnlyMapper.getUserIdToGroupId(userId);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseModel addUser(AppUser user) {
    ResponseModel res = new ResponseModel();

    AppUser userInfo = readOnlyMapper.userSerialMatchCheck(user);

    if (readOnlyMapper.userIdCheck(user.getUserId()) != 0) {
      logger.error(
          "Call addUser API FAIL : [ ErrorCode : {}, ErrorMsg : please User Id Check, UserId : {} ]",
          1L, user.getUserId());
      res.setErrorCode(1L);
      res.setResult(CommonConstant.R_FAIL_CODE);

      return res;

    } else if (readOnlyMapper.serialCheck(user.getStationNo()) != 0) {
      logger.error(
          "Call addUser API FAIL : [ ErrorCode : {}, ErrorMsg : please Serial Number Check, Serial : {} ]",
          2L, user.getStationNo());
      res.setErrorCode(2L);
      res.setResult(CommonConstant.R_FAIL_CODE);

      return res;

    } else if (readOnlyMapper.userStationNameCheck(user.getStationName(), user.getUserId()) != 0) {
      logger.error(
          "Call addUser API FAIL : [ ErrorCode : {}, ErrorMsg : please Station Name Check, StationName : {} ]",
          3L, user.getStationName());
      res.setErrorCode(3L);
      res.setResult(CommonConstant.R_FAIL_CODE);

      return res;

    } else {
      res.setErrorCode(0L);

      user.setDeviceIdx(readOnlyMapper.getSerialToDeviceIdx(user.getStationNo()));
      user.setUserPw(Sha256EncryptUtil.ShaEncoder(user.getUserPw()));

      mapper.addUser(user);
      mapper.addUserDeviceInfo(user);
      mapper.addAppDeviceInfo(user);

      res.setResult(CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseModel delUser(AppUser user) {
    ResponseModel res = new ResponseModel();

    AppUser userInfo = readOnlyMapper.userSerialMatchCheck(user);
    user.setUserPw(Sha256EncryptUtil.ShaEncoder(user.getUserPw()));

    if (readOnlyMapper.userIdCheck(user.getUserId()) == 0) {
      logger.error(
          "Call delUser API FAIL : [ ErrorCode : {}, ErrorMsg : please User Id Check, UserId : {} ]",
          2L, user.getUserId());
      res.setErrorCode(2L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else if (readOnlyMapper.userMatchCheck(user) == 0) {
      logger.error(
          "Call delUser API FAIL : [ ErrorCode : {}, ErrorMsg : please User Id & Password Check, UserId : {} ]",
          1L, user.getUserId());
      res.setErrorCode(1L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else if (userInfo.getMemberIdx().isEmpty() || userInfo.getDeviceIdx().isEmpty()) {
      logger.error(
          "Call delUser API FAIL : [ ErrorCode : {}, ErrorMsg : please User Id & Serial Number, UserId : {} ]",
          4L, user.getUserId());
      res.setErrorCode(4L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else {
      mapper.delUser(user.getUserId());
      mapper.delAppDevice(userInfo.getMemberIdx());
      mapper.delDevice(user.getStationNo());
      mapper.delDeviceInfo(userInfo.getMemberIdx());
      mapper.delVentDeviceInfo(userInfo.getMemberIdx());

      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseLoginModel appLogin(AppUser user, HttpServletRequest request) {
    ResponseLoginModel res = new ResponseLoginModel();

    user.setUserPw(Sha256EncryptUtil.ShaEncoder(user.getUserPw()));

    if (readOnlyMapper.userMatchCheck(user) == 0) {
      logger.error(
          "Call appLogin API FAIL : [ ErrorCode : {} ErrorMsg : please User Match Check, ClientIp : {}, userId : {} ]",
          1L, request.getHeader("X-Forwarded-For"), user.getUserId());
      res.setErrorCode(1L);
      res.setResult(CommonConstant.R_FAIL_CODE);
      return res;

    } else {
      user.setIdx(readOnlyMapper.selectUserIdx(user.getUserId()));
      mapper.addAppDeviceInfo(user);

      ResponseLoginModel resInfo = readOnlyMapper.selectResLoginInfo(user.getUserId());
      res.setStationName(resInfo.getStationName());
      res.setStationNo(resInfo.getStationNo());
      res.setRegionId(resInfo.getRegionId());
      res.setRegionName(resInfo.getRegionName());

      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseGroupLoginModel appGroupLogin(AppUser user, HttpServletRequest request) {
    ResponseGroupLoginModel res = new ResponseGroupLoginModel();

    user.setUserPw(Sha256EncryptUtil.ShaEncoder(user.getUserPw()));

    if (readOnlyMapper.groupUserMatchCheck(user) == 0) {
      logger.error(
          "Call appGroupLogin API FAIL : [ ErrorCode : {} ErrorMsg : please User Match Check, ClientIp : {}, userId : {} ]",
          1L, request.getHeader("X-Forwarded-For"), user.getUserId());
      res.setErrorCode(1L);
      res.setResult(CommonConstant.R_FAIL_CODE);

    } else {

      List<Map<String, Object>> datas = new ArrayList<>();

      for (String userId : readOnlyMapper.appSelectGroupMember(user.getUserId())) {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Map<String, Object>> deviceDatas = new ArrayList<>();
        for (HashMap<String, Object> device : readOnlyMapper.appSelectMemberDevice(userId)) {
          Map<String, Object> deviceMp = new LinkedHashMap<>();
          deviceMp.put("serial_num", device.get("serial_num"));
          if (device.get("vent_yn").equals("1")) {
            deviceMp.put("vent_serial_num",
                readOnlyMapper.appSelectMemberVentDevice(device.get("serial_num").toString()));
          }

          deviceDatas.add(deviceMp);
        }

        data.put("user_id", userId);
        data.put("device_data", deviceDatas);

        datas.add(data);
      }

      res.setUsers(datas);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);
    }

    return res;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public ResponseModel appLogout(AppUser user) {
    ResponseModel res = new ResponseModel();

    mapper.delAppDeviceInfo(user.getAppDeviceImei());
    res.setErrorCode(0L);
    res.setResult(CommonConstant.R_SUCC_CODE);

    return res;
  }

  public ResponseModel findId(AppUser user, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      String userId = readOnlyMapper.findId(user.getStationNo());

      if (userId.isEmpty()) {
        logger.error(
            "Call findId API FAIL : [ ErrorCode : {} ErrorMsg : please User Id Check, ClientIp : {}, userId : {} ]",
            2L, request.getHeader("X-Forwarded-For"), user.getUserId());
        res.setErrorCode(2L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;

      } else {
        res.setId(userId);
        res.setErrorCode(0L);
        res.setResult(CommonConstant.R_SUCC_CODE);
      }

    } catch (RuntimeException e) {
      logger.error(
          "Call findId API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {}, userId : {} ]",
          1L, request.getHeader("X-Forwarded-For"), user.getUserId());
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(findId) KIOT");
    }

    return res;
  }

  public ResponseModel findPwd(AppUser user, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      int findUser = readOnlyMapper.findPw(user);

      if (findUser == 0) {
        logger.error(
            "Call findPwd API FAIL : [ ErrorCode : {} ErrorMsg : please User Match Check, ClientIp : {}, userId : {} ]",
            1L, request.getHeader("X-Forwarded-For"), user.getUserId());
        res.setErrorCode(1L);
        res.setResult(CommonConstant.R_FAIL_CODE);
        return res;

      } else {
        res.setErrorCode(0L);
        res.setResult(CommonConstant.R_SUCC_CODE);

      }

    } catch (RuntimeException e) {
      logger.error(
          "Call findPwd API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {}, userId : {} ]",
          9L, request.getHeader("X-Forwarded-For"), user.getUserId());
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(findPwd) KIOT");
    }

    return res;
  }

  public ResponseModel chgPasswd(AppUser user, HttpServletRequest request) {
    ResponseModel res = new ResponseModel();

    try {

      user.setUserPw(Sha256EncryptUtil.ShaEncoder(user.getUserPw()));

      mapper.chgPasswd(user);
      res.setErrorCode(0L);
      res.setResult(CommonConstant.R_SUCC_CODE);

    } catch (RuntimeException e) {
      logger.error(
          "Call chgPasswd API FAIL : [ ErrorCode : {} ErrorMsg : RuntimeException, ClientIp : {} ]",
          9L, request.getHeader("X-Forwarded-For"));
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw new RuntimeException("(chgPasswd) KIOT");
    }

    return res;
  }
}
